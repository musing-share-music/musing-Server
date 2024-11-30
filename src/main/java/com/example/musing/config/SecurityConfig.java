package com.example.musing.config;

import com.example.musing.auth.filter.TokenAuthenticationFilter;
import com.example.musing.auth.filter.TokenExceptionFilter;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
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


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    public final TokenAuthenticationFilter tokenAuthenticationFilter;
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        //해당 부분은 필터를 거치지 않게 설정
        return web -> web.ignoring().dispatcherTypeMatchers(DispatcherType.ERROR).requestMatchers("/error")
                .requestMatchers("swagger-UI");
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .cors(AbstractHttpConfigurer::disable) //cors 차단
                .csrf(AbstractHttpConfigurer::disable) // csrf 차단, jwt사용으로 차단
                // oauth2 사용으로 기존 시큐리티 로그인 페이지 차단
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                //JWT 사용으로 세션 사용하지않음
                .sessionManagement(c ->
                        c.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용하지 않음
                //X-Frame 차단 (타 사이트 iframe 및 오브젝트 등등 접근 차단)
                .headers(c -> c.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable).disable())
                .authorizeHttpRequests((authorize) -> authorize
                        //스웨거 UI 접속 전체 허용(배포할때 주석처리 해야함)
                        .requestMatchers("/swagger-ui").permitAll()//스웨거
                        //사용자 전체 허용
                        .requestMatchers("/musing/main").permitAll()
                        .requestMatchers("/musing/signup").permitAll() //회원가입 도메인 1단계가 필요없이 구글 로그인 부분으로 되면 이렇게 적용
                        .requestMatchers("/musing/notice/**").permitAll()
                        .requestMatchers("/musing/rec-music-list/**").permitAll()
                        //관리자 전용 도메인 허용
                        .requestMatchers("/musing/register/**").hasRole("Admin")
                        //나머지 도메인 허용 필요(회원,관리자)
                        .anyRequest().authenticated())

                //oauth2 인증 관련 코드 추가하기

                //JWT 관련 설정, 하단 필터 실행
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new TokenExceptionFilter(), tokenAuthenticationFilter.getClass());

                //인증 관련 커스텀 예외처리 추가하기

        return http.build();
    }

}
