package com.example.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.api.clients.CepApiClient;
import com.example.api.domain.Address;
import com.example.api.domain.Customer;
import com.example.api.dto.client.CepAddressClientDTO;
import com.example.api.dto.model.AddressInsertDTO;
import com.example.api.dto.model.AddressViewerDTO;
import com.example.api.exception.BusinessException;
import com.example.api.repository.AddressRepository;
import com.example.api.util.ObjectUtil;

import feign.FeignException.BadRequest;
import feign.FeignException.NotFound;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressService {

	private final AddressRepository repository;
	private final CepApiClient cepClient;

	public List<Address> findByCustomerId(Long customerId) {
		return repository.findByCustomerId(customerId);
	}

	public Optional<Address> findById(Long id) {
		return repository.findById(id);
	}

	@Transactional(rollbackFor = Exception.class)
	public Address insertAddress(@Valid @NotNull AddressInsertDTO addressDTO) {
		Address address = mapFrom(addressDTO);
		if (address.getId() != null) {
			throw new BusinessException("ID do objeto está preenchido, não é permitido informar ID.");
		}
		address = save(address);

		return address;
	}

	@Transactional(rollbackFor = Exception.class)
	public Address updateAddress(Long id, @Valid @NotNull AddressInsertDTO addressDTO) {
		Address address = mapFrom(addressDTO);
		validateUpdate(id, address);

		repository.findById(address.getId()).ifPresentOrElse(
				addressFinded -> save(address),
				() -> new BusinessException("Address não encontrado no sistema"));

		return repository.findById(id)
				.orElseThrow(() -> new BusinessException("Address not exists"));
	}

	private Address mapFrom(AddressInsertDTO addressInsertDTO) {
		CepAddressClientDTO cepClientDTO = findCep(addressInsertDTO.getZipCode());

		Customer customer = Customer.builder()
				.id(addressInsertDTO.getCustomerId())
				.build();
		String complement = Stream.of(
				addressInsertDTO.getAddressComplement(),
				cepClientDTO.getAddressComplement())
				.filter(ObjectUtil::isNotNullAndNotEmpty)
				.collect(Collectors.joining(", "));

		return Address.builder()
				.id(addressInsertDTO.getId())
				.customer(customer)
				.zipCode(ObjectUtil.getOnlyNumbers(addressInsertDTO.getZipCode()))
				.addressLine(cepClientDTO.getAddressLine())
				.addressNumber(addressInsertDTO.getAddressNumber())
				.addressComplement(complement)
				.district(cepClientDTO.getDistrict())
				.city(cepClientDTO.getCity())
				.stateAbbreviation(cepClientDTO.getStateAbbreviation())
				.ibgeCode(cepClientDTO.getIbgeCode())
				.giaCode(cepClientDTO.getGiaCode())
				.dddCode(cepClientDTO.getDddCode())
				.siafiCode(cepClientDTO.getSiafiCode())
				.build();
	}

	private Address mapFrom(AddressViewerDTO addressDTO) {
		Customer customer = Customer.builder()
				.id(addressDTO.getCustomerId())
				.build();

		return Address.builder()
				.id(addressDTO.getId())
				.customer(customer)
				.zipCode(addressDTO.getZipCode())
				.addressLine(addressDTO.getAddressLine())
				.addressNumber(addressDTO.getAddressNumber())
				.addressComplement(addressDTO.getAddressComplement())
				.district(addressDTO.getDistrict())
				.city(addressDTO.getCity())
				.stateAbbreviation(addressDTO.getStateAbbreviation())
				.ibgeCode(addressDTO.getIbgeCode())
				.giaCode(addressDTO.getGiaCode())
				.dddCode(addressDTO.getDddCode())
				.siafiCode(addressDTO.getSiafiCode())
				.build();
	}

	private AddressViewerDTO mapFrom(Address address) {
		return AddressViewerDTO.builder()
				.id(address.getId())
				.customerId(address.getId())
				.zipCode(address.getZipCode())
				.addressLine(address.getAddressLine())
				.addressNumber(address.getAddressNumber())
				.addressComplement(address.getAddressComplement())
				.district(address.getDistrict())
				.city(address.getCity())
				.stateAbbreviation(address.getStateAbbreviation())
				.ibgeCode(address.getIbgeCode())
				.giaCode(address.getGiaCode())
				.dddCode(address.getDddCode())
				.siafiCode(address.getSiafiCode())
				.build();
	}

	private void validateUpdate(Long id, Address address) {
		List<String> errors = new ArrayList<>();
		if (Stream.of(id, address.getId()).anyMatch(Objects::isNull)) {
			errors.add("ID está nulo");
		}
		if (!Objects.equals(id, address.getId())) {
			errors.add("ID do objeto está diferente do ID da rota");
		}
		if (!errors.isEmpty()) {
			throw new BusinessException(errors);
		}
	}

	/**
	 * Neste address, estamos salvando todos os dados tanto no insert como no
	 * update, claro, só impactando nos dados que podem ser mapeados como
	 * atualizáveis. <br>
	 * Caso queira separe a lógica do insert e do update.
	 */
	private Address save(Address address) {
		address = repository.save(address);
		return address;
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteAddress(Long id) {
		if (id == null) {
			throw new BusinessException("ID está nulo");
		}
		repository.deleteById(id);
	}

	public CepAddressClientDTO findCep(String cep) {
		try {
			var cepClientDTO = cepClient.getCep(ObjectUtil.getOnlyNumbers(cep));
			if (cepClientDTO == null || cepClientDTO.getZipCode() == null) {
				throw new BusinessException("CEP não encontrado");
			}
			return cepClientDTO;
		} catch (BadRequest | NotFound e) {
			log.error("CEP não encontrado", e);
			throw new BusinessException("CEP não encontrado", e);
		}
	}

}
