package com.example.api.dto.client;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CepClientDTO {
	@JsonAlias("cep")
	private String zipCode;

	@JsonAlias("logradouro")
	private String addressLine;

	@JsonAlias("complemento")
	private String addressComplement;

	@JsonAlias("bairro")
	private String district;

	@JsonAlias("localidade")
	private String city;

	@JsonAlias("uf")
	private String stateAbbreviation;

	@JsonAlias("ibge")
	private String ibgeCode;

	@JsonAlias("gia")
	private String giaCode;

	@JsonAlias("ddd")
	private String dddCode;

	@JsonAlias("siafi")
	private String siafiCode;
}
