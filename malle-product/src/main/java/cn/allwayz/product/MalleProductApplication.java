package cn.allwayz.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author allwayz
 */
@EnableDiscoveryClient
@SpringBootApplication
public class MalleProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(MalleProductApplication.class, args);
	}

}
