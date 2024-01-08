package com.example.api.validators;

import java.util.List;
import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.example.api.validators.annotations.Gender;

public class GenderValidator implements ConstraintValidator<Gender, String> {

	private static final String FEMININO = "F";
	private static final String MASCULINO = "M";

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		value = Optional.ofNullable(value).orElse("");
		return List.of(MASCULINO, FEMININO).contains(value);
	}

}
