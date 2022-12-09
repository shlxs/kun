package io.mountainsea.kun.mybatis;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import lombok.Setter;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : ShaoHongLiang
 * @since : 2022/11/9
 */
@Intercepts(
        {
                @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}),
                @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        }
)
public class MybatisPlusWholeInterceptor extends MybatisPlusInterceptor {

    @Setter
    private List<PostInterceptor> postInterceptors = new ArrayList<>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object result = super.intercept(invocation);
        Object target = invocation.getTarget();
        Object[] args = invocation.getArgs();
        if (target instanceof Executor) {
            final Executor executor = (Executor) target;
            Object parameter = args[1];
            boolean isUpdate = args.length == 2;
            MappedStatement ms = (MappedStatement) args[0];
            if (!isUpdate && ms.getSqlCommandType() == SqlCommandType.SELECT) {
                RowBounds rowBounds = (RowBounds) args[2];
                ResultHandler<?> resultHandler = (ResultHandler<?>) args[3];
                BoundSql boundSql;
                if (args.length == 4) {
                    boundSql = ms.getBoundSql(parameter);
                } else {
                    // 几乎不可能走进这里面,除非使用Executor的代理对象调用query[args[6]]
                    boundSql = (BoundSql) args[5];
                }
                for (PostInterceptor postInterceptor : postInterceptors) {
                    postInterceptor.afterQuery(executor, ms, parameter, rowBounds, resultHandler, boundSql, (List<?>)result);
                }
            } else if (isUpdate) {
                for (PostInterceptor postInterceptor : postInterceptors) {
                    postInterceptor.afterUpdate(executor, ms, parameter, (int)result);
                }
            }
        }
        return result;
    }

    public void addPostInterceptor(PostInterceptor postInterceptor) {
        this.postInterceptors.add(postInterceptor);
    }


    /**
     * MyBatis后置处理器
     * @author : ShaoHongLiang
     * @date : 2022/11/9
     */
    @SuppressWarnings({"rawtypes"})
    public interface PostInterceptor {

        /**
         * {@link Executor#query(MappedStatement, Object, RowBounds, ResultHandler, CacheKey, BoundSql)} 操作前置处理
         * <p>
         * 改改sql啥的
         *
         * @param executor      Executor(可能是代理对象)
         * @param ms            MappedStatement
         * @param parameter     parameter
         * @param rowBounds     rowBounds
         * @param resultHandler resultHandler
         * @param boundSql      boundSql
         */
        default void afterQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql, List<?> queryResult) throws SQLException {
            // do nothing
        }

        /**
         * {@link Executor#update(MappedStatement, Object)} 操作前置处理
         * <p>
         * 改改sql啥的
         *
         * @param executor  Executor(可能是代理对象)
         * @param ms        MappedStatement
         * @param parameter parameter
         */
        default void afterUpdate(Executor executor, MappedStatement ms, Object parameter, int updateCount) throws SQLException {
            // do nothing
        }

    }

}

