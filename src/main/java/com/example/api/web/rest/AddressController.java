package com.example.api.web.rest;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.api.domain.Address;
import com.example.api.dto.client.CepAddressClientDTO;
import com.example.api.dto.model.AddressInsertDTO;
import com.example.api.service.AddressService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/addresses")
public class AddressController {

	private final AddressService service;

	@GetMapping("/{id}")
	public Address findById(@PathVariable Long id) {
		return service.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address não encontrado"));
	}

	@GetMapping("/customer/{customerId}")
	public List<Address> findByCustomerId(@PathVariable Long customerId) {
		return service.findByCustomerId(customerId);
	}

	@PostMapping("/")
	public ResponseEntity<Address> insert(@RequestBody @Valid AddressInsertDTO input) {
		var response = service.insertAddress(input);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Address> update(@PathVariable Long id, @RequestBody @Valid AddressInsertDTO input) {
		var response = service.updateAddress(id, input);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		service.deleteAddress(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/cep-api/{cep}")
	public CepAddressClientDTO findCep(@PathVariable String cep) {
		return service.findCep(cep);
	}

}
