package com.example.api.dto.exception;

import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@AllArgsConstructor
public class ExceptionResponseDTO {

	private LocalDateTime timestamp;
	private Integer status;
	private String error;
	private String message;
	private String path;

	@ToString.Exclude
	private List<String> stackTrace;

	public ExceptionResponseDTO(@NotNull HttpStatus httpStatus, @NotNull HttpServletRequest request,
			@NotBlank String message, @Nullable String additionalErrorMessage, List<String> stackTrace) {
		this.timestamp = LocalDateTime.now();
		this.status = httpStatus.value();
		this.message = message;
		this.path = request.getServletPath();
		this.error = httpStatus.name() + additionalErrorMessage;
		this.stackTrace = stackTrace;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder()
				.append("{ \"ExceptionResponseDTO\": { \"timestamp\": \"")
				.append(timestamp)
				.append("\", status\": \"")
				.append(status)
				.append("\", error\": \"")
				.append(error)
				.append("\", message\": \"")
				.append(message)
				.append("\", path\": \"")
				.append(path)
				.append("] } }");
		return builder.toString();
	}

}
