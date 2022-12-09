package io.mountainsea.kun.autoconfigure;

import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.boot.actuate.audit.InMemoryAuditEventRepository;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author shaohongliang
 * @date 2021/12/13 17:57
 */
@Configuration
public class ActuatorCustomizeAutoConfiguration {

    /**
     * 提供 actuator/auditevents 审计日志
     * @return
     */
    @Bean
    public AuditEventRepository auditEventRepository(){
        AuditEventRepository auditEventRepository = new InMemoryAuditEventRepository();
        return auditEventRepository;
    }


    /**
     * 提供 actuator/httptrace 跟踪日志
     * @return
     */
    @Bean
    public HttpTraceRepository httpTraceRepository(){
        HttpTraceRepository httpTraceRepository = new InMemoryHttpTraceRepository();
        return httpTraceRepository;
    }


}
