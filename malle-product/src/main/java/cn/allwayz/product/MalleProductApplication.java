package cn.allwayz.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @author allwayz
 */
@EnableRedisHttpSession
@EnableCaching
@EnableTransactionManagement
@EnableFeignClients(basePackages = "cn.allwayz.product.feign")
@EnableDiscoveryClient
@MapperScan("cn.allwayz.product.dao")
@SpringBootApplication
public class MalleProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(MalleProductApplication.class, args);
	}

}
