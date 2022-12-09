package io.mountainsea.kun.observability.logging.mdc;

import io.mountainsea.kun.observability.tracing.TracingStatus;
import org.slf4j.MDC;

import java.util.Map;

/**
 * 覆盖Adapter或者自定义Adapter方案都有缺陷，因而借助TaskDecorator传递
 *
 * @author : ShaoHongLiang
 * @date : 2022/11/16
 */
public class MDCBridge {

    /**
     * 日志跟踪id名。
     */
    public static final String LOG_TRACE_ID = "trace_id";

    // 设置跟踪ID
    public static void setMDCFields(TracingStatus traceStatus) {
        MDC.put(LOG_TRACE_ID, traceStatus.getContextId());
    }

    public static void removeMDCFields(TracingStatus traceStatus) {
        MDC.remove(LOG_TRACE_ID);
    }

    public static Map<String, String> capture() {
        return MDC.getCopyOfContextMap();
    }

    public static void restore(Map<String, String> captured) {
        MDC.setContextMap(captured);
    }
}
