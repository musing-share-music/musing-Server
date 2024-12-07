# Amazon Corretto 19 이미지 사용
FROM amazoncorretto:19

# 작업 디렉토리를 설정
WORKDIR /app

# 파일 복사
COPY musing-0.0.1-SNAPSHOT.jar musing.jar

# 커맨드 실행
CMD ["java", "-jar", "musing.jar"]
