package com.example.api.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ADDRESS")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@NamedEntityGraph(name = "address-entity-graph", attributeNodes = { @NamedAttributeNode("customer") })
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_Customer", nullable = false)
	@NotNull
	private Customer customer;

	@Column(nullable = false, length = 8)
	@NotBlank
	@Size(min = 8, max = 8)
	private String zipCode;

	@Column(nullable = false, length = 255)
	@NotBlank
	private String addressLine;

	@Column(nullable = false, length = 255)
	@NotBlank
	private String addressNumber;

	@Column(nullable = false, length = 255)
	private String addressComplement;

	@Column(nullable = false, length = 255)
	@NotBlank
	private String district;

	@Column(nullable = false, length = 255)
	@NotBlank
	private String city;

	@Column(nullable = false, length = 2)
	@NotBlank
	@Size(min = 2, max = 2)
	private String stateAbbreviation;

	@Column(nullable = false, length = 255)
	private String ibgeCode;

	@Column(nullable = false, length = 255)
	private String giaCode;

	@Column(nullable = false, length = 2)
	@Size(min = 2, max = 2)
	private String dddCode;

	@Column(nullable = false, length = 255)
	private String siafiCode;

}
