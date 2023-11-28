package gov.uk.ets.itl.webservices.endpoint;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.startup.Tomcat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    @Value("${axis.path}")
    private String axisPath;

    /**
     * Main method to initialize the Spring Boot application.
     *
     * @param args The command line arguments
     */
    public static void main(final String[] args) {
        SpringApplication.run(Application.class);
    }

    @Bean
    public TomcatServletWebServerFactory servletContainerFactory() {
        return new TomcatServletWebServerFactory() {
            @Override
            protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {                
                // webapps directory does not exist by default, needs to be created
                new File(tomcat.getServer().getCatalinaBase(), "webapps").mkdirs();
                // Add a war with given context path
                // Can add multiple wars this way with different context paths
                Context appContext = tomcat.addWebapp(axisPath, new File("target/webapps/itl-ws.war").getAbsolutePath());
                appContext.setParentClassLoader(Thread.currentThread().getContextClassLoader());
                return super.getTomcatWebServer(tomcat);
            }
        };
    }
}
