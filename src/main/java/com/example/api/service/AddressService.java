package com.example.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.api.clients.CepApiClient;
import com.example.api.domain.Address;
import com.example.api.domain.Customer;
import com.example.api.dto.client.CepClientDTO;
import com.example.api.dto.model.AddressDTO;
import com.example.api.exception.BusinessException;
import com.example.api.repository.AddressRepository;

import lombok.RequiredArgsConstructor;

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
	public Address insertAddress(@Valid @NotNull AddressDTO addressDTO) {
		Address address = mapFrom(addressDTO);
		if (address.getId() != null) {
			throw new BusinessException("ID do objeto está preenchido, não é permitido informar ID.");
		}
		address = save(address);

		return address;
	}

	@Transactional(rollbackFor = Exception.class)
	public Address updateAddress(Long id, @Valid @NotNull AddressDTO addressDTO) {
		Address address = mapFrom(addressDTO);
		validateUpdate(id, address);

		repository.findById(address.getId()).ifPresentOrElse(
				addressFinded -> save(address),
				() -> new BusinessException("Address não encontrado no sistema"));

		return repository.findById(id)
				.orElseThrow(() -> new BusinessException("Address not exists"));
	}

	private Address mapFrom(AddressDTO addressDTO) {
		return Address.builder()
				.id(addressDTO.getId())
				.customer(Customer.builder()
						.id(addressDTO.getCustomerId())
						.build())
				.zipCode(addressDTO.getZipCode())
				.addressLine(addressDTO.getAddressLine())
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

	private void validateUpdate(Long id, Address address) {
		List<String> errors = new ArrayList<>();
		if (List.of(id, address.getId()).stream().anyMatch(Objects::isNull)) {
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

	public CepClientDTO findCep(Long cep) {
		return cepClient.getCep(cep);
	}

}
