# ===== Runtime image (Java 21, Alpine) ====
FROM eclipse-temurin:21-jre-alpine

# 타임존
ENV TZ=Asia/Seoul
RUN apk add --no-cache tzdata \
  && cp /usr/share/zoneinfo/${TZ} /etc/localtime \
  && echo "${TZ}" > /etc/timezone

# 앱 작업 디렉터리
WORKDIR /app

# 빌드 아티팩트 경로
ARG JAR_FILE=build/libs/board-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080

# SPRING_PROFILES_ACTIVE 가 없으면 prod로
# .env 로 받은 환경변수(SPRING_DATASOURCE_*, JWT_SECRET 등) 자동 주입됨
ENTRYPOINT ["sh","-c","java -Duser.timezone=Asia/Seoul -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod} -jar /app/app.jar"]
