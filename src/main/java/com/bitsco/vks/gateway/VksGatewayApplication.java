package com.bitsco.vks.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.core.env.Environment;

@SpringBootApplication(scanBasePackages = "com.bitsco.*")
@EnableZuulProxy
public class VksGatewayApplication {

    public static void main(String[] args) {
        Environment env = SpringApplication.run(VksGatewayApplication.class, args).getEnvironment();
        String appName = env.getProperty("spring.application.name").toUpperCase();
        String port = env.getProperty("server.port");
        System.out.println("-------------------------START " + appName + " Application------------------------------");
        System.out.println("   Application         : " + appName);
        System.out.println("   Url swagger-ui      : http://localhost:" + port + "/swagger-ui.html");
        System.out.println("-------------------------START SUCCESS " + appName + " Application------------------------------");
    }

}
