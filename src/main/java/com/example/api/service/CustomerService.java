package com.example.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.api.domain.Customer;
import com.example.api.dto.model.CustomerDTO;
import com.example.api.exception.BusinessException;
import com.example.api.repository.CustomerRepository;

@Service
public class CustomerService {

	private static final int MIN_CHAR_VALID_NAME = 10;

	private final CustomerRepository repository;

	public CustomerService(CustomerRepository repository) {
		this.repository = repository;
	}

	public Page<Customer> findAll(String nameLike, String emailLike, String gender, Pageable pageable) {

		var customerSearchModel = Customer.builder()
				.name(nameLike)
				.email(emailLike)
				.gender(gender)
				.build();

		ExampleMatcher customerSearchExampleMatcher = ExampleMatcher
				.matchingAll()
				.withIgnoreCase()
				.withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains())
				.withMatcher("email", ExampleMatcher.GenericPropertyMatchers.contains())
				.withMatcher("gender", ExampleMatcher.GenericPropertyMatchers.exact());

		Example<Customer> exampleOfCustomer = Example.of(customerSearchModel, customerSearchExampleMatcher);

		return repository.findAll(exampleOfCustomer, pageable);
	}

	public Optional<Customer> findById(Long id) {
		return repository.findById(id);
	}

	@Transactional(rollbackFor = Exception.class)
	public Customer insertCustomer(@Valid @NotNull CustomerDTO customerDTO) {
		if (customerDTO.getId() != null) {
			throw new BusinessException("ID do objeto está preenchido, não é permitido informar ID.");
		}
		Customer customer = mapFrom(customerDTO);
		customer = save(customer);

		return customer;
	}

	@Transactional(rollbackFor = Exception.class)
	public Customer updateCustomer(Long id, @Valid @NotNull CustomerDTO customerDTO) {
		Customer customer = mapFrom(customerDTO);
		validateUpdate(id, customer);

		repository.findById(customer.getId()).ifPresentOrElse(
				customerFinded -> {
					if (customer.getName().length() >= MIN_CHAR_VALID_NAME) {
						save(customer);
					} else {
						throw new BusinessException("Não é permitido alterar o nome '" + customerFinded.getName()
								+ "', que iria mudar para '" + customer.getName()
								+ "', pois para atualizar é necessário possui mais do que " + MIN_CHAR_VALID_NAME
								+ " caracteres !!!");
					}
				},
				() -> new BusinessException("Customer não encontrado no sistema"));

		return repository.findById(id)
				.orElseThrow(() -> new BusinessException("Customer not exists"));
	}

	private Customer mapFrom(CustomerDTO customerDTO) {
		return Customer.builder()
				.id(customerDTO.getId())
				.name(customerDTO.getName())
				.email(customerDTO.getEmail())
				.gender(customerDTO.getGender())
				.build();
	}

	private void validateUpdate(Long id, Customer customer) {
		List<String> errors = new ArrayList<>();
		if (List.of(id, customer.getId()).stream().anyMatch(Objects::isNull)) {
			errors.add("ID está nulo");
		}
		if (!Objects.equals(id, customer.getId())) {
			errors.add("ID do objeto está diferente do ID da rota");
		}
		if (!errors.isEmpty()) {
			throw new BusinessException(errors);
		}
	}

	/**
	 * Neste customer, estamos salvando todos os dados tanto no insert como no
	 * update, claro, só impactando nos dados que podem ser mapeados como
	 * atualizáveis. <br>
	 * Caso queira separe a lógica do insert e do update.
	 */
	private Customer save(Customer customer) {
		customer = repository.save(customer);
		return customer;
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteCustomer(Long id) {
		if (id == null) {
			throw new BusinessException("ID está nulo");
		}
		repository.deleteById(id);
	}

}
