package cn.allwayz.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author allwayz
 */
@MapperScan("cn.allwayz.order.dao")
@SpringBootApplication
public class MalleOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(MalleOrderApplication.class, args);
	}

}
