package it.polito.dp2.BIB.sol3.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.polito.dp2.BIB.ass3.ItemReader;
import it.polito.dp2.BIB.sol3.client.Items.Item;

public class ItemReaderImpl implements it.polito.dp2.BIB.ass3.ItemReader {

	private Integer id;
	private String title;
	private String subtitle = null;
	private List<String> authors;
	private Set<it.polito.dp2.BIB.ass3.ItemReader> citingItems;
	
	
	public ItemReaderImpl(Item i) {
		this.title = i.getTitle();
		if (i.getSubtitle() != null)
			this.subtitle = i.getSubtitle();
		this.authors = new ArrayList<>();
		if (i.getAuthor() != null) {
			if (! i.getAuthor().isEmpty())
				this.authors.addAll(i.getAuthor());
		}
		citingItems = new HashSet<>();
	}

	public Integer getId()  {
		return id;
	}
	
	@Override
	public String[] getAuthors() {
		String[] a = new String[authors.size()];
		for (int i = 0; i < authors.size(); i++)
			a[i] = authors.get(i);
		return a;
	}

	@Override
	public Set<it.polito.dp2.BIB.ass3.ItemReader> getCitingItems() {
		if (citingItems == null)
			citingItems = new HashSet<>();
		return citingItems;
	}

	@Override
	public String getSubtitle() {
		return subtitle;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Item id="+id+" Title="+title);
		if (subtitle != null)
			sb.append(" Subtitle="+subtitle);
		sb.append("\nAuthors="+authors);
		sb.append("\nCiting Items=");
		for (it.polito.dp2.BIB.ass3.ItemReader i : citingItems)  {
			sb.append(i.getTitle()+" ");
		}
		return sb.toString();
	}
}
