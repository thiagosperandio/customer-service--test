package com.example.api.util;

import java.lang.reflect.Method;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.core.GenericTypeResolver;
import org.springframework.util.NumberUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import lombok.experimental.UtilityClass;

/**
 * Utility class to handle with objects in the project.
 *
 * @see Patterns <a href=
 *      "https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html">Java
 *      patterns regex (regular-expressions)</a>
 */
@UtilityClass
public class ObjectUtil {

	public static final String NOT_NUMBERS_REGEX = "\\D"; // A non-digit: [^0-9]

	public static final Pattern NUMERIC_DECIMAL_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?");
	public static final Pattern LATIN_ALPHABETICAL_LETTERS_PATTERN = Pattern.compile("^[a-zA-Z]+$");
	public static final Pattern WORD_PATTERN = Pattern.compile("\\w+"); // A word character: [a-zA-Z_0-9]

	/**
	 *
	 * @param value
	 * @return {@code true} if the {@code value} contains only numerical characters,
	 *         optionally with floating point or negative sign, {@code false}
	 *         otherwise
	 */
	public static boolean isNumeric(String value) {
		if (value == null) {
			return false;
		}
		return NUMERIC_DECIMAL_PATTERN
				.matcher(value)
				.matches();
	}

	/**
	 *
	 * @param value
	 * @return {@code true} if the {@code value} contains only characters from the
	 *         Latin alphabet, {@code false} otherwise
	 */
	public static boolean isOnlyLatinLetters(String value) {
		if (value == null) {
			return false;
		}
		return LATIN_ALPHABETICAL_LETTERS_PATTERN
				.matcher(value)
				.matches();
	}

	/**
	 *
	 * @param text
	 * @return {@code true} if the {@code value} is only one word, {@code false}
	 *         otherwise
	 */
	public static boolean isOneWord(String text) {
		if (text == null) {
			return false;
		}
		return WORD_PATTERN
				.matcher(text)
				.matches();
	}

	/**
	 *
	 * @param value
	 * @return the string with only numbers characters of the given {@code value}
	 */
	public static final String getOnlyNumbers(String value) {
		if (isNullOrEmpty(value)) {
			return null;
		}
		return value.replaceAll(NOT_NUMBERS_REGEX, "");
	}

	/**
	 *
	 * @param text
	 * @return the string with non-accent characters of the given {@code text}
	 */
	public static String removeAccents(String text) {
		if (text == null) {
			return text;
		}
		return Normalizer
				.normalize(text, Normalizer.Form.NFD)
				.replaceAll("[\\p{M}]", "");
	}

	/**
	 * Analyze if {@code value} is null or empty, returning null if true. It's
	 * useful method to use in Optional and Stream logic to check if is'nt valid
	 * value, for example.
	 *
	 * @param value
	 * @return the original {@code value}, if not null neither empty, and null
	 *         otherwise
	 */
	public static String getOrNull(String value) {
		if (isNullOrEmpty(value)) {
			return null;
		} else {
			return value;
		}
	}

	/**
	 * Analyze if {@code value} is null, returning empty string if true.
	 *
	 * @param value
	 * @return the original {@code value}, if not null, and empty string otherwise
	 */
	public static String getOrEmptyIfNull(String value) {
		if (value == null) {
			return "";
		} else {
			return value;
		}
	}

	/**
	 * Analyze if {@code collection} is null, returning empty collection if true.
	 *
	 * @param value
	 * @return the original {@code collection}, if not null, and empty collection
	 *         otherwise
	 */
	public static <T> List<T> getOrEmpty(List<T> collection) {
		return Optional.ofNullable(collection).orElse(new ArrayList<>());
	}

	/**
	 * Analyze if {@code collection} is null, returning empty collection if true.
	 *
	 * @param value
	 * @return the original {@code collection}, if not null, and empty collection
	 *         otherwise
	 */
	public static <T> Set<T> getOrEmpty(Set<T> collection) {
		return Optional.ofNullable(collection).orElse(new HashSet<>());
	}

	/**
	 * Analyze if {@code map} is null, returning empty map if true.
	 *
	 * @param value
	 * @return the original {@code map}, if not null, and empty map otherwise
	 */
	public static <K, V> Map<K, V> getOrEmpty(Map<K, V> map) {
		return Optional.ofNullable(map).orElse(new HashMap<>());
	}

