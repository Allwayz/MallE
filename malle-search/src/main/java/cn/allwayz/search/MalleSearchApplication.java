package cn.allwayz.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class MalleSearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(MalleSearchApplication.class, args);
	}

}
