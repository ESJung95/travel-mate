package com.eunsun.travel_mate.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  // http://localhost:8080/swagger-ui/index.html

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Travel Mate - 여행 친구")
            .description("누구나 쉽게 여행 정보를 얻을 수 있도록 도와주고,\n"
                + "일정표를 작성하는 기능을 제공하는 서비스입니다.")
            .version("1.0")
        );
  }
}