package com.ontology.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Configuration
@EnableAsync
public class TaskExecutorConfig {

    @Value("${threadPoolSize.max}")
    protected int THREADPOOLSIZE_MAX;

    @Value("${threadPoolSize.core}")
    protected int THREADPOOLSIZE_CORE;

    @Value("${threadPoolSize.queue}")
    protected int THREADPOOLSIZE_QUEUE;

    @Value("${threadPoolSize.keepalive}")
    protected int THREADPOOLSIZE_KEEPALIVE_SECOND;

    @Bean(name = "synTaskExecutor")
    public AsyncTaskExecutor taskExecutor() {
        log.info("########synTaskExecutor#########");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(THREADPOOLSIZE_MAX);
        executor.setCorePoolSize(THREADPOOLSIZE_CORE);
        executor.setQueueCapacity(THREADPOOLSIZE_QUEUE);
        executor.setThreadNamePrefix("AsyncTaskThread--");
        executor.setKeepAliveSeconds(THREADPOOLSIZE_KEEPALIVE_SECOND);

        // Rejection policies
/*		executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
				logger.error("###########reject thread....");
				// .....
			}
		});*/
        //调用者的线程会执行该任务,如果执行器已关闭,则丢弃
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();

        return executor;
    }


}
