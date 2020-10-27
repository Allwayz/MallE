package cn.allwayz.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author allwayz
 */
@EnableDiscoveryClient
@MapperScan("cn.allwayz.order.dao")
@SpringBootApplication
public class MalleOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(MalleOrderApplication.class, args);
	}

}
