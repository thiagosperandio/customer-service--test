package com.example.api.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.example.api.exception.BusinessException;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionUtil {

	/**
	 *
	 * @param exception
	 * @returns true if the exception has a cause, or if the class is neither
	 *          UserException nor BusinessException, false otherwise
	 */
	public static final boolean needPrintStackTrace(Throwable exception) {
		return exception.getCause() != null || !(exception instanceof BusinessException);
	}

	/**
	 *
	 * @param exception
	 * @param checkIfReturnExceptionMsg boolean to control externally if this method
	 *                                  returns exception message or null
	 * @return
	 */
	public static final String getErrorMessage(Throwable exception, boolean checkIfReturnExceptionMsg) {
		if (checkIfReturnExceptionMsg) {
			Optional<Throwable> exceptionOpt = Optional.ofNullable(exception);
			StringBuilder msgReturn = new StringBuilder();
			if (exceptionOpt.isPresent()) {
				msgReturn
						.append(" Exception type: '")
						.append(exceptionOpt.map(Throwable::getClass).map(Class::getSimpleName).orElse("(no class)"))
						.append("', message: '")
						.append(exceptionOpt.map(Throwable::getLocalizedMessage).orElse("(no message found)"))
						.append("', cause: '")
						.append(exceptionOpt.map(ExceptionUtil::addCausedBy).orElse("(no cause found)"))
						.append("'. ");
			}
			return msgReturn.toString();
		}
		return "";
	}

	/**
	 *
	 * @param exception
	 * @return
	 */
	public static final String getErrorMessage(Throwable exception) {
		return getErrorMessage(exception, true);
	}

	/**
	 *
	 * @param exception
	 * @return a new String with all caused-by texts
	 */
	public static final String addCausedBy(Throwable exception) {
		return addCausedBy(exception, new StringBuilder())
				.toString();
	}

	/**
	 *
	 * @param exception
	 * @param stackTrace
	 * @return the StringBuilder param filled with all caused-by texts
	 */
	public static final StringBuilder addCausedBy(Throwable exception, StringBuilder stringBuilder) {
		if (exception != null && exception.getCause() != null) {
			stringBuilder
					.append(", cause: ")
					.append(exception.getCause());
			addCausedBy(exception.getCause(), stringBuilder);
		}
		return stringBuilder;
	}

	/**
	 *
	 * @param exception
	 * @return a new String with the exception and all caused-by names
	 */
	public static final String getExceptionAndCausedByNames(Throwable exception) {
		return getCausedByNames(exception, new StringBuilder(exception.getClass().getSimpleName()))
				.toString();
	}

	/**
	 *
	 * @param exception
	 * @param stackTrace
	 * @return the StringBuilder param filled with all caused-by texts
	 */
	public static final StringBuilder getCausedByNames(Throwable exception, StringBuilder stringBuilder) {
		if (exception != null && exception.getCause() != null) {
			stringBuilder
					.append(", cause: ")
					.append(exception.getCause().getClass().getSimpleName());
			getCausedByNames(exception.getCause(), stringBuilder);
		}
		return stringBuilder;
	}

	/**
	 *
	 * @param exception
	 * @param showStackTrace
	 * @return
	 */
	public static final List<String> getErrorStackTrace(Throwable exception, boolean showStackTrace) {
		List<String> stackTrace = new ArrayList<>();
		if (showStackTrace) {
			addTrace(exception, stackTrace);
		}
		return stackTrace;
	}

	/**
	 *
	 * @param exception
	 * @param stackTrace
	 */
	public static final void addTrace(Throwable exception, List<String> stackTrace) {
		if (exception == null) {
			return;
		}
		stackTrace.add(new StringBuilder(exception.getClass().getName())
				.append(": ")
				.append(exception.getLocalizedMessage())
				.toString());
		String atStackString = "  at ";
		if (exception.getStackTrace() != null) {
			stackTrace.addAll(Stream.of(exception.getStackTrace())
					.map(StackTraceElement::toString)
					.map(atStackString::concat)
					.collect(Collectors.toList()));
		}
		if (exception.getCause() != null) {
			stackTrace.add("Caused by: ");
			addTrace(exception.getCause(), stackTrace);
		}
	}

}
