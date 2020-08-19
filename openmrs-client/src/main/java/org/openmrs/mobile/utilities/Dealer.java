package org.openmrs.mobile.utilities;

import com.google.common.base.Supplier;

/**
 * Adds a method to handle functionality beyond just supplying data, but also rejecting it
 * @param <T>
 */
public interface Dealer<T> extends Supplier<T> {
	/**
	 * Reject what the supplier gives in it's get() method
	 */
	void reject();
}
