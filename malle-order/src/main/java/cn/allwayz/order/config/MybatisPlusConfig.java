package cn.allwayz.order.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.optimize.JsqlParserCountOptimize;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author allwayz
 */
@Configuration
@MapperScan("cn.allwayz.order.dao")
public class MybatisPlusConfig {

    /**
     * Configure the paging plug-in
     *
     * @return
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // Set the requested page to be larger than the maximum page size, true to return to the front page, false to continue to request default false
        // paginationInterceptor.setOverflow(false);
        // Set the maximum number of pages per page, default is 500, -1 is unrestricted
        // paginationInterceptor.setLimit(500);
        // Enable count join optimization on partial left joins
        paginationInterceptor.setCountSqlParser(new JsqlParserCountOptimize(true));
        return paginationInterceptor;
    }
}
