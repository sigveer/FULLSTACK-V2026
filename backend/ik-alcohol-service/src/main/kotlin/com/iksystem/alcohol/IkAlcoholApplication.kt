package com.iksystem.alcohol

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(scanBasePackages = ["com.iksystem.common", "com.iksystem.alcohol"])
@EntityScan(basePackages = ["com.iksystem.common"])
@EnableJpaRepositories(basePackages = ["com.iksystem.common"])
class IkAlcoholApplication

fun main(args: Array<String>) {
    runApplication<IkAlcoholApplication>(*args)
}
