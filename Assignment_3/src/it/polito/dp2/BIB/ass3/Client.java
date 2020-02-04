/**
 * 
 */
package it.polito.dp2.BIB.ass3;

import java.util.Set;

/**
 * An interface for interacting with the Biblio Service
 *
 */
public interface Client extends ItemsReader {
	/**
	 * Creates a new bookshelf with the given name (this operation is performed on the remote service)
	 * @param name the name of the bookshelf to be created
	 * @return an interface for operating on the created bookshelf
	 * @throws ServiceException if the operation cannot be completed because of problems with the remote service
	 */
	public Bookshelf createBookshelf(String name) throws ServiceException;
	
	/**
	 * Finds bookshelves whose name matches the given name (this operation is performed on the remote service)
	 * @param name the name to be searched
	 * @return a set of interfaces for operating on the selected bookshelves
	 * @throws ServiceException
	 */
	public Set<? extends Bookshelf> getBookshelfs(String name) throws ServiceException;
}
