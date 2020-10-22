package cn.allwayz.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author allwayz
 */
@MapperScan("cn.allwayz.product.dao")
@SpringBootApplication
public class MalleProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(MalleProductApplication.class, args);
	}

}