	/**
	 * @param <T>        the generic type that extends the {@code Number} class.
	 * @param number     the instance {@code number} to check.
	 * @param numberType the type of the Number class, such as Long, Integer,
	 *                   Double, Float, BigDecimal, or BigInteger. Is the
	 *                   {@code <T>} type class
	 *
	 * @return the supplied {@code number} parameter, otherwise zero.
	 */
	public <T extends Number> T zeroIfNull(T number, Class<T> numberType) {
		if (number != null) {
			return number;
		} else {
			return NumberUtils.convertNumberToTargetClass(0, numberType);
		}
	}

	/**
	 * Limit string by the given size from the left to de right side, like the
	 * LTRIM() DB function.
	 *
	 * @param text
	 * @param size
	 * @return the string truncated from the beginning of the string to the given
	 *         length parameter for trimming on the right side.
	 */
	public static final String limitSize(String text, int size) {
		String limitedText = text;
		if (text != null && text.length() > size) {
			limitedText = text.substring(0, size);
		}
		return limitedText;
	}

	/**
	 * Limit string by the given size from the right to de left side, like the
	 * RTRIM() DB function.
	 *
	 * @param text
	 * @param size
	 * @return the truncated string with the length based on the end of the string
	 *         up to the given length parameter to trim on the left side.
	 */
	public static final String limitSizeFromRight(String text, int size) {
		String limitedText = text;
		if (text != null && text.length() > size) {
			limitedText = text.substring(text.length() - size);
		}
		return limitedText;
	}

	/**
	 *
	 * @param values
	 * @return the first not null and not empty value.
	 */
	public static Object getFirstNotEmpty(Object... values) {
		for (var value : values) {
			if (isNotNullAndNotEmpty(value)) {
				return value;
			}
		}
		return null;
	}

	/**
	 *
	 * @param properties a {@code Optional<Map<String, Object>>} collection of
	 *                   source properties used to find the {@code key}
	 * @param key        the string {@code key} wich stores the object value to get
	 *                   in the {@code properties} map
	 * @return the optional with the object value finded if {@code key} exists in
	 *         {@code properties} map, empty optional otherwise
	 */
	public static Optional<Object> getPropertyMap(Optional<Map<String, Object>> properties, String key) {
		return properties.map(m -> m.get(key));
	}

	/**
	 *
	 * @param properties a {@code Map<String, Object>} collection of source
	 *                   properties used to find the {@code key}
	 * @param key        the string {@code key} wich stores the object value to get
	 *                   in the {@code properties} map
	 * @return the optional with the object value finded if {@code key} exists in
	 *         {@code properties} map, empty optional otherwise
	 */
	public static Optional<Object> getPropertyMap(Map<String, Object> properties, String key) {
		return Optional.ofNullable(properties).map(m -> m.get(key));
	}

	/**
	 * Checks if the object is a collection, and return it as a collection of
	 * strings for each element. If not a collection, returns a single-element
	 * collection.
	 *
	 * @param value
	 * @return a collection of strings containing the object values, or empty
	 *         collection otherwise.
	 */
	public static List<String> getAttributeAsList(Object value) {
		if (value == null) {
			return Collections.emptyList();
		}
		if (value instanceof Collection) {
			List<?> collectionReturn = new ArrayList<>((Collection<?>) value);
			return collectionReturn.stream()
					.map(Object::toString)
					.collect(Collectors.toList());
		}
		return Collections.singletonList(value.toString());
	}

	/**
	 * @param objectInstance
	 * @param propertyName
	 *
	 * @return the value of {@code propertyName}'s instance object obtained by
	 *         getter method, if exists.
	 *
	 * @throws IllegalArgumentException if don't exists the getter method of the
	 *                                  propertyName.
	 * @throws RuntimeException         if the invoke method has a runtime error.
	 */
	public static final Optional<Object> getPropertyValue(@Valid @NotNull Object objectInstance,
			@Valid @NotBlank String propertyName) {
		String getterName = "get" + StringUtils.capitalize(propertyName);
		Method getter = ReflectionUtils.findMethod(objectInstance.getClass(), getterName);
		if (getter == null) {
			throw new IllegalArgumentException("Getter method not found for property: " + propertyName);
		}
		return Optional.ofNullable(ReflectionUtils.invokeMethod(getter, objectInstance));
	}

