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
        org.eclipse.jetty.servlet.FilterHolder cors = new org.eclipse.jetty.servlet.FilterHolder(
                new CrossOriginFilter());

        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,PUT,DELETE,OPTIONS,HEAD");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "Content-Type,Authorization,Accept,Origin");
        cors.setInitParameter(CrossOriginFilter.EXPOSED_HEADERS_PARAM, "Content-Type,Authorization");
        cors.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true");
        cors.setInitParameter(CrossOriginFilter.PREFLIGHT_MAX_AGE_PARAM, "3600");

        context.addFilter(cors, "/*", EnumSet.of(DispatcherType.REQUEST));

        server.setHandler(context);

        // Register LoginServlet using ServletHolder
        context.addServlet(new ServletHolder(new LoginServlet()), "/api/login");
        context.addServlet(new ServletHolder(new CreateAccountServlet()), "/api/accounts");
        context.addServlet(new ServletHolder(new GetUserInfoServlet()), "/api/user/info");
        context.addServlet(new ServletHolder(new RecordsServlet()), "/api/records");
        context.addServlet(new ServletHolder(new SearchServlet()), "/api/search");
        context.addServlet(new ServletHolder(new DeleteServlet()), "/api/delete");
        context.addServlet(new ServletHolder(new OptionsServlet()), "/*");

        server.start();
        System.out.println("Jetty running at http://localhost:8080");
        server.join();
    }
}
