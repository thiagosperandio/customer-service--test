package com.example.api.web.rest;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.api.dto.client.CepClientDTO;
import com.example.api.service.AddressService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/addresses")
public class AddressController {

	private final AddressService service;

	@GetMapping("/cep-api/{cep}")
	public CepClientDTO findCep(@PathVariable Long cep) {
		return Optional.ofNullable(service.findCep(cep))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CEP not found"));
	}

}
