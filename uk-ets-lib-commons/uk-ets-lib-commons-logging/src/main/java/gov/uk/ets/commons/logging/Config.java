package gov.uk.ets.commons.logging;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Configuration
@ComponentScan
@PropertySource("classpath:${envTarget:git}.properties")
@ConfigurationProperties(prefix = "")
@EnableConfigurationProperties
public class Config {

    @Value("${git.commit.id}")
    private String[] commitId;
    @Value("${git.commit.time}")
    private String[] commitTime;
}
