package cn.allwayz.order.config;

import cn.allwayz.common.exception.BizCodeEnum;
import cn.allwayz.common.utils.R;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.*;

@ControllerAdvice
@Configuration
public class ThreadPoolConfig {

    /**
     * corePoolSize: Number of core threads
     * MaximumPoolSize: Maximum number of threads
     * KeepAliveTime: How long a thread other than the core thread is idle will be released
     * Unit: Time unit
     * BlockQueue: Blocking Queue where requests that exceed the maximum number of threads are placed in a blocking queue waiting to be executed
     * Factory: Thread pool creation factory
     * Handler: How other requests are handled once the blocking queue is full (remember to handle exceptions using the default policy)
     *      ThreadPoolExecutor. AbortPolicy default: throw an exception RejectedExecutionException
     *      ThreadPoolExecutor CallerRunsPolicy, direct execution threads call method, equivalent to synchronous execution
     *      ThreadPoolExecutor DiscardPolicy: abandon directly, not processing
     *      ThreadPoolExecutor. DiscardOldestPolicy: abandon the longest unprocessed requests (queue head), trying to execute a new request
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