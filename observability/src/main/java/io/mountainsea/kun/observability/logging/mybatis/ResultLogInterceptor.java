package io.mountainsea.kun.observability.logging.mybatis;

import io.mountainsea.kun.mybatis.MybatisPlusWholeInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.SQLException;
import java.util.List;

@Slf4j
public class ResultLogInterceptor implements MybatisPlusWholeInterceptor.PostInterceptor {


    @Override
    public void afterQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql, List<?> queryResult) throws SQLException {
        log.info("<==   查询返回行数：{}", queryResult.size());
    }

    @Override
    public void afterUpdate(Executor executor, MappedStatement ms, Object parameter, int updateCount) throws SQLException {
        log.info("<==   更新影响行数：{}", updateCount);
    }


}

