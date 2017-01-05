package com.sap.dbs.dbx.i068191.annotation.processor.core.datasource;

import java.util.List;
import java.util.Map;

import org.apache.olingo.odata2.annotation.processor.core.util.AnnotationHelper.AnnotatedNavInfo;

public interface ODataInterface {
	public List<?> getEntitySet();
	
	public Object getEntity(Map<String, ?> keys);
	
	public List<?> getRelatedEntity(Object source, String relatedEntityName, Map<String, Object> keys, AnnotatedNavInfo navInfo);
	
	public void createEntity(Object dataToCreate);
	
	public void deleteEntity(Map<String, ?> keys);
	
	public void updateEntity(Object dataToUpdate);
}
