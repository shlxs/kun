package io.mountainsea.kun.mybatis.interceptor;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.core.toolkit.TableNameParser;
import com.baomidou.mybatisplus.extension.parser.JsqlParserSupport;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.*;
import org.springframework.dao.PermissionDeniedDataAccessException;

import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 校验该模块是否有这类表的访问权限，开发调试环境使用，生产不建议
 */
@Slf4j
public class TableAccessibleInterceptor extends JsqlParserSupport implements InnerInterceptor {

    private final Set<String> allowTableNames;

    private final Set<String> notAllowTableNames;

    private final boolean isThrowException;

    public TableAccessibleInterceptor(Set<String> allowTableNames, Set<String> notAllowTableNames, boolean isThrowException){
        Objects.requireNonNull(allowTableNames);
        Objects.requireNonNull(notAllowTableNames);
        this.allowTableNames = allowTableNames.stream().map(s -> s.toLowerCase()).collect(Collectors.toSet());
        this.notAllowTableNames = notAllowTableNames.stream().map(s -> s.toLowerCase()).collect(Collectors.toSet());
        this.isThrowException = isThrowException;
    }

    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        if(allowTableNames.size() > 0){
            PluginUtils.MPStatementHandler mpSh = PluginUtils.mpStatementHandler(sh);
            MappedStatement ms = mpSh.mappedStatement();
            SqlCommandType sct = ms.getSqlCommandType();
            if (sct == SqlCommandType.INSERT || sct == SqlCommandType.UPDATE || sct == SqlCommandType.DELETE || sct == SqlCommandType.SELECT) {
                PluginUtils.MPBoundSql mpBs = mpSh.mPBoundSql();
                this.validTable(mpBs.sql());
            }
        }
    }

    protected void validTable(String sql) {
        TableNameParser parser = new TableNameParser(sql);
        Collection<String> names = parser.tables();

        names.stream().map(name -> name.toLowerCase())
            .forEach(name -> {
            if(!allowTableNames.contains(name)){
                reportDenyTable(name);
            }

            if(!notAllowTableNames.contains(name)){
                reportDenyTable(name);
            }
        });
    }

    private void reportDenyTable(String name) {
        if(isThrowException){
            throw new PermissionDeniedException("该表禁止访问：" + name);
        }else{
            log.error("!=>   该表不建议访问{}", name);
        }
    }
}

