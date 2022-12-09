package io.mountainsea.kun.observability.logging.mybatis;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.core.enums.IEnum;
import com.baomidou.mybatisplus.core.parser.SqlParserHelper;
import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
public class SqlLogInterceptor implements InnerInterceptor {

    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        PluginUtils.MPStatementHandler mpStatementHandler = PluginUtils.mpStatementHandler(sh);
        MappedStatement ms = mpStatementHandler.mappedStatement();
        BoundSql boundSql = mpStatementHandler.boundSql();
        log.info("==>   执行Mapper: {}", ms.getId());
        log.info("==>   MySQL: {}", this.showSql(ms.getConfiguration(), boundSql));
    }

    /**
     * 替换参数
     *
     * @param boundSql sql脚本对象
     * @return 返回完整sql
     */
    private String showSql(Configuration configuration, BoundSql boundSql) {
        // 去除多余空格换行符
        String sql = boundSql.getSql().replaceAll("\\s+", " ");

        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappings();
        Object parameterObject = boundSql.getParameterObject();
        if (CollectionUtil.isNotEmpty(parameterMappingList) && Objects.nonNull(parameterObject)) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            MetaObject metaObject = configuration.newMetaObject(parameterObject);

            for(ParameterMapping param : parameterMappingList){
                if (param.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = param.getProperty();
                    if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else {
                        value = metaObject.getValue(propertyName);
                    }
                    sql = sql.replaceFirst("\\?", ParameterHandler.getValue(value, param.getTypeHandler()));
                }
            }
        }

        return sql;
    }


    /**
     * 参数助手
     */
    private static class ParameterHandler {
        /**
         * 日期时间格式化
         */
        private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        /**
         * 日期格式化
         */
        private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        /**
         * 时间格式化
         */
        private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

        /**
         * 获取参数值
         *
         * @param value       参数值
         * @param typeHandler 类型助手
         * @return 返回参数值
         */
        static String getValue(Object value, TypeHandler typeHandler) {
            if (value == null) {
                return null;
            } else if (value instanceof String) {
                return "'" + value + "'";
            } else if (value instanceof Date) {
                return "'" + dateFormat((Date) value, typeHandler) + "'";
            } else if (value instanceof TemporalAccessor) {
                return "'" + localDateFormat((Temporal) value, typeHandler) + "'";
            } else if (value instanceof Enum) {
                return enumValue(((Enum) value), typeHandler).toString();
            } else {
                return value.toString();
            }
        }

        /**
         * 日期格式化
         *
         * @param date        日期对象
         * @param typeHandler 类型助手
         * @return 返回格式化后字符串
         */
        static String dateFormat(Date date, TypeHandler<?> typeHandler) {
            LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            if (typeHandler instanceof DateOnlyTypeHandler) {
                return localDateTime.format(DATE_FORMATTER);
            } else if (typeHandler instanceof TimeOnlyTypeHandler) {
                return localDateTime.format(TIME_FORMATTER);
            } else {
                return localDateTime.format(DATE_TIME_FORMATTER);
            }
        }

        /**
         * 日期格式化
         *
         * @param temporal    日期对象
         * @param typeHandler 类型助手
         * @return 返回格式化后字符串
         */
        static String localDateFormat(TemporalAccessor temporal, TypeHandler<?> typeHandler) {
            if (temporal instanceof LocalDate && typeHandler instanceof LocalDateTypeHandler) {
                return LocalDate.from(temporal).format(DATE_FORMATTER);
            } else if (temporal instanceof LocalTime && typeHandler instanceof LocalTimeTypeHandler) {
                return LocalTime.from(temporal).format(TIME_FORMATTER);
            } else if (temporal instanceof LocalDateTime && typeHandler instanceof LocalDateTimeTypeHandler) {
                return LocalDateTime.from(temporal).format(DATE_TIME_FORMATTER);
            } else {
                return temporal.toString();
            }
        }

        /**
         * 枚举格式化
         *
         * @param obj 枚举对象
         * @return
         */
        static Object enumValue(Enum obj, TypeHandler<?> typeHandle) {
            Object value = null;
            if (obj instanceof IEnum) {
                value = ((IEnum) obj).getValue();
            }
            if (Objects.isNull(value)) {
                Field[] fields = ReflectUtil.getFields(obj.getDeclaringClass());
                for (Field field : fields) {
                    Annotation[] declaredAnnotations = field.getDeclaredAnnotations();
                    for (Annotation annotation : declaredAnnotations) {
                        if (annotation instanceof EnumValue) {
                            value = ReflectUtil.getFieldValue(obj, field);
                        }
                    }
                }
            }

            if (value instanceof String) {
                return "'" + value + "'";
            } else if (value instanceof Date) {
                return "'" + dateFormat((Date) value, typeHandle) + "'";
            } else if (value instanceof TemporalAccessor) {
                return "'" + localDateFormat((Temporal) value, typeHandle) + "'";
            } else {
                return value;
            }
        }
    }
}

