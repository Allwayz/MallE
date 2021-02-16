package cn.allwayz.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
//@EnableRedisHttpSession
public class MalleSearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(MalleSearchApplication.class, args);
	}

}
