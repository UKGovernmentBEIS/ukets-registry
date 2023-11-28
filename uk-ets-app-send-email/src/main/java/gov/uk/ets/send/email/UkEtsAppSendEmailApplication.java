package gov.uk.ets.send.email;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class UkEtsAppSendEmailApplication {

    public static void main(String[] args) {
        SpringApplication.run(UkEtsAppSendEmailApplication.class, args);
    }
}
