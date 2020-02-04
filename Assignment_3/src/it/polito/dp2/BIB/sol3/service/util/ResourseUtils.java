package it.polito.dp2.BIB.sol3.service.util;

import java.math.BigInteger;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import it.polito.dp2.BIB.sol3.service.jaxb.Citation;
import it.polito.dp2.BIB.sol3.service.jaxb.Item;

public class ResourseUtils {
	UriBuilder base;
	UriBuilder items;

	public ResourseUtils(UriBuilder base) {
		this.base = base;
		this.items = base.clone().path("biblio/items");
	}
	
	public void completeItem(Item item, BigInteger id) {
		UriBuilder selfBuilder = items.clone().path(id.toString());
    	URI self = selfBuilder.build();
    	item.setSelf(self.toString());
    	URI citations = selfBuilder.clone().path("citations").build();
    	item.setCitations(citations.toString());
    	URI citedBy = selfBuilder.clone().path("citedBy").build();
    	item.setCitedBy(citedBy.toString());
    	URI targets = selfBuilder.clone().path("citations/targets").build();
    	item.setTargets(targets.toString());
	}

	public void completeCitation(Citation citation, BigInteger id, BigInteger tid) {
		UriBuilder fromBuilder = items.clone().path(id.toString());
		citation.setFrom(fromBuilder.build().toString());
		citation.setTo(items.clone().path(tid.toString()).build().toString());
		citation.setSelf(fromBuilder.clone().path("citations").path(tid.toString()).build().toString());
	}

}
