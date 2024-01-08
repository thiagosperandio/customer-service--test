package com.example.api.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.api.dto.client.CepClientDTO;

@FeignClient(name = "cepApiClient", url = "${clients.url.cep}")
public interface CepApiClient {

	@GetMapping("/ws/{cep}/json/")
	public CepClientDTO getCep(@PathVariable Long cep);

}
