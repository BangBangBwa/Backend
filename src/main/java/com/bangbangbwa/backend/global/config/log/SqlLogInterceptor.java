package com.bangbangbwa.backend.global.config.log;

import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;

@Slf4j
@Intercepts(value = {
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class,
        Object.class}),
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
        RowBounds.class, ResultHandler.class})}
)
public class SqlLogInterceptor implements Interceptor {

  private static final String LOG_FORMAT = "\n\t\t";

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    MappedStatement statement = (MappedStatement) invocation.getArgs()[0];
    Object param = invocation.getArgs().length > 1 ? invocation.getArgs()[1] : null;

    BoundSql boundSql = statement.getBoundSql(param);
    Configuration configuration = statement.getConfiguration();

    String paramSql = getParamBindSQL(configuration, boundSql);

    String sb = LOG_FORMAT
        + "/*------------------------------ SQL LOG -----------------------------------*/"
        + LOG_FORMAT + "RESOURCE: " + statement.getResource()
        + LOG_FORMAT + "SQL_ID: " + statement.getId()
        + LOG_FORMAT + "SQL: " + paramSql
        + LOG_FORMAT
        + "/*--------------------------------------------------------------------------*/";
    log.debug(sb);

    long start = System.currentTimeMillis();
    Object returnValue = invocation.proceed();
    log.debug("SQL processed in : {} ms", System.currentTimeMillis() - start);
    return returnValue;
  }

  private String getParamBindSQL(Configuration configuration, BoundSql boundSql) {
    Object parameterObject = boundSql.getParameterObject();
    List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
    String sql = boundSql.getSql();

    if (!parameterMappings.isEmpty() && Objects.nonNull(parameterObject)) {
      TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
      if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) { // 단일 파라미터 처리
        sql = sql.replaceFirst("\\?", getParamValue(parameterObject));
      } else { // 복합 파라미터 처리
        MetaObject metaObject = configuration.newMetaObject(parameterObject);
        for (ParameterMapping parameterMapping : parameterMappings) {
          String propertyName = parameterMapping.getProperty();
          if (metaObject.hasGetter(propertyName)) {
            Object obj = metaObject.getValue(propertyName);
            sql = sql.replaceFirst("\\?", getParamValue(obj));
          } else if (boundSql.hasAdditionalParameter(propertyName)) {
            Object obj = boundSql.getAdditionalParameter(propertyName);
            sql = sql.replaceFirst("\\?", getParamValue(obj));
          }
        }
      }
    }

    return sql;
  }

  private String getParamValue(Object obj) {
    if (obj instanceof String) {
      return Matcher.quoteReplacement("'" + obj + "'");
    } else {
      return Matcher.quoteReplacement(Objects.isNull(obj) ? "null" : obj.toString());
    }
  }

  @Override
  public Object plugin(Object target) {
    return Interceptor.super.plugin(target);
  }

  @Override
  public void setProperties(Properties properties) {
    Interceptor.super.setProperties(properties);
  }
}
