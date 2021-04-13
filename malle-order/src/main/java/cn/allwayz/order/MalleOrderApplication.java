package cn.allwayz.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author allwayz
 */
@EnableFeignClients(basePackages = {"cn.allwayz.order.feign"})
//@MapperScan("cn.allwayz.order.dao")
@EnableDiscoveryClient
@SpringBootApplication
public class MalleOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(MalleOrderApplication.class, args);
	}

}
