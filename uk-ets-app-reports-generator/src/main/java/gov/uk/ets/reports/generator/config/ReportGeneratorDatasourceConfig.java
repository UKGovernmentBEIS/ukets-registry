package gov.uk.ets.reports.generator.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@Configuration
public class ReportGeneratorDatasourceConfig {

    @Bean("registryDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public JdbcTemplate jdbcTemplate(@Qualifier("registryDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(@Qualifier("registryDataSource") DataSource ds) {
        return new NamedParameterJdbcTemplate(ds);
    }

    @Bean("keycloakDataSource")
    @ConfigurationProperties(prefix = "spring.keycloak-datasource")
    public DataSource keycloakDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean("keycloakJdbcTemplate")
    public JdbcTemplate keycloakJdbcTemplate(@Qualifier("keycloakDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }
}
