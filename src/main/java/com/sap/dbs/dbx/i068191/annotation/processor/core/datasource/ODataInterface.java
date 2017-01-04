package com.sap.dbs.dbx.i068191.annotation.processor.core.datasource;

import java.util.List;
import java.util.Map;

public interface ODataInterface {
	public List<?> getEntitySet();
	
	public Object getEntity(Map<String, ?> keys);
	
	public List<?> getRelatedEntity(Object source, String relatedEntityName, Map<String, Object> keys);
	
	public void createEntity(Object dataToCreate);
	
	public void deleteEntity(Map<String, ?> keys);
	
	public void updateEntity(Object dataToUpdate);
}
