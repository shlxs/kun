package io.mountainsea.kun.observability.tracing;

import io.mountainsea.kun.observability.logging.mvc.HttpLogHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.protocol.HTTP;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * 禁用
 * spring:
 *   web:
 *     resources:
 *       add-mappings: false
 *   mvc:
 *     throw-exception-if-no-handler-found: true
 * 使得所有请求在过滤器处理完后不会进入BaseErrorController处理逻辑，从而避免二次经过拦截器
 *
 * @author : ShaoHongLiang
 * @date : 2022/11/15
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class TraceAwareFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest)servletRequest;
            // 开启请求
            TracingUtils.begin(httpServletRequest);
            // 打印请求日志
            InputStream bodyInputStream = HttpLogHelper.printRequestInfo(httpServletRequest);
            // 执行责任链
            if(Objects.isNull(bodyInputStream)){
                filterChain.doFilter(servletRequest, servletResponse);
            }else{

            }
        }finally {
            // 结束请求
            TracingUtils.finish();
        }
    }

}