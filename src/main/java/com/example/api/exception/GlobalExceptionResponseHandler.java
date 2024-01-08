package com.example.api.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import com.example.api.dto.exception.ExceptionResponseDTO;
import com.example.api.util.ExceptionUtil;
import com.example.api.util.LoggerUtil;
import com.example.api.util.ObjectUtil;
import com.fasterxml.jackson.databind.JsonMappingException;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionResponseHandler {

	@Value("${app.show-exception-response}")
	private boolean isShowExceptionResponse;

	private final MessageSource messageSource;

	/***********************************************************************************************************
	 * Exceções com Status Response dinâmico, abaixo (HttpServletResponse feito pelo
	 * próprio client response): *
	 ***********************************************************************************************************/

	/**
	 * Lidando a exceção do Spring Feign Client (Origin exception type for all Http
	 * Apis.)
	 */
	@ExceptionHandler(FeignException.class)
	public ExceptionResponseDTO handleFeignStatusException(FeignException exception, HttpServletRequest request,
			HttpServletResponse response) {
		var httpStatus = HttpStatus.valueOf(exception.status());
		response.setStatus(httpStatus.value());

		StringBuilder msg = new StringBuilder("Erro ao acessar dados da Unimed Goiânia (API). ")
				.append("Informações retornadas: ")
				.append(Optional.ofNullable(exception.contentUTF8())
						.map(ObjectUtil::getOrNull)
						.orElse(exception.getLocalizedMessage()));

		return makeDefaultResponse(exception, msg.toString(), request, httpStatus);
	}

	/**
	 * Lidando a exceção do Spring (Base class for exceptions associated with
	 * specific HTTP response status codes.)
	 */
	@ExceptionHandler(ResponseStatusException.class)
	public ExceptionResponseDTO handleResponseStatus(ResponseStatusException exception, HttpServletRequest request,
			HttpServletResponse response) {
		var httpStatus = exception.getStatus();
		response.setStatus(httpStatus.value());
		return makeDefaultResponse(exception, null, request, httpStatus);
	}

	/**
	 * Exceção de negócio, que pode ter uma lista de erros a ser retornada ao
	 * frontend, caso tenha coletado uma a uma mensagem de exceção nos services.
	 */
	@ExceptionHandler(BusinessException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ExceptionResponseDTO handleBusiness(BusinessException exception, HttpServletRequest request) {
		var httpStatus = exception.getStatus();
		return makeDefaultResponse(exception, null, request, httpStatus);
	}

	/***********************************************************************************************************************************
	 * Exceções com Status Response estático, abaixo (HttpServletResponse definido
	 * pela anotação @ResponseStatus nos próprios métodos): *
	 ***********************************************************************************************************************************/

	/**
	 * Exceção usada para a autenticação do sistema.
	 */
	@ExceptionHandler(AuthenticationException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ExceptionResponseDTO handleAccessDenied(AuthenticationException exception, HttpServletRequest request) {
		return makeDefaultResponse(exception, null, request, HttpStatus.UNAUTHORIZED);
	}

	/**
	 * Exceção usada para a autenticação do sistema.
	 */
	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ExceptionResponseDTO handleAccessDenied(AccessDeniedException exception, HttpServletRequest request) {
		return makeDefaultResponse(exception, null, request, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler({ IllegalArgumentException.class, IllegalStateException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ExceptionResponseDTO handleIllegalArgument(RuntimeException exception, HttpServletRequest request) {
		return makeDefaultResponse(exception, null, request, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ HttpRequestMethodNotSupportedException.class })
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	public ExceptionResponseDTO handleMethodNotSupported(HttpRequestMethodNotSupportedException exception,
			HttpServletRequest request) {
		return makeDefaultResponse(exception, null, request, HttpStatus.METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler(BindException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ExceptionResponseDTO handleBindValidation(BindException exception, HttpServletRequest request) {
		return makeDefaultResponse(exception, null, request, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ExceptionResponseDTO handleBusiness(ConstraintViolationException exception, HttpServletRequest request) {
		List<String> erros = new ArrayList<>();
		for (var constraintEx : exception.getConstraintViolations()) {
			erros.add(constraintEx.getPropertyPath() + ": " + constraintEx.getMessage());
		}
		return makeDefaultResponse(exception, erros.toString(), request, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ExceptionResponseDTO handleHttpMessageNotReadable(HttpMessageNotReadableException exception,
			HttpServletRequest request) {
		var msg = "Não foi possível ler o BODY request";
		return makeDefaultResponse(exception, msg, request, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ JsonMappingException.class, HttpMediaTypeNotSupportedException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ExceptionResponseDTO handleMappingType(Exception exception, HttpServletRequest request) {
		return makeDefaultResponse(exception, null, request, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	public ExceptionResponseDTO handleValidationError(MethodArgumentNotValidException exception,
			HttpServletRequest request) {
		var errorMessage = new ArrayList<String>();
		for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
			String message = Optional.ofNullable(messageSource.getMessage(fieldError, LocaleContextHolder.getLocale()))
					.map(ObjectUtil::getOrNull)
					.orElse(fieldError.getDefaultMessage());
			errorMessage.add("Campo '" + fieldError.getField() + "': " + message + ", valor informado: '"
					+ fieldError.getRejectedValue() + "'");
		}
		for (ObjectError objError : exception.getBindingResult().getGlobalErrors()) {
			String message = Optional.ofNullable(messageSource.getMessage(objError, LocaleContextHolder.getLocale()))
					.map(ObjectUtil::getOrNull)
					.orElse(objError.getDefaultMessage());
			errorMessage.add(objError.getObjectName() + ": " + message);
		}

		String errorMsgJoined = errorMessage.stream().collect(Collectors.joining(". "));
		return makeDefaultResponse(exception, errorMsgJoined, request, HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler({ NoSuchElementException.class, EntityNotFoundException.class,
			MissingPathVariableException.class })
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ExceptionResponseDTO handleNoSuchElementFoundException(Exception exception, HttpServletRequest request) {
		return makeDefaultResponse(exception, null, request, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ExceptionResponseDTO missingServletRequestParameter(MissingServletRequestParameterException exception,
			HttpServletRequest request) {
		return makeDefaultResponse(exception, null, request, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ExceptionResponseDTO handleServerError(Exception exception, HttpServletRequest request) {
		var msg = "Erro inesperado, contatar o administrador do sistema com o codigo: " + UUID.randomUUID();
		return makeDefaultResponse(exception, msg, request, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 *
	 * @param exception
	 * @param responseMessage
	 * @param request
	 * @param httpStatus
	 * @return
	 */
	private ExceptionResponseDTO makeDefaultResponse(Exception exception, String responseMessage,
			HttpServletRequest request, HttpStatus httpStatus) {
		boolean printStackTrace = ExceptionUtil.needPrintStackTrace(exception);
		if (ObjectUtil.isNullOrEmpty(responseMessage)) {
			responseMessage = exception.getLocalizedMessage();
		}
		var errorResponse = new ExceptionResponseDTO(
				httpStatus, request, responseMessage,
				ExceptionUtil.getErrorMessage(exception, isShowExceptionResponse),
				ExceptionUtil.getErrorStackTrace(exception, isShowExceptionResponse));
		log(exception, printStackTrace, errorResponse);
		return errorResponse;
	}

	/**
	 *
	 * @param exception
	 * @param body
	 * @param printStackTrace
	 * @param errorResponse
	 */
	private void log(Exception exception, boolean printStackTrace, ExceptionResponseDTO errorResponse) {
		LoggerUtil.logError(exception, printStackTrace, log, errorResponse.toString());
	}

}
