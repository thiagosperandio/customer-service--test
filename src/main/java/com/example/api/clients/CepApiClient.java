package com.example.api.clients;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.api.dto.client.CepAddressClientDTO;

@FeignClient(name = "cepApiClient", url = "${clients.url.cep}")
public interface CepApiClient {

	/**
	 * @return o endereço do CEP informado, ou uma exceção do tipo
	 *         {@code FeignException$BadRequest}
	 */
	@GetMapping("/ws/{cep}/json/")
	public CepAddressClientDTO getCep(@NotBlank @Size(min = 8, max = 8) @PathVariable String cep);

}
