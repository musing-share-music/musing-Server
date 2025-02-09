package com.example.musing.config;

import com.example.musing.auth.Oauth2.CustomOauth2UserService;
import com.example.musing.auth.handler.CustomAccessDeniedHandler;
import com.example.musing.auth.handler.CustomAuthenticationEntryPoint;
import com.example.musing.auth.handler.OAuth2FailureHandler;
import com.example.musing.auth.handler.Oauth2SuccessHandler;
import com.example.musing.auth.filter.TokenAuthenticationFilter;
import com.example.musing.auth.filter.TokenExceptionFilter;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    public final TokenAuthenticationFilter tokenAuthenticationFilter;
    public final CustomOauth2UserService userService;
    private final Oauth2SuccessHandler oauth2SuccessHandler;

    @Value("${client.host}")
    private String clientHost;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        //해당 부분은 필터를 거치지 않게 설정
        return web -> web.ignoring().dispatcherTypeMatchers(DispatcherType.ERROR)
                .requestMatchers("/error","/favicon.ico") //에러페이지는 필터 안거치게 설정
/*                .requestMatchers("/youtube/search/**") //에러페이지는 필터 안거치게 설정*/
                .requestMatchers("/swagger", "/swagger-ui.html", "/swagger-ui/**", "/api-docs", "/api-docs/**", "/v3/api-docs/**");
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable) // csrf 차단, jwt사용으로 차단
                // oauth2 사용으로 기존 시큐리티 로그인 페이지 차단
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable) //로그아웃할때 쿠키 삭제하는거 해야할듯
                //JWT 사용으로 세션 사용하지않음
                .sessionManagement(c ->
                        c.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 세션 사용하지 않음
                //X-Frame 차단 (타 사이트 iframe 및 오브젝트 등등 접근 차단)
                .headers(c -> c.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable).disable())
                .authorizeHttpRequests((authorize) -> authorize
                        //스웨거 UI 접속 전체 허용(배포할때 주석처리 해야함)
                        .requestMatchers("/swagger", "/swagger-ui.html", "/swagger-ui/**", "/api-docs", "/api-docs/**", "/v3/api-docs/**").permitAll()
                        //사용자 전체 허용
                        .requestMatchers("/musing/main").permitAll()
                        .requestMatchers("/musing/signup").permitAll() //회원가입 도메인 1단계가 필요없이 구글 로그인 부분으로 되면 이렇게 적용
                        .requestMatchers("/musing/notice/**").permitAll()
                        .requestMatchers("/musing/rec-music-list/**").permitAll()
                        //관리자 전용 도메인 허용
                        .requestMatchers("/musing/admin/**").hasRole("Admin")
                        //나머지 도메인 허용 필요(회원,관리자)
                        .anyRequest().authenticated())

                //oauth2 인증 관련 코드
                .oauth2Login(oauth ->
                                oauth.userInfoEndpoint(c -> c.userService(userService))
                                        .successHandler(oauth2SuccessHandler)
                                        .failureHandler(new OAuth2FailureHandler())
                        )
                .logout(auth -> auth
                        .logoutUrl("/musing/logout")
                        .deleteCookies("accessToken")
                        .logoutSuccessUrl(clientHost)
                )
                   //JWT 관련 설정, 하단 필터 실행
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new TokenExceptionFilter(), tokenAuthenticationFilter.getClass())

                //인증 관련 커스텀 예외처리 추가하기
                 .exceptionHandling((exceptions) -> exceptions
                    .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                    .accessDeniedHandler(new CustomAccessDeniedHandler()));
        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin(clientHost);
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.addAllowedHeader("Set-Cookie");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
