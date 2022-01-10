package com.ambrosia.nymph.configs

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.zalando.problem.jackson.ProblemModule
import org.zalando.problem.violations.ConstraintViolationProblemModule

@Configuration
class ObjectMapperConfiguration {
    @Bean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper().registerModules(
            ProblemModule().withStackTraces(false),
            ConstraintViolationProblemModule()
        )
    }
}
