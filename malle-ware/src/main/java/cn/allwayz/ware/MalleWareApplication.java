package cn.allwayz.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author allwayz
 */
@EnableRabbit
@EnableFeignClients
@EnableDiscoveryClient
@MapperScan("cn.allwayz.ware.dao")
@SpringBootApplication
public class MalleWareApplication {

	public static void main(String[] args) {
		SpringApplication.run(MalleWareApplication.class, args);
	}

}
