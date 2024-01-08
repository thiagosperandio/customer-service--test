package com.example.api.repository;

import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.api.domain.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	@EntityGraph(value = "customer-entity-graph")
	@Override
	Optional<Customer> findById(Long id);

	@EntityGraph(value = "customer-entity-graph")
	@Override
	<S extends Customer> Page<S> findAll(Example<S> example, Pageable pageable);

}
