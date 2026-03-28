package com.iksystem

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

/**
 * Spring Boot entry point for the IK System backend.
 *
 * Explicitly scans `com.iksystem` for JPA entities and repositories
 * to ensure classes in backtick-escaped sub-packages are discovered.
 */
@SpringBootApplication
@EntityScan(basePackages = ["com.iksystem"])
@EnableJpaRepositories(basePackages = ["com.iksystem"])
class IkSystemApplication

/** Bootstraps the Spring Boot application. */
fun main(args: Array<String>) {
    runApplication<IkSystemApplication>(*args)
}
