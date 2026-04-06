package com.iksystem.food

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication(scanBasePackages = ["com.iksystem.common", "com.iksystem.food"])
@EnableScheduling
@EntityScan(basePackages = ["com.iksystem.common", "com.iksystem.food"])
@EnableJpaRepositories(basePackages = ["com.iksystem.common", "com.iksystem.food"])
class IkFoodApplication

fun main(args: Array<String>) {
    runApplication<IkFoodApplication>(*args)
}
