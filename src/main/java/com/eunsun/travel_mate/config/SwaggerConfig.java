package com.eunsun.travel_mate.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

/**
 * Swagger UI에서 인증이 필요한 엔드포인트를 테스트하려면 다음 단계를 따르세요:
 *
 * 1. Swagger UI 상단의 ⭐자물쇠 모양⭐ "Authorize" 버튼을 클릭합니다.
 * 2. "Available authorizations" 창에서 "bearerAuth"를 선택하고 "Authorize" 버튼을 클릭합니다.
 * 3. "Value" 입력란에 유효한 JWT 토큰을 입력한 후 "Authorize" 버튼을 클릭하고 "Close" 버튼을 클릭합니다.
 *
 * 이제 Swagger UI에서 보내는 모든 요청에 입력한 JWT 토큰이 "Authorization: Bearer <token>" 형식으로 포함됩니다.
 */

@OpenAPIDefinition(
    info = @Info(
        title = "Travel Mate - 여행 친구",
        description = "누구나 쉽게 여행 정보를 얻을 수 있도록 도와주고,\n" +
            "일정표를 작성하는 기능을 제공하는 서비스입니다.",
        version = "1.0"
    ),
    security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer",
    in = SecuritySchemeIn.HEADER
)
public class SwaggerConfig {
  // ...
}