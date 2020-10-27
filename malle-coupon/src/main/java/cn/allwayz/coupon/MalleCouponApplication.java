package cn.allwayz.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author allwayz
 */
@EnableDiscoveryClient
@MapperScan("cn.allwayz.coupon.dao")
@SpringBootApplication
public class MalleCouponApplication {

	public static void main(String[] args) {
		SpringApplication.run(MalleCouponApplication.class, args);
	}

}
