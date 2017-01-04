package com.sap.dbs.dbx.i068191;

import java.sql.SQLException;

import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.sql.DataSource;

import org.apache.olingo.odata2.api.ODataServiceFactory;
import org.apache.olingo.odata2.core.servlet.ODataServlet;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sap.dbs.dbx.i068191.annotation.processor.MyODataServiceFactory;
import com.sap.dbs.dbx.i068191.servlet.SimpleODataServlet;

import lombok.extern.slf4j.Slf4j;

//@SpringBootApplication
//@ServletComponentScan
@Slf4j
public class SpringbootOlingoApplication /*extends SpringBootServletInitializer*/{

	public static void main(String[] args) {
		SpringApplication.run(SpringbootOlingoApplication.class, args);
	}

	@Bean(name = "standalone")
	@DependsOn("h2TcpServer")
	public DataSource h2Standalone() {
		log.info("--- create database source ---");
		DriverManagerDataSource ds = new DriverManagerDataSource();
		ds.setDriverClassName("org.h2.Driver");
		// Tcp connection
		//ds.setUrl("jdbc:h2:tcp://localhost:8082/~/test");
		//
		ds.setUrl("jdbc:h2:~/test");
		ds.setUsername("sa");
		ds.setPassword("");
		return ds;
	}
	
	@Bean(name = "h2TcpServer", destroyMethod = "stop")
	public Server h2TcpServer() throws SQLException {
		log.info("--- create h2 database service ---");
		// return Server.createTcpServer("-tcpPort", "8082", "-trace").start();
		return Server.createWebServer("-tcpPort", "8082", "-trace").start();
	}
	
	@Bean(name = "entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean emf(JpaVendorAdapter adapter, DataSource ds) {
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setPackagesToScan("com.sap.dbs.dbx.i068191.bean");
		factory.setJpaVendorAdapter(adapter);
		factory.setJtaDataSource(ds);
		return factory;
	}
	
	@Bean 
	public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
		return new JpaTransactionManager(emf);
	}

	@Bean
	public JpaVendorAdapter eclipseLink() {
		EclipseLinkJpaVendorAdapter adapter = new EclipseLinkJpaVendorAdapter();
		adapter.setDatabase(Database.H2);
		adapter.setShowSql(true);
		adapter.setGenerateDdl(true);
		return adapter;
	}
	
//	@Bean(name="MyODataServiceFactory")
//	public ODataServiceFactory getServiceFactory(){
//		return new MyODataServiceFactory("com.sap.dbs.dbx.i068191.bean");
//	}
//	
//	@Bean(name="com.sap.dbs.dbx.i068191.bean.PersonODataAgent")
//	public PersonODataAgent personODataAgent(){
//		log.info("return PersonODataAgent object");
//		return new PersonODataAgent();
//	}
}

//@WebServlet(urlPatterns = { "/one_odata.svc/*" })
//class OneODataServlet extends SimpleODataServlet {
//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 1L;
//
//	@Override
//	public void init(ServletConfig servletConfig) throws ServletException {
//		super.init(servletConfig);
//		setoDataServiceFactoryBeanName("MyODataServiceFactory");
//	}
//}
//
//@WebServlet(urlPatterns = { "/two_odata.svc/*" })
//class TwoODataServlet extends SimpleODataServlet {
//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 2L;
//
//	@Override
//	public void init(ServletConfig servletConfig) throws ServletException {
//		super.init(servletConfig);
//		setoDataServiceFactoryBeanName("MyODataServiceFactory");
//	}
//}

