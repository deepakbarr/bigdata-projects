package com.geo.service;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

/**
 * Created by dbarr on 12/10/17.
 */

public class ServiceMain extends Application<ServiceConfig> {
    public static void main(String[] args) throws Exception {
        new ServiceMain().run(new String[]{args[0], args[1]});
//        new ServiceMain().run(new String[]{"server", "/Users/dbarr/coderep_2/Workspace_bigdata/bigdata-projects/geo-service/src/main/resources/config.yml"});
    }

    @Override
    public void initialize(Bootstrap<ServiceConfig> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/assets/", "/maps"));
    }

    @Override
    public void run(ServiceConfig conf, Environment env) throws Exception {

        KafkaManager.get().init(conf.getKafkaBroker());
        configureCors(env);
        ServiceResource svcResource = new ServiceResource();
        env.jersey().register(svcResource);
    }

    private void configureCors(Environment environment) {
        final FilterRegistration.Dynamic cors =
                environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin,Authorization");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "OPTIONS,GET,PUT,POST,DELETE,HEAD,PATCH");
        cors.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true");
        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

    }
}