package com.example.api.web.rest;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.api.domain.Customer;
import com.example.api.service.CustomerService;

@RestController
@RequestMapping("/customers")
public class CustomerController {

	private final CustomerService service;

	public CustomerController(CustomerService service) {
		this.service = service;
	}

	@GetMapping
	public Page<Customer> findAll(
			@RequestParam(name = "name-like", required = false) String nameLike,
			@RequestParam(name = "email-like", required = false) String emailLike,
			@RequestParam(name = "gender-char", required = false) String gender,
			@PageableDefault(sort = "name", direction = Direction.ASC) Pageable pageable) {
		return service.findAll(nameLike, emailLike, gender, pageable);
	}

	@GetMapping("/{id}")
	public Customer findById(@PathVariable Long id) {
		return service.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
	}

	@PostMapping("/")
	public ResponseEntity<Customer> insert(@RequestBody @Valid Customer input) {
		var response = service.insertCustomer(input);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Customer> update(@PathVariable Long id, @RequestBody @Valid Customer input) {
		var response = service.updateCustomer(id, input);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		service.deleteCustomer(id);
		return ResponseEntity.ok().build();
	}

}
