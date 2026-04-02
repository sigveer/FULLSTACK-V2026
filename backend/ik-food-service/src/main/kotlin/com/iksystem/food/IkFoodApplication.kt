package com.iksystem.food

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(scanBasePackages = ["com.iksystem.common", "com.iksystem.food"])
@EntityScan(basePackages = ["com.iksystem.common"])
@EnableJpaRepositories(basePackages = ["com.iksystem.common"])
class IkFoodApplication

fun main(args: Array<String>) {
    runApplication<IkFoodApplication>(*args)
}
