package com.example.api.dto.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressInsertDTO {

	private Long id;

	@NotNull
	private Long customerId;

	@NotBlank
	@Size(min = 8, max = 8)
	private String zipCode;

	@NotBlank
	private String addressNumber;

	private String addressComplement;

}
