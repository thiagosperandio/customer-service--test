package com.example.api.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import lombok.Getter;

public class BusinessException extends ResponseStatusException {

	/**
	 *
	 */
	private static final HttpStatus defaultStatus = HttpStatus.BAD_REQUEST;

	private static final long serialVersionUID = 1L;

	@Getter
	private final List<String> errors = new ArrayList<>();

	public BusinessException(List<String> errors) {
		super(defaultStatus);
		this.errors.addAll(errors);
	}

	public BusinessException(List<String> errors, Throwable cause) {
		super(defaultStatus, defaultStatus.getReasonPhrase(), cause);
		this.errors.addAll(errors);
	}

	public BusinessException(String message) {
		super(defaultStatus, message);
	}

	public BusinessException(Throwable cause) {
		super(defaultStatus, defaultStatus.getReasonPhrase(), cause);
	}

	public BusinessException(String message, Throwable cause) {
		super(defaultStatus, message, cause);
	}

	public BusinessException(HttpStatus httpStatus, String message, Throwable cause) {
		super(httpStatus, message, cause);
	}

	public BusinessException(HttpStatus httpStatus, Throwable cause) {
		super(httpStatus, httpStatus.getReasonPhrase(), cause);
	}

	public BusinessException(HttpStatus httpStatus, String message) {
		super(httpStatus, message);
	}

	/**
	 * Returns the detail message string of this throwable.
	 *
	 * @return the detail message string of this {@code Throwable} instance (which
	 *         may be {@code null}).
	 */
	@Override
	public String getMessage() {
		if (errors.isEmpty()) {
			errors.add(super.getMessage());
		}
		return errors.stream()
				.distinct()
				.filter(Objects::nonNull)
				.collect(Collectors.joining(". "));
	}

	/**
	 * Creates a localized description of this throwable. Subclasses may override
	 * this method in order to produce a locale-specific message. For subclasses
	 * that do not override this method, the default implementation returns the same
	 * result as {@code getMessage()}.
	 *
	 * @return The localized description of this throwable.
	 * @since 1.1
	 */
	@Override
	public String getLocalizedMessage() {
		return getMessage();
	}

}
