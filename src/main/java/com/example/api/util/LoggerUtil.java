package com.example.api.util;

import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LoggerUtil {

	public static void logError(@Valid @NotNull Throwable exception, boolean printStackTrace,
			@Valid @NotNull Logger logger, String msg, Object... msgArgs) {
		Optional<Throwable> exceptionOpt = Optional.ofNullable(exception);
		String className = exceptionOpt.map(Throwable::getClass).map(Class::getSimpleName).orElse("(no class)");
		String message = exceptionOpt.map(Throwable::getLocalizedMessage).orElse("(no message found)");
		String cause = exceptionOpt.map(Throwable::getCause).map(Throwable::toString).orElse("(no cause found)");

		if (msgArgs != null && msgArgs.length > 0) {
			String messageLog = "Mensagem de negócio: '" + msg + "', exception type {}, exMsg {}, cause: {}";
			if (printStackTrace) {
				logger.error(messageLog, msgArgs, className, message, cause, exception);
			} else {
				logger.error(messageLog, msgArgs, className, message, cause);
			}
		} else {
			String messageLog = "Mensagem de negócio: '{}', exception type {}, exMsg {}, cause: {}";
			if (printStackTrace) {
				logger.error(messageLog, msg, className, message, cause, exception);
			} else {
				logger.error(messageLog, msg, className, message, cause);
			}
		}
	}

	public static void logInfo(@Valid @NotNull Logger logger, String msg, Object... msgArgs) {
		if (msgArgs != null && msgArgs.length > 0) {
			logger.info(msg, msgArgs);
		} else {
			logger.info("{}", msg);
		}
	}

	public static void logWarn(@Valid @NotNull Logger logger, String msg, Object... msgArgs) {
		if (msgArgs != null && msgArgs.length > 0) {
			logger.warn(msg, msgArgs);
		} else {
			logger.warn("{}", msg);
		}
	}

	public static void logDebug(@Valid @NotNull Logger logger, String msg, Object... msgArgs) {
		if (msgArgs != null && msgArgs.length > 0) {
			logger.debug(msg, msgArgs);
		} else {
			logger.debug("{}", msg);
		}
	}

}
