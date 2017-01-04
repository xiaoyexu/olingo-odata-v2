package com.sap.dbs.dbx.i068191.annotation.processor.core.datasource;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.odata2.annotation.processor.core.datasource.DataSource;
import org.apache.olingo.odata2.annotation.processor.core.datasource.DataStore;
import org.apache.olingo.odata2.annotation.processor.core.util.AnnotationHelper;
import org.apache.olingo.odata2.annotation.processor.core.util.ClassHelper;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmFunctionImport;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.exception.ODataNotFoundException;
import org.apache.olingo.odata2.api.exception.ODataNotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyODataAnnotationDs implements MyODataSource{
	private static final AnnotationHelper ANNOTATION_HELPER = new AnnotationHelper();
	private final Map<String, DataStore<Object>> dataStores = new HashMap<String, DataStore<Object>>();
	  
	@Getter
	@Setter
	private ApplicationContext appContext;
	
	private Map<String, Object> oDataAgents = new HashMap<String, Object>();
	
	public MyODataAnnotationDs(final String packageToScan) throws ODataException {
	    this(packageToScan, true);
	  }

	  public MyODataAnnotationDs(final String packageToScan, final boolean persistInMemory) throws ODataException {
	    List<Class<?>> foundClasses = ClassHelper.loadClasses(packageToScan, new ClassHelper.ClassValidator() {
	      @Override
	      public boolean isClassValid(final Class<?> c) {
	        return null != c.getAnnotation(org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet.class);
	      }
	    });
	    init(foundClasses);
	  }

	  @SuppressWarnings("unchecked")
	  private void init(final Collection<Class<?>> annotatedClasses) throws ODataException {
	    try {
	      for (Class<?> clz : annotatedClasses) {
	        String entitySetName = ANNOTATION_HELPER.extractEntitySetName(clz);
	        if (entitySetName != null) {
	          DataStore<Object> dhs = (DataStore<Object>) DataStore.createInMemory(clz, true);
	          dataStores.put(entitySetName, dhs);
	        } else if (!ANNOTATION_HELPER.isEdmAnnotated(clz)) {
	          throw new ODataException("Found not annotated class during DataStore initilization of type: "
	              + clz.getName());
	        }
	      }
	    } catch (DataStore.DataStoreException e) {
	      throw new ODataException("Error in DataStore initilization with message: " + e.getMessage(), e);
	    }
	  }
	
	private String getEntitySetBeanFullClassName(EdmEntitySet entitySet) throws EdmException {
		return this.getEntitySetBeanClass(entitySet).getName();
	}
	
	private Class getEntitySetBeanClass(EdmEntitySet entitySet) throws EdmException{
		DataStore<Object> ds = dataStores.get(entitySet.getName());
		Class dataTypeClass = ds.getDataTypeClass();
		log.debug("getEntitySetBeanClass return " + dataTypeClass);
		return dataTypeClass;
	}
	
	private String getODataBeanAgentName(EdmEntitySet entitySet) throws EdmException{
		String beanClassName = this.getEntitySetBeanFullClassName(entitySet);
		beanClassName += "ODataAgent";
		log.debug("getODataBeanAgentName return " + beanClassName);
		return beanClassName;
	}
	
	private ODataInterface getODataInterfaceByName(String beanName) {
		ODataInterface oDataInterface = (ODataInterface) oDataAgents.get(beanName);
		if (oDataInterface == null) {
			log.debug("getODataInterfaceByName oDataInterface == null");
			oDataInterface = (ODataInterface)this.appContext.getBean(beanName);	
			
			oDataAgents.put(beanName, oDataInterface);
			log.debug("getODataInterfaceByName getBean oDataInterface " + oDataInterface + " and put in map");
		}
		return oDataInterface;
	}
	  
	  
	@Override
	public List<?> readData(EdmEntitySet entitySet)
			throws ODataNotImplementedException, ODataNotFoundException, EdmException, ODataApplicationException {
		log.debug("MyODataAnnotationDs readData called");
		ODataInterface oDataInterface = this.getODataInterfaceByName(this.getODataBeanAgentName(entitySet));
		return oDataInterface.getEntitySet();
	}

	@Override
	public Object readData(EdmEntitySet entitySet, Map<String, Object> keys)
			throws ODataNotImplementedException, ODataNotFoundException, EdmException, ODataApplicationException {
		log.debug("MyODataAnnotationDs readData with keys called");
		ODataInterface oDataInterface = this.getODataInterfaceByName(this.getODataBeanAgentName(entitySet));
		return oDataInterface.getEntity(keys);
	}

	@Override
	public Object readData(EdmFunctionImport function, Map<String, Object> parameters, Map<String, Object> keys)
			throws ODataNotImplementedException, ODataNotFoundException, EdmException, ODataApplicationException {
		log.debug("MyODataAnnotationDs readData with function parameters keys called");
		return null;
	}

	@Override
	public Object readRelatedData(EdmEntitySet sourceEntitySet, Object sourceData, EdmEntitySet targetEntitySet,
			Map<String, Object> targetKeys)
			throws ODataNotImplementedException, ODataNotFoundException, EdmException, ODataApplicationException {
		log.debug("MyODataAnnotationDs readRelatedData with sourceEntitySet sourceData targetEntitySet targetKeys called");
		ODataInterface oDataInterface = this.getODataInterfaceByName(this.getODataBeanAgentName(sourceEntitySet));
		log.debug("oDataInterface is " + oDataInterface);
		String relatedEntityName = targetEntitySet.getName();
		log.debug("relatedEntityName is " + relatedEntityName);
		return oDataInterface.getRelatedEntity(sourceData, relatedEntityName, targetKeys);
		
	}

	@Override
	public BinaryData readBinaryData(EdmEntitySet entitySet, Object mediaLinkEntryData)
			throws ODataNotImplementedException, ODataNotFoundException, EdmException, ODataApplicationException {
		log.debug("MyODataAnnotationDs readBinaryData with entitySet mediaLinkEntryData called");
		//return this.dataSourceDelegate.readBinaryData(entitySet, mediaLinkEntryData);
		return null;
	}

	@Override
	public Object newDataObject(EdmEntitySet entitySet)
			throws ODataNotImplementedException, EdmException, ODataApplicationException {
		log.debug("MyODataAnnotationDs newDataObject with entitySet called");
		DataStore<Object> ds = dataStores.get(entitySet.getName());
		Class dataTypeClass = ds.getDataTypeClass();
		try {
			Object obj = dataTypeClass.newInstance();
			log.debug("newDataObject new object " + obj + " created");
			return obj;
		} catch (InstantiationException | IllegalAccessException e) {
			log.error(e.getLocalizedMessage());
		}
		return null;
	}

	@Override
	public void writeBinaryData(EdmEntitySet entitySet, Object mediaLinkEntryData, BinaryData binaryData)
			throws ODataNotImplementedException, ODataNotFoundException, EdmException, ODataApplicationException {
		log.debug("MyODataAnnotationDs writeBinaryData called");
		//this.dataSourceDelegate.writeBinaryData(entitySet, mediaLinkEntryData, binaryData);
	}

	@Override
	public void deleteData(EdmEntitySet entitySet, Map<String, Object> keys)
			throws ODataNotImplementedException, ODataNotFoundException, EdmException, ODataApplicationException {
		log.debug("MyODataAnnotationDs deleteData called");
		ODataInterface oDataInterface = this.getODataInterfaceByName(this.getODataBeanAgentName(entitySet));
		oDataInterface.deleteEntity(keys);
	}

	@Override
	public void createData(EdmEntitySet entitySet, Object data)
			throws ODataNotImplementedException, EdmException, ODataApplicationException {
		log.debug("MyODataAnnotationDs createData called");
		ODataInterface oDataInterface = this.getODataInterfaceByName(this.getODataBeanAgentName(entitySet));
		oDataInterface.createEntity(data);
	}

	@Override
	public void deleteRelation(EdmEntitySet sourceEntitySet, Object sourceData, EdmEntitySet targetEntitySet,
			Map<String, Object> targetKeys)
			throws ODataNotImplementedException, ODataNotFoundException, EdmException, ODataApplicationException {
		log.debug("MyODataAnnotationDs deleteRelation called");
		//this.dataSourceDelegate.deleteRelation(sourceEntitySet, sourceData, targetEntitySet, targetKeys);
	}

	@Override
	public void writeRelation(EdmEntitySet sourceEntitySet, Object sourceData, EdmEntitySet targetEntitySet,
			Map<String, Object> targetKeys)
			throws ODataNotImplementedException, ODataNotFoundException, EdmException, ODataApplicationException {
		log.debug("MyODataAnnotationDs writeRelation called");
		//this.dataSourceDelegate.writeRelation(sourceEntitySet, sourceData, targetEntitySet, targetKeys);
	}

	@Override
	public void updateData(EdmEntitySet entitySet, Object data)
			throws ODataNotImplementedException, EdmException, ODataApplicationException {
		ODataInterface oDataInterface = this.getODataInterfaceByName(this.getODataBeanAgentName(entitySet));
		oDataInterface.updateEntity(data);
	}
}
