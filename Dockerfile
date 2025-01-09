# Amazon Corretto 19 이미지 사용
FROM amazoncorretto:19

# 작업 디렉토리를 설정
# 2. 애플리케이션 JAR 복사
ARG JAR_FILE=build/libs/musing-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# 3. 포트 노출 (스프링 부트 기본 포트)
EXPOSE 8080

# 4. 애플리케이션 실행 명령
ENTRYPOINT ["java", "-jar", "app.jar"]

