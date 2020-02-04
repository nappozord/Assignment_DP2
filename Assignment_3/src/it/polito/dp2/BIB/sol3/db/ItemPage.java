package it.polito.dp2.BIB.sol3.db;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import it.polito.dp2.BIB.sol3.service.jaxb.Item;

public class ItemPage {
	Map<BigInteger,Item> map;
	BigInteger totalPages=BigInteger.ONE;
	
	public BigInteger getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(BigInteger totalPages) {
		this.totalPages = totalPages;
	}
	public Map<BigInteger, Item> getMap() {
		if (map==null)
			map = new HashMap<BigInteger,Item>();
		return map;
	}
}
