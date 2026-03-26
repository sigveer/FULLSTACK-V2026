package com.iksystem.ik_mat

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.iksystem"])
class IkMatApplication

fun main(args: Array<String>) {
    runApplication<IkMatApplication>(*args)
}
