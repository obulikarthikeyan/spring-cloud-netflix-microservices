package io.pivotal.microservices.hystrix.monitor;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@EnableEurekaClient
@EnableHystrixDashboard
@ComponentScan("io.pivotal.microservices.hystrix.monitor")
public class HystrixDashboardServer {
	public static void main(String[] args) {
		SpringApplication.run(HystrixDashboardServer.class, args);
	}
	
	@Bean
    public CloseableHttpAsyncClient httpAsyncClient() {
        CloseableHttpAsyncClient httpClient = HttpAsyncClients.createDefault();
        httpClient.start();
        return httpClient;
    }
}
