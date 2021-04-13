package cn.allwayz.cart.config;

import cn.allwayz.common.exception.BizCodeEnum;
import cn.allwayz.common.utils.R;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.*;

/**
 * @author allwayz
 */
@ControllerAdvice
@Configuration
public class ThreadPoolConfig {

    /**
     * corePoolSize: Number of core threads
     * maximumPoolSize: Maximum number of threads
     * keepAliveTime: How long a thread other than the core thread is idle will be freed
     * unit: Unit of time
     * blockQueue: Blocking queue. Requests that exceed the maximum number of threads are placed in a blocking queue waiting to be executed
     * factory: Thread pool creation factory
     * handler: How other requests are handled once the blocking queue is full (remember to handle exceptions using the default policy)
     *       ThreadPoolExecutor.AbortPolicy Default: Throw an exception RejectedExecutionException
     *       ThreadPoolExecutor.CallerRunsPolicy, Direct execution of the thread's call method, equivalent to synchronous execution
     *        ThreadPoolExecutor.DiscardPolicy：Throw it away without dealing with it
     *        ThreadPoolExecutor.DiscardOldestPolicy：Discard the longest unprocessed request (queue header) and attempt to execute a new request
     * @param properties
     * @return
     */
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolProperties properties) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                properties.getCoreSize(),
                properties.getMaximumSize(),
                properties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(properties.getBlockQueueSize()),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
        return executor;
    }

    @ResponseBody
    @ExceptionHandler({RejectedExecutionException.class})
    public R handler() {
        return R.error(BizCodeEnum.TOO_MANY_REQUEST.getCode(), BizCodeEnum.TOO_MANY_REQUEST.getMsg());
    }
}

