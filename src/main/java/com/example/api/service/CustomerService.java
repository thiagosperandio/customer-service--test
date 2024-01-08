package com.example.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.api.domain.Address;
import com.example.api.domain.Customer;
import com.example.api.dto.model.CustomerDTO;
import com.example.api.exception.BusinessException;
import com.example.api.repository.CustomerRepository;
import com.example.api.util.ObjectUtil;

@Service
public class CustomerService {

	private static final int MIN_CHAR_VALID_NAME = 10;

	private final CustomerRepository repository;

	public CustomerService(CustomerRepository repository) {
		this.repository = repository;
	}

	public Page<Customer> findAll(String nameLike, String emailLike, String gender, String cityLike, String state,
			Pageable pageable) {

		if (ObjectUtil.isNotNullAndNotEmptyAnyElements(cityLike, state)) {
			return getSpecificationOfCustomer(nameLike, emailLike, gender, cityLike, state, pageable);

		} else {
			return getExampleOfCustomer(nameLike, emailLike, gender, pageable);
		}
	}

	private Page<Customer> getSpecificationOfCustomer(String nameLike, String emailLike, String gender, String cityLike,
			String state, Pageable pageable) {
		var specification = (Specification<Customer>) (root, query, criteriaBuilder) -> {

			List<Predicate> predicates = new ArrayList<>();

			if (ObjectUtil.isNotNullAndNotEmpty(nameLike)) {
				predicates.add(criteriaBuilder.and(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
						"%" + nameLike.toLowerCase() + "%")));
			}

			if (ObjectUtil.isNotNullAndNotEmpty(emailLike)) {
				predicates.add(criteriaBuilder.and(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")),
						"%" + emailLike.toLowerCase() + "%")));
			}

			if (ObjectUtil.isNotNullAndNotEmpty(gender)) {
				predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("gender"), gender)));
			}

			Join<Customer, Address> addressJoin = root.join("addresses", JoinType.LEFT);

			if (ObjectUtil.isNotNullAndNotEmpty(cityLike)) {
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(addressJoin.get("city")),
						"%" + cityLike.toLowerCase() + "%"));
			}

			if (ObjectUtil.isNotNullAndNotEmpty(state)) {
				predicates.add(criteriaBuilder.equal(addressJoin.get("stateAbbreviation"), state));
			}

			query.distinct(true);
			return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
		};
		return repository.findAll(specification, pageable);
	}

	private Page<Customer> getExampleOfCustomer(String nameLike, String emailLike, String gender, Pageable pageable) {
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
		if (Stream.of(id, customer.getId()).anyMatch(Objects::isNull)) {
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
