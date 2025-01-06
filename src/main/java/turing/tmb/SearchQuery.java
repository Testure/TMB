package turing.tmb;

import turing.tmb.api.ISearchQuery;

public class SearchQuery implements ISearchQuery {
	protected String namespaceFilter;
	protected String filter;

	public SearchQuery(String namespaceFilter, String filter) {
		this.namespaceFilter = namespaceFilter;
		this.filter = filter;
	}

	public static SearchQuery textSearch(String text) {
		return new SearchQuery("", text);
	}

	public static SearchQuery namespaceSearch(String namespace) {
		return new SearchQuery(namespace, "");
	}

	@Override
	public String getText() {
		return filter;
	}

	@Override
	public String getNamespaceFilter() {
		return namespaceFilter;
	}

	@Override
	public int hashCode() {
		return filter.hashCode() + namespaceFilter.hashCode();
	}
}
