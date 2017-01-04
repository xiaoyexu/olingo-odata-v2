package com.sap.dbs.dbx.i068191.annotation.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.olingo.odata2.annotation.processor.core.ListsProcessor;
import org.apache.olingo.odata2.annotation.processor.core.datasource.AnnotationInMemoryDs;
import org.apache.olingo.odata2.annotation.processor.core.datasource.AnnotationValueAccess;
import org.apache.olingo.odata2.annotation.processor.core.datasource.DataSource.BinaryData;
import org.apache.olingo.odata2.annotation.processor.core.edm.AnnotationEdmProvider;
import org.apache.olingo.odata2.api.ODataService;
import org.apache.olingo.odata2.api.ODataServiceFactory;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.rt.RuntimeDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.sap.dbs.dbx.i068191.annotation.processor.core.MyODataProcessor;
import com.sap.dbs.dbx.i068191.annotation.processor.core.datasource.MyODataAnnotationDs;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyODataServiceFactory extends ODataServiceFactory {

	private String packageToScan;
	
	private ODataService MY_ODATA_SERVICE;
	
	@Autowired
	private ApplicationContext appContext;
	
	public MyODataServiceFactory(String packageToScan) {
		this.packageToScan = packageToScan;
	}
	
	@Override
	public ODataService createService(ODataContext ctx) throws ODataException {
		
		if (this.MY_ODATA_SERVICE == null) {
			AnnotationEdmProvider edmProvider = new AnnotationEdmProvider(packageToScan);
			MyODataAnnotationDs dataSource = new MyODataAnnotationDs(packageToScan);
			dataSource.setAppContext(this.appContext);
		    AnnotationValueAccess valueAccess = new AnnotationValueAccess();
		    this.MY_ODATA_SERVICE = RuntimeDelegate.createODataSingleProcessorService(edmProvider,
			        new MyODataProcessor(dataSource, valueAccess));
		}
		return this.MY_ODATA_SERVICE;
	}
}
