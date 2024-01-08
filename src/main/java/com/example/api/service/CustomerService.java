package com.example.api.service;

import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.api.domain.Customer;
import com.example.api.repository.CustomerRepository;

@Service
public class CustomerService {

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

}
