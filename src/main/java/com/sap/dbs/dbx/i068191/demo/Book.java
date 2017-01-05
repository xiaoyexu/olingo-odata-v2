package com.sap.dbs.dbx.i068191.demo;

import java.util.ArrayList;
import java.util.List;


import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;

import lombok.Getter;
import lombok.Setter;

@EdmEntityType
@EdmEntitySet
public class Book {
	@Getter
	@Setter
	@EdmKey
	@EdmProperty
	private Integer id;
	
	@Getter
	@Setter
	@EdmProperty
	private String bookName;
	
	@Getter
	@Setter
	@EdmNavigationProperty
	private List<Book> relatedBooks = new ArrayList<Book>();
}