	/**
	 *
	 * @param classInstance
	 * @param classToGetGenerics
	 *
	 * @return the generic classes of the given param, based on class to get
	 *         generics param.
	 *
	 * @see GenericTypeResolver#resolveTypeArguments
	 */
	public static Class<?>[] getGenericType(@Valid @NotNull Class<?> classInstance,
			@Valid @NotNull Class<?> classToGetGenerics) {
		return GenericTypeResolver.resolveTypeArguments(classInstance, classToGetGenerics);
	}

	/**
	 *
	 * @param classInstance
	 * @param classToGetGenerics
	 * @param genericPosition
	 *
	 * @return the generic class of the given param, based on class to get generics
	 *         and generic position params.
	 *
	 * @see ObjectUtil#getGenericType
	 * @see GenericTypeResolver#resolveTypeArguments
	 */
	public static Class<?> getGenericType(@Valid @NotNull Class<?> classInstance,
			@Valid @NotNull Class<?> classToGetGenerics, int genericPosition) {
		Class<?>[] typeArguments = getGenericType(classInstance, classToGetGenerics);
		if (typeArguments != null && typeArguments.length >= genericPosition) {
			return typeArguments[genericPosition];
		}
		throw new IllegalArgumentException(
				"Não foi possível determinar o tipo genérico para a interface " + classInstance.getName());
	}

	/**
	 *
	 * @param object
	 * @return {@code true} if the object is null or empty, {@code false} otherwise
	 */
	public static boolean isNullOrEmpty(Object object) {
		return object == null || (object instanceof String && ((String) object).isBlank())
				|| (object instanceof Collection<?> && ((Collection<?>) object).isEmpty());
	}

	/**
	 *
	 * @param object
	 * @return {@code true} if the object is not null and not empty, {@code false}
	 *         otherwise
	 */
	public static boolean isNotNullAndNotEmpty(Object object) {
		return object != null && !object.toString().isBlank() && !"0".equals(object.toString().trim())
				&& !"0.0".equals(object.toString().trim())
				&& !(object instanceof Collection && ((Collection<?>) object).isEmpty());
	}

