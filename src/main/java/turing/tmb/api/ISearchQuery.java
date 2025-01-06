package turing.tmb.api;

public interface ISearchQuery {
	String getText();

	default String getNamespaceFilter() {
		return "";
	}
}
