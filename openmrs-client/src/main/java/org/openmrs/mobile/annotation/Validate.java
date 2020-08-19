package org.openmrs.mobile.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Alerts our app that a field needs to be validated by calling a specified method on a specified class.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Validate {
	/*
	 * @return The class containing the method to call for validation
	 */
	Class<?> clazz();

	/**
	 * @return The method to call in the class
	 */
	String method();

	/**
	 * @return Whether the result needs to be negated (default false)
	 */
	boolean negate() default false;
}
