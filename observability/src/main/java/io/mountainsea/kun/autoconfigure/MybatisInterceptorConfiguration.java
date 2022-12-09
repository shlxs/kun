package io.mountainsea.kun.autoconfigure;

import io.mountainsea.kun.mybatis.MybatisPlusWholeInterceptor;
import io.mountainsea.kun.observability.logging.mybatis.ResultLogInterceptor;
import io.mountainsea.kun.observability.logging.mybatis.SqlLogInterceptor;
import io.mountainsea.kun.mybatis.interceptor.TableAccessibleInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Set;

/**
 * @author : ShaoHongLiang
 * @date : 2022/11/4
 */
@Configuration
public class MybatisInterceptorConfiguration {

    @Value("${mybatis.permission.allowed-tables:}")
    private Set<String> mybatisAllowedTables;

    @Value("${mybatis.permission.not-allowed-tables:}")
    private Set<String> mybatisNotAllowedTables;

    @Value("${mybatis.permission.throw-exception-on-denied:false}")
    private boolean mybatisThrowException;

    @Bean
    public MybatisPlusWholeInterceptor mybatisPlusInterceptor(List<MybatisPlusWholeInterceptor.PostInterceptor> postInterceptors, List<InnerInterceptor> interceptors){
        MybatisPlusWholeInterceptor wholeInterceptor = new MybatisPlusWholeInterceptor();
        postInterceptors.forEach(interceptor -> wholeInterceptor.addPostInterceptor(interceptor));
        interceptors.forEach(interceptor -> wholeInterceptor.addInnerInterceptor(interceptor));
        return wholeInterceptor;
    }

    @Bean
    public ResultLogInterceptor resultLogInterceptor(){
        return new ResultLogInterceptor();
    }

    @Bean
    public SqlLogInterceptor sqlLogInterceptor(){
        return new SqlLogInterceptor();
    }

    @Bean
    public TableAccessibleInterceptor tableAllowedInterceptor(){
        return new TableAccessibleInterceptor(mybatisAllowedTables, mybatisNotAllowedTables, mybatisThrowException);
    }
}