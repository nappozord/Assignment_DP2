package it.polito.dp2.BIB.sol3.db;

import java.math.BigInteger;

import it.polito.dp2.BIB.sol3.service.SearchScope;
import it.polito.dp2.BIB.sol3.service.jaxb.Citation;
import it.polito.dp2.BIB.sol3.service.jaxb.Item;

/**
 * An interface to interact with a DB of Item and Citation objects
 *
 */
public interface DB {

	/**
	 * Create a new item in the DB using the given item information
	 * @param item the item object with the information about the item to be created
	 * @return an integer id assigned to the created item
	 * @throws NullPointerException if item is null
	 * @throws Exception if the item cannot be created in the DB for other reasons
	 */
	BigInteger createItem(Item item) throws Exception;

	/**
	 * Perform a query on the DB to search items
	 * @param scope the scope of the search 
	 * @param keyword the keyword for selecting items.
	 * @param beforeInclusive the publication date to which (inclusive) items have to be selected
	 * @param afterInclusive the publication year since which (inclusive) items have to be selected
	 * @param page the number of the results page to get
	 * @return an ItemPage object that contains the search results (one page)
	 * @throws Exception if the operation cannot be completed
	 */
	ItemPage getItems(SearchScope scope, String keyword, int beforeInclusive, int afterInclusive, BigInteger page)
			throws Exception;

	
	/**
	 * Perform a query on the DB to obtain the information related to the item with a given id
	 * @param id the integer id of the item
	 * @return an Item object describing the item with the given id, or null if an item 
	 *  with the given id is not available in the DB
	 * @throws Exception if the operation cannot be completed
	 */
	Item getItem(BigInteger id) throws Exception;

	
	/**
	 * Update an item in the DB using the given item information
	 * @param id the integer id of the item to update
	 * @param item the item object with the information about the item to be updated
	 * @return  an updated Item object, or null if an item 
	 *  with the given id is not available in the DB
	 * @throws Exception if the operation cannot be completed
	 */
	Item updateItem(BigInteger id, Item item) throws Exception;

	
	/**
	 * Remove an item from the DB 
	 * @param id the integer id of the item 
	 * @return the same integer id of the item upon successful removal
	 * or null if an item with the given id is not available in the DB
	 * @throws ConflictInOperationException if the Item is cited by or cites other items
	 * @throws Exception if the operation cannot be completed for other reasons
	 */
	BigInteger deleteItem(BigInteger id) throws ConflictInOperationException, Exception;

	
	/**
	 * Create a citation relationship between two items in the DB
	 * @param id the integer id of the citing item 
	 * @param tid the integer id of the cited item 
	 * @param citation a Citation object that contains information about the citation relationship
	 * @return a Citation object that describes the created relationship
	 * @throws BadRequestInOperationException if the items 
	 *  with the given ids are not available in the DB
	 * @throws Exception if the operation cannot be completed
	 */
	Citation createItemCitation(BigInteger id, BigInteger tid, Citation citation)
			throws BadRequestInOperationException, Exception;

	/**
	 * Remove a citation relationship between two items in the DB
	 * @param id the integer id of the citing item 
	 * @param tid the integer id of the cited item
	 * @return true if the operation is successful, or false if there is no relationship between the items in the DB
	 * @throws Exception if the operation cannot be completed 
	 */
	boolean deleteItemCitation(BigInteger id, BigInteger tid) throws Exception;

	/**
	 * Perform a query on the DB to obtain a citation information between two items 
	 * @param id the integer id of the citing item 
	 * @param tid the integer id of the cited item
	 * @return  a Citation object that contains information about the specified citation
	 * @throws Exception if the operation cannot be completed 
	 */
	Citation getItemCitation(BigInteger id, BigInteger tid) throws Exception;

	/**
	 * Perform a query on the DB to obtain the items cited by a given item
	 * @param  id the integer id of the citing item
	 * @param page the number of the results page to get
	 * @return an ItemPage object that contains the search results (one page)
	 * @throws Exception if the operation cannot be completed 
	 */
	ItemPage getItemCitations(BigInteger id, BigInteger page) throws Exception;

	/**
	 * Perform a query on the DB to obtain the items citing a given item
	 * @param id the integer id of the citing item
	 * @param page the number of the results page to get
	 * @return an ItemPage object that contains the search results (one page)
	 * @throws Exception if the operation cannot be completed 
	 */
	ItemPage getItemCitedBy(BigInteger id, BigInteger page) throws Exception;

}