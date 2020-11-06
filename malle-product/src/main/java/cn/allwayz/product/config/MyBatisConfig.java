package cn.allwayz.product.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author allwayz
 */
@Configuration
@EnableTransactionManagement
@MapperScan("cn.allwayz.product.dao")
public class MyBatisConfig {
    /**
     * PagesPlugin
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // Set the requested page to be larger than the maximum post page operation, true returns to the first page, false continues to request the default false
        paginationInterceptor.setOverflow(true);
        // Sets the maximum number of pages, default is 500, -1 is not limited
        paginationInterceptor.setLimit(500);
        return paginationInterceptor;
    }
}
