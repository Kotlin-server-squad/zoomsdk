package com.kss.zoom.demo.webhooks.spring

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WebhooksDemoApplication

fun main(args: Array<String>) {
	runApplication<WebhooksDemoApplication>(*args)
}
