package com.example.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.api.domain.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
