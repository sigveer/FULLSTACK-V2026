package com.iksystem.alcohol

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication(scanBasePackages = ["com.iksystem.common", "com.iksystem.alcohol"])
@EnableScheduling
@EntityScan(basePackages = ["com.iksystem.common", "com.iksystem.alcohol"])
@EnableJpaRepositories(basePackages = ["com.iksystem.common", "com.iksystem.alcohol"])
class IkAlcoholApplication

fun main(args: Array<String>) {
    runApplication<IkAlcoholApplication>(*args)
}
