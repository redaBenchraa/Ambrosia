package com.ambrosia.nymph.configs

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.datasource.init.DataSourceInitializer
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import javax.sql.DataSource

@Configuration
class DatasourceConfig {
    @Bean
    fun dataSourceInitializer(@Qualifier("dataSource") dataSource: DataSource): DataSourceInitializer? {
        val resourceDatabasePopulator = ResourceDatabasePopulator().apply { addScript(ClassPathResource("script.sql")) }
        return DataSourceInitializer().apply {
            setDataSource(dataSource)
            setDatabasePopulator(resourceDatabasePopulator)
        }
    }
}
