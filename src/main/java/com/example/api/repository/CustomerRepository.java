package com.example.api.repository;

import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;

import com.example.api.domain.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {

	@EntityGraph(value = "customer-entity-graph")
	@Override
	Optional<Customer> findById(Long id);

	@EntityGraph(value = "customer-entity-graph")
	@Override
	<S extends Customer> Page<S> findAll(Example<S> example, Pageable pageable);

	@EntityGraph(value = "customer-entity-graph")
	@Override
	Page<Customer> findAll(@Nullable Specification<Customer> spec, Pageable pageable);

}
