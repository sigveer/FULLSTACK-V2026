package com.iksystem.ik_alkohol

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.iksystem"])
class IkAlkoholApplication

fun main(args: Array<String>) {
    runApplication<IkAlkoholApplication>(*args)
}
