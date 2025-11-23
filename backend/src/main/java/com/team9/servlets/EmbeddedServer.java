package com.team9.servlets;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import jakarta.servlet.DispatcherType;
import java.util.EnumSet;

public class EmbeddedServer {
    public static void main(String[] args) throws Exception {

        Server server = new Server(8080);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
	org.eclipse.jetty.servlet.FilterHolder cors =
    	new org.eclipse.jetty.servlet.FilterHolder(new CrossOriginFilter());

	cors.setInitParameter("allowedOrigins", "*");
	cors.setInitParameter("allowedMethods", "GET,POST,HEAD,OPTIONS");
	cors.setInitParameter("allowedHeaders", "*");

	context.addFilter(cors, "/*", EnumSet.of(DispatcherType.REQUEST));
        server.setHandler(context);

        // Register LoginServlet using ServletHolder
        context.addServlet(new ServletHolder(new LoginServlet()), "/api/login");
	context.addServlet(new ServletHolder(new CreateAccountServlet()), "/api/accounts");
	context.addServlet(new ServletHolder(new GetUserInfoServlet()), "/api/user/info");  
	context.addServlet(new ServletHolder(new RecordsServlet()), "/api/records");
	context.addServlet(new ServletHolder(new SearchServlet()), "/api/search");


        server.start();
        System.out.println("Jetty running at http://localhost:8080");
        server.join();
    }
}
