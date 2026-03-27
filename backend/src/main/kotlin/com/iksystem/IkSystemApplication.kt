package com.iksystem

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EntityScan(basePackages = ["com.iksystem"])
@EnableJpaRepositories(basePackages = ["com.iksystem"])
class IkSystemApplication

fun main(args: Array<String>) {
    runApplication<IkSystemApplication>(*args)
}
