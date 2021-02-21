package cn.allwayz.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "cn.allwayz.auth.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class MalleAuthServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MalleAuthServerApplication.class, args);
	}

}
