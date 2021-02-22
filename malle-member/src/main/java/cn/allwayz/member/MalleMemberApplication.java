package cn.allwayz.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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

	/**
	 * 放入一个密码加密器
	 * @return
	 */
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
