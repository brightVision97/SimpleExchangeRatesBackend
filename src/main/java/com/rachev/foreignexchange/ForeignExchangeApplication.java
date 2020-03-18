package com.rachev.foreignexchange;

import static springfox.documentation.builders.PathSelectors.regex;
import static springfox.documentation.builders.RequestHandlerSelectors.any;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.concurrent.Executor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
@EnableAsync
public class ForeignExchangeApplication {

  public static void main(String[] args) {
    SpringApplication.run(ForeignExchangeApplication.class, args);
  }
  
  @Bean("asyncExecutor")
  public Executor asyncExecutor() {
    ThreadPoolTaskExecutor executor =  new ThreadPoolTaskExecutor();
    executor .setCorePoolSize(3);
    executor.setMaxPoolSize(3);
    executor.setQueueCapacity(100);
    executor.initialize();
    
    return executor;
  }

  @Bean
  public Docket docket() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(any())
        .paths(paths())
        .build();
  }

  private Predicate<String> paths() {
    return Predicates.not(regex("/error"));
  }

  @Bean
  public ObjectMapper objectMapper() {
    return new Jackson2ObjectMapperBuilder()
        .serializationInclusion(Include.NON_NULL)
        .serializationInclusion(Include.NON_EMPTY)
        .build()
        .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
        .configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
        .registerModule(new JavaTimeModule());
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
