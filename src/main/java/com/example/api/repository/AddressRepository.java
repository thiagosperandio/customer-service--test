package com.example.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.api.domain.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {

	List<Address> findByCustomerId(Long customerId);

}
