# 빌드 스테이지 - 명시적으로 amd64 플랫폼 지정
FROM --platform=linux/amd64 gradle:8.5-jdk21 AS build
WORKDIR /app

# Gradle 의존성 레이어 최적화
COPY settings.gradle build.gradle ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon

# 소스 코드 복사 및 빌드
COPY src ./src
RUN gradle build --no-daemon -x test

# 실행 스테이지 - 명시적으로 amd64 플랫폼 지정
FROM --platform=linux/amd64 eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
