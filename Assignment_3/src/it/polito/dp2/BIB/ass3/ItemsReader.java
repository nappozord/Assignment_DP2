package it.polito.dp2.BIB.ass3;

import java.util.Set;

/**
 * An interface for reading bibliographic information from a remote service
 *
 */
public interface ItemsReader {
	/**
	 * Gets readers for all the available items whose title contains the specified keyword
	 * and whose publication year is between the specified values (between the given "since" and "to" years
	 * inclusive; if "to" is lower than "since", an empty set is returned).
	 * @param keyword the title keyword for selecting items or null to get all items.
	 * @param since the publication year since which (inclusive) items have to be selected, or null to get items published since any year.
	 * @param to the publication date to which (inclusive) items have to be selected, or null to get items published to any year.
	 * @return a set of interfaces for reading the selected items.
	 * @throws ServiceException if the operation cannot be completed because of problems related to the service used to get the data
	 */
	public Set<ItemReader> getItems(String keyword, int since, int to) throws ServiceException;

}
