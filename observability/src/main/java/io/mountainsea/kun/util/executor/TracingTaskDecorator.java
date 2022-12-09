package io.mountainsea.kun.util.executor;

import io.mountainsea.kun.observability.logging.mdc.MDCBridge;
import io.mountainsea.kun.observability.tracing.TracingThreadLocal;
import org.springframework.core.task.TaskDecorator;

import java.util.HashMap;
import java.util.Map;

/**
 * 装饰线程池运行方法
 * @author : ShaoHongLiang
 * @date : 2022/11/10
 */
public class TracingTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable task) {
        HashMap<TracingThreadLocal<?>, Object> parentCaptured = TracingThreadLocal.capture();
        Map<String, String> mdcParentCaptured = MDCBridge.capture();

        return () -> {
            HashMap<TracingThreadLocal<?>, Object> childCaptured = TracingThreadLocal.capture();
            Map<String, String> mdcChildCaptured = MDCBridge.capture();
            TracingThreadLocal.restore(parentCaptured);
            MDCBridge.restore(mdcParentCaptured);
            try {
                task.run();
            } finally {
                MDCBridge.restore(mdcChildCaptured);
                TracingThreadLocal.restore(childCaptured);
            }
        };
    }
}
