package com.example

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication


@SpringBootApplication
class Application

fun main(args: Array<String>) {
    println("시작")
    SpringApplication.run(Application::class.java, *args)
}