//package com.ontology.schedulers;
//
//import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
//import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
//import org.springframework.context.annotation.Bean;
//import org.springframework.scheduling.TaskScheduler;
//import org.springframework.scheduling.annotation.AsyncConfigurer;
//import org.springframework.scheduling.annotation.SchedulingConfigurer;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
//import org.springframework.scheduling.config.ScheduledTaskRegistrar;
//
//import java.util.concurrent.Executor;
//
//class BaseScheduler implements SchedulingConfigurer, AsyncConfigurer {
//    /*
//     * 并行任务
//     */
//    @Override
//    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
//        TaskScheduler taskScheduler = taskScheduler();
//        taskRegistrar.setTaskScheduler(taskScheduler);
//    }
//
//    /**
//     * 并行任务使用策略：多线程处理（配置线程数等）
//     *  
//     *
//     * @return ThreadPoolTaskScheduler 线程池
//     */
//    @Bean(destroyMethod = "shutdown")
//    public ThreadPoolTaskScheduler taskScheduler() {
//        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
//        scheduler.initialize();
//        scheduler.setPoolSize(10);
//        //设置线程名开头
//        scheduler.setThreadNamePrefix("task-");
//        //等待时常
////        scheduler.setAwaitTerminationSeconds(600);
//        //当调度器shutdown被调用时等待当前被调度的任务完成
//        scheduler.setWaitForTasksToCompleteOnShutdown(true);
//        //设置当任务被取消的同时从当前调度器移除的策略
//        scheduler.setRemoveOnCancelPolicy(true);
//        return scheduler;
//    }
//
//
//    /*
//     * 异步任务
//     */
//    @Override
//    public Executor getAsyncExecutor() {
//        Executor executor = taskScheduler();
//        return executor;
//    }
//
//    /*
//     * 异步任务 异常处理
//     */
//    @Override
//    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
//        return new SimpleAsyncUncaughtExceptionHandler();
//    }
//}
