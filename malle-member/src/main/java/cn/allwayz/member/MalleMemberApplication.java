package cn.allwayz.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author allwayz
 */
@MapperScan("cn.allwayz.member.dao")
@SpringBootApplication
public class MalleMemberApplication {

	public static void main(String[] args) {
		SpringApplication.run(MalleMemberApplication.class, args);
	}

}
