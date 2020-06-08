package com.alexquasar.supplierPriceLoader.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.sql.DataSource;

@Configuration
@EnableJpaAuditing
public class DatabaseConfig {

    @ConfigurationProperties(prefix = "alex.datasource")
    @Bean
    public DataSource dataSource() {
        return new HikariDataSource();
    }
}
