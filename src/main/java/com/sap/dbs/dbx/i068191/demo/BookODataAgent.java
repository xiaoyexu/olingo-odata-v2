package com.sap.dbs.dbx.i068191.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.olingo.odata2.annotation.processor.core.util.AnnotationHelper.AnnotatedNavInfo;
import org.springframework.beans.factory.annotation.Autowired;

import com.sap.dbs.dbx.i068191.annotation.processor.core.datasource.ODataInterface;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BookODataAgent implements ODataInterface{
	
	public List<?> getEntitySet(){
		Book b1 = new Book();
		b1.setId(100);
		b1.setBookName("Book 1");
		
		Book b2 = new Book();
		b2.setId(200);
		b2.setBookName("Book 2");
		
		return Arrays.asList(b1,b2);
	}

	@Override
	public Object getEntity(Map<String, ?> keys) {
		log.debug("getEntity called");
		Integer id = (Integer) keys.get("Id");
		log.debug("getEntity id is " + id.intValue());
		Book b1 = new Book();
		b1.setId(100);
		b1.setBookName("Book 1");
		return b1;
	}
	
	@Override
	public List<?> getRelatedEntity(Object source, String relatedEntityName, Map<String, Object> keys,
			AnnotatedNavInfo navInfo) {
		if (navInfo.getToField().getName().equalsIgnoreCase("relatedBooks")) {
			Book b1 = new Book();
			b1.setId(101);
			b1.setBookName("Related Book 1");
			return Arrays.asList(b1);
		}
		return new ArrayList<>();
	}
	
	@Override
	public void createEntity(Object dataToCreate) {
		log.debug("createEntity called");
	}

	@Override
	public void deleteEntity(Map<String, ?> keys) {
		log.debug("deleteEntity called");
	}

	@Override
	public void updateEntity(Object dataToUpdate) {
		log.debug("updateEntity called");
	}
}
