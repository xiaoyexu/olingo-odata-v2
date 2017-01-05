# olingo-odata-v2 - A Simple implementation for odata version 2 with Olingo

## Background
In process of learning olingo odata handling and I try to implement a DataSource class to see how it works. 

The project provided a feature that for each odata entity(describled by annotation @EdmEntityType @EdmEntitySet), the CRUD operations are delegated to a <OData entity name>ODataAgent class, so that you handle the logic yourself, with or without using JPA.

It maybe useful if you are consuming data from different data sources or reconstruct output odata structure by existing tables.

Basically you have to write CRUD logic yourself in the interface, like a DAO class.

## Usage
To use the jar file, you could add below in your pom.xml

```
<dependency>
	<groupId>com.sap.dbs.dbx.i068191</groupId>
	<artifactId>springboot-olingo-jpa</artifactId>
	<version>0.1.1-SNAPSHOT</version>
</dependency>
```

In you spring boot application class, added below bean to create an ODataService factory by com.sap.dbs.dbx.i068191.annotation.processor.MyODataServiceFactory with package you would like to scan.

Below will create a service factory which scans all class under "com.mario.bean".

```
@Bean(name="MyODataServiceFactory")
public ODataServiceFactory getServiceFactory(){
	return new MyODataServiceFactory("com.mario.bean");
}
```

Here, class com.mario.bean.Employee is a bean with JPA and Olingo V2 annotated.

```
package com.mario.bean;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;

import lombok.Getter;
import lombok.Setter;

@EdmEntityType
@EdmEntitySet
@Entity
@Table(name="Employee")
public class Employee {
	@Getter
	@Setter
	@EdmKey
	@EdmProperty
	@Id
	private Integer id;
	
	@Getter
	@Setter
	@EdmProperty
	private String name;
	
	@Getter
	@Setter
	@EdmProperty
	private Integer age;
}
```

Create a JpaRepo class for Employee, e.g.

```
package com.mario.bean.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mario.bean.Employee;

public interface EmployeeRepo extends JpaRepository<Employee, Integer>{
	public Employee findById(Integer id);
}

```

Create a EmployeeODataAgent class, this class will be used to bridging Olingo and JPA.

If you are not using JPA, you can also implmenet the logic in a different way, e.g. read from file/write to file, or event return hardcoded data etc.

```
package com.mario.bean.odata;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.mario.bean.Employee;
import com.mario.bean.repo.EmployeeRepo;
import com.sap.dbs.dbx.i068191.annotation.processor.core.datasource.ODataInterface;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmployeeODataAgent implements ODataInterface{
	
	@Autowired
	EmployeeRepo employeeRepo;
	
	public List<?> getEntitySet(){
		return employeeRepo.findAll();
	}

	@Override
	public Object getEntity(Map<String, ?> keys) {
		log.debug("getEntity called");
		Integer id = (Integer) keys.get("Id");
		log.debug("getEntity id is " + id.intValue());
		return employeeRepo.findById(id);
	}

	@Override
	public List<?> getRelatedEntity(Object source, String relatedEntityName, Map<String, Object> keys,
			Field sourceField) {
		return new ArrayList<>();
	}
	

	@Override
	public void createEntity(Object dataToCreate) {
		log.debug("createEntity called");
		Employee p = (Employee)dataToCreate;
		if (!employeeRepo.exists(p.getId())) {
			employeeRepo.save((Employee)dataToCreate);
		}
	}

	@Override
	public void deleteEntity(Map<String, ?> keys) {
		log.debug("deleteEntity called");
		Integer id = (Integer)keys.get("Id");
		employeeRepo.delete(id);
	}

	@Override
	public void updateEntity(Object dataToUpdate) {
		log.debug("updateEntity called");
		Employee p = (Employee)dataToUpdate;
		employeeRepo.save(p);
	}
}
```

In application class, set bean name as ``full entity class path + "ODataAgent"`` so that it will be created.

Please notice that bean name has nothing to do with ODataAgent class path, EmployeeODataAgent class could be in package com.mario.bean.odata but the bean name need to be set as "com.mario.bean.EmployeeODataAgent".

```
@Bean(name="com.mario.bean.EmployeeODataAgent")
public EmployeeODataAgent employeeODataAgent(){
	log.info("return EmployeeODataAgent object");
	return new EmployeeODataAgent();
}
```

Create web servlet for odata service.

```
@WebServlet(urlPatterns = { "/my_odata.svc/*" })
class OneODataServlet extends SimpleODataServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		setoDataServiceFactoryBeanName("MyODataServiceFactory");
	}
}
```

You can also create serveral servlet with different url path to handle different entities.

Below application class injected 2 odata factories with different package name and created 2 servlets to handle request respectively.

```
@SpringBootApplication
@ServletComponentScan
@Slf4j
public class SpringbootOlingoApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(SpringbootOlingoApplication.class, args);
	}
	//
	// ... Ommit JPA Dataource/H2 Server/Other Injection ...
	//
	@Bean(name="MarioODataServiceFactory")
	public ODataServiceFactory marioServiceFactory(){
		return new MyODataServiceFactory("com.mario.bean");
	}
	
	@Bean(name="LuigiODataServiceFactory")
	public ODataServiceFactory luigiServiceFactory(){
		return new MyODataServiceFactory("com.luigi.bean");
	}
	
	@Bean(name="com.mario.bean.EmployeeODataAgent")
	public EmployeeODataAgent marioEmployeeODataAgent(){
		log.info("return EmployeeODataAgent object");
		return new EmployeeODataAgent();
	}
	
	@Bean(name="com.luigi.bean.StudentODataAgent")
	public StudentODataAgent luigiEmployeeODataAgent(){
		return new StudentODataAgent();
	}
}
```

```
@WebServlet(urlPatterns = { "/mario_odata.svc/*" })
class MarioODataServlet extends SimpleODataServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		setoDataServiceFactoryBeanName("MarioODataServiceFactory");
	}
}

@WebServlet(urlPatterns = { "/luigi_odata.svc/*" })
class LuigiODataServlet extends SimpleODataServlet {

	private static final long serialVersionUID = 2L;

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		setoDataServiceFactoryBeanName("LuigiODataServiceFactory");
	}
}
```

Refer to project [olingo-odata-v2-sample](https://github.wdf.sap.corp/I068191/olingo-odata-v2-sample) for detail example.
