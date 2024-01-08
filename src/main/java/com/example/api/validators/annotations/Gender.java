package com.example.api.validators.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.example.api.validators.GenderValidator;

@Documented
@Constraint(validatedBy = GenderValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Gender {

	String message() default "{GenderInvalidMessage}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