	/**
	 * <b>Objetivo:</b> Verifica se existe algum elemento preenchido (não nulo nem
	 * vazio) na lista de objetos informada como parâmetro.
	 * <p>
	 * <b>Uso:</b>
	 * <ul>
	 * <li>É interessante para avaliar objetos ou as propriedades de um ou mais
	 * objetos e verificar se pelo menos um deles está preenchido, diminuindo a
	 * verbosidade de uma verificação deste tipo para cada elemento. <br>
	 * <li>Também é interessante pois avalia os objetos na ordem de precedência. Por
	 * exemplo, ao informar objetos e suas propriedades em sequencia, verifica se um
	 * objeto está preenchido, depois se uma propriedade deste objeto está
	 * preenchida, depois se a propriedade da propriedade do objeto está
	 * preenchida. Mas nesse caso, não é garantida a prevenção de um
	 * {@link NullPointerException}, pois os objetos são passados na chamada deste
	 * método.
	 * </ul>
	 *
	 * @return {@code true} ao encontrar o primeiro elemento não nulo nem vazio. Se
	 *         não encontrar nenhum, retorna {@code false}.
	 *
	 * @throws NullPointerException se o objeto (pai) de uma propriedade passada
	 *                              como parâmetro for nulo.
	 *
	 */
	public static boolean isNotNullAndNotEmptyAnyElements(Object... objects) {
		if (objects != null && objects.length > 0) {
			for (Object object : objects) {
				if (isNotNullAndNotEmpty(object)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * <b>Objetivo:</b> Verifica se todos os elementos na lista de objetos informada
	 * como parâmetro estão preenchidos (não são nulos nem vazios), diminuindo a
	 * verbosidade de uma verificação deste tipo para cada elemento.
	 * <p>
	 * <b>Uso:</b> É interessante para avaliar o caso quando todos os elementos
	 * devem estar preenchidos (normalmente usado em validações).
	 *
	 * @return {@code false} ao encontrar o primeiro elemento nulo. Se não encontrar
	 *         nenhum, retorna {@code true}.
	 *
	 * @throws NullPointerException se o objeto (pai) de uma propriedade passada
	 *                              como parâmetro for nulo.
	 *
	 */
	public static boolean isNotNullAndNotEmptyAllElements(Object... objects) {
		if (objects != null && objects.length > 0) {
			for (Object object : objects) {
				if (!isNotNullAndNotEmpty(object)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * <b>Objetivo:</b> Verifica se existe algum elemento nulo ou vazio na lista de
	 * objetos informada como parâmetro.
	 * <p>
	 * <b>Uso:</b>
	 * <ul>
	 * <li>É interessante para avaliar objetos ou as propriedades de um ou mais
	 * objetos os quais não podem ser nulos ou vazios, mesmo que haja na lista
	 * alguns objetos preenchidos, diminuindo a verbosidade de uma verificação deste
	 * tipo para cada elemento. <br>
	 * <li>Também é interessante pois avalia os objetos na ordem de precedência. Por
	 * exemplo, ao informar objetos e suas propriedades em sequencia, verifica se um
	 * objeto é nulo, depois se uma propriedade deste objeto é nula, depois se a
	 * propriedade da propriedade do objeto é nula. Mas nesse caso,
	 * não é garantida a prevenção de um {@link NullPointerException}, pois os
	 * objetos são passados na chamada deste método.
	 * </ul>
	 *
	 * @return {@code true} ao encontrar o primeiro elemento nulo. Se não encontrar
	 *         nenhum, retorna {@code false}.
	 *
	 * @throws NullPointerException se o objeto (pai) de uma propriedade passada
	 *                              como parâmetro for nulo.
	 *
	 */
	public static boolean isNullOrEmptyAnyElements(Object... objects) {
		boolean hasNullOrEmpty = false;

		if (objects != null && objects.length > 0) {
			for (Object object : objects) {
				if (isNullOrEmpty(object)) {
					hasNullOrEmpty = true;
					break;
				}
			}
		} else {
			hasNullOrEmpty = true;
		}

		return hasNullOrEmpty;
	}

	/**
	 * <b>Objetivo:</b> Verifica se todos os elementos na lista de objetos informada
	 * como parâmetro são nulos ou vazios, diminuindo a verbosidade de uma
	 * verificação deste tipo para cada elemento.
	 * <p>
	 * <b>Uso:</b> É interessante para avaliar o caso quando pelo menos um elemento
	 * deveria estar preenchido (normalmente usado em validações de negação).
	 *
	 * @return {@code true} ao encontrar o primeiro elemento nulo. Se não encontrar
	 *         nenhum, retorna {@code false}.
	 *
	 * @throws NullPointerException se o objeto (pai) de uma propriedade passada
	 *                              como parâmetro for nulo.
	 *
	 */
	public static boolean isNullOrEmptyAllElements(Object... objects) {
		boolean hasNotNullAndNotEmpty = false;

		if (objects != null && objects.length > 0) {
			for (Object object : objects) {
				if (!isNullOrEmpty(object)) {
					hasNotNullAndNotEmpty = true;
					break;
				}
			}
		}

		return !hasNotNullAndNotEmpty;
	}

	/**
	 *
	 * @param value
	 * @return the {@code value} with the first character in uppercase
	 */
	public static String firstCharUpperCase(@Valid @NotBlank String value) {
		char[] arr = value.toCharArray();
		arr[0] = Character.toUpperCase(arr[0]);
		return new String(arr);
	}

	/**
	 * <b>Objetivo:</b> Verifica se o Boolean passado para o método é {@code true}.
	 *
	 * @return {@code true} se o Boolean tem valor {@code true}, senão retorna
	 *         {@code false}.
	 *
	 */
	public boolean isTrue(Boolean value) {
		return Boolean.TRUE.equals(value);
	}

	/**
	 * <b>Objetivo:</b> Verifica se o Boolean passado para o método é {@code false}.
	 *
	 * @return {@code true} se o Boolean tem valor {@code false}, senão retorna
	 *         {@code false}.
	 *
	 */
	public boolean isFalse(Boolean value) {
		return Boolean.FALSE.equals(value);
	}

	/**
	 * <b>Objetivo:</b> Verifica se o Boolean passado para o método é {@code false}
	 * ou {@code null}.
	 *
	 * @return {@code true} se o Boolean tem valor {@code false} ou {@code null},
	 *         senão retorna {@code false}.
	 *
	 */
	public boolean isFalseOrNull(Boolean value) {
		return value == null || Boolean.FALSE.equals(value);
	}
}
