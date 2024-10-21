package com.bangbangbwa.backend.global.config;

import com.bangbangbwa.backend.global.config.log.SqlLogInterceptor;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Configuration
@RequiredArgsConstructor
public class MyBatisConfig {

  private final DataSource dataSource;

  @Bean
  public SqlSessionFactory sqlSessionFactory() throws Exception {
    SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
    sqlSessionFactoryBean.setDataSource(dataSource);
    sqlSessionFactoryBean.setPlugins(new SqlLogInterceptor());

    Resource configLocation = new PathMatchingResourcePatternResolver().getResource(
        "classpath:mybatis/sqlmap-config.xml");
    Resource[] mapperLocations = new PathMatchingResourcePatternResolver().getResources(
        "classpath:mybatis/map/*.xml");
    sqlSessionFactoryBean.setConfigLocation(configLocation);
    sqlSessionFactoryBean.setMapperLocations(mapperLocations);
    sqlSessionFactoryBean.setTypeAliasesPackage("com.bangbangbwa.backend.domain");

    return sqlSessionFactoryBean.getObject();
  }
}
