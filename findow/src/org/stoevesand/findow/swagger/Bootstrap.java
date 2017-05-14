package org.stoevesand.findow.swagger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stoevesand.findow.provider.figo.FigoBankingAPI;

import io.swagger.jaxrs.config.BeanConfig;

public class Bootstrap extends HttpServlet {
	
	private Logger log = LoggerFactory.getLogger(Bootstrap.class);

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        log.info("Init Bootstrap for Swagger");
        
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("1.0.1");
        beanConfig.setTitle("findow");
        beanConfig.setSchemes(new String[]{"https", "http"});
        //beanConfig.setHost("localhost:8080");
        beanConfig.setBasePath("/findow/v1");
        beanConfig.setResourcePackage("org.stoevesand.findow.rest");
        beanConfig.setScan(true);
    }
}