package io.mountainsea.kun.util.executor;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author : ShaoHongLiang
 * @date : 2022/11/10
 */
public class TracingThreadPoolExecutor extends ThreadPoolTaskExecutor {
    private static TracingTaskDecorator tracingTaskDecorator = new TracingTaskDecorator();

    public TracingThreadPoolExecutor(){
        super();
        this.setTaskDecorator(tracingTaskDecorator);
    }

}
