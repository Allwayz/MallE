package cn.allwayz.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author allwayz
 */
@MapperScan("cn.allwayz.ware.dao")
@SpringBootApplication
public class MalleWareApplication {

	public static void main(String[] args) {
		SpringApplication.run(MalleWareApplication.class, args);
	}

}
