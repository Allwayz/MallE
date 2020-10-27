package cn.allwayz.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author allwayz
 */
@EnableFeignClients(basePackages = "cn.allwayz.member.feign")
@EnableDiscoveryClient
@MapperScan("cn.allwayz.member.dao")
@SpringBootApplication
public class MalleMemberApplication {

	public static void main(String[] args) {
		SpringApplication.run(MalleMemberApplication.class, args);
	}

}
