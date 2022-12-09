package io.mountainsea.kun.observability.tracing;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import io.mountainsea.kun.observability.logging.mdc.MDCBridge;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * @author shaohongliang
 * @since 2022-11-15
 */
@Slf4j
public class TracingUtils {
    public static final String HTTP_HEADER_TRACE_ID = "app_trace_id";

    private static final ThreadLocal<TracingStatus> tracingThreadLocal = new TracingThreadLocal<>();

    public static String getContextId() {
        TracingStatus traceStatus = tracingThreadLocal.get();
        if(traceStatus == null){
            throw new IllegalStateException("Trace跟踪未开启");
        }
        return traceStatus.getContextId();
    }

    private static String generateId(HttpServletRequest httpServletRequest) {

        if(httpServletRequest != null){
            String httpHeaderTraceId = httpServletRequest.getHeader(HTTP_HEADER_TRACE_ID);
            if (StringUtils.hasLength(httpHeaderTraceId)){
                return httpHeaderTraceId;
            }
        }
        String left = StrUtil.subPre(UUID.fastUUID().toString(true), 8);
        String format = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return format + "_" + left.toUpperCase();
    }


    public static boolean begin() {
        return begin(null);
    }

    public static boolean begin(HttpServletRequest httpServletRequest) {
        TracingStatus traceStatus = tracingThreadLocal.get();
        if(traceStatus != null){
            throw new IllegalStateException("Trace跟踪无法重复开启");
        }

        traceStatus = new TracingStatus();
        // 生成TraceID
        traceStatus.setContextId(generateId(httpServletRequest));
        // 开启计时器
        traceStatus.setStopwatch(Stopwatch.createStarted());

        MDCBridge.setMDCFields(traceStatus);
        tracingThreadLocal.set(traceStatus);

        log.info("链路 {} 开始", traceStatus.getContextId());
        return true;
    }

    /**
     * 返回耗时统计，单位毫秒
     * @return
     */
    public static void finish() {
        TracingStatus traceStatus = tracingThreadLocal.get();
        if(traceStatus == null){
            throw new IllegalStateException("Trace跟踪未开启");
        }
        // 结束计时器
        long elapsed = traceStatus.getStopwatch().elapsed(TimeUnit.MILLISECONDS);

        // 结束的处理顺序与开始时相反
        log.info("链路 {} 结束, 耗时:{} ms", traceStatus.getContextId(), elapsed);

        tracingThreadLocal.remove();
        MDCBridge.removeMDCFields(traceStatus);
    }

}


