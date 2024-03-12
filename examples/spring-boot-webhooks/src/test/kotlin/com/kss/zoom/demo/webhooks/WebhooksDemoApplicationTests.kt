package com.kss.zoom.demo.webhooks

import com.kss.zoom.demo.webhooks.spring.WebhooksDemoApplication
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [WebhooksDemoApplication::class])
class WebhooksDemoApplicationTests {

	@Test
	fun contextLoads() {
	}

}
