# 1단계: Gradle로 JAR 빌드
FROM gradle:8.5-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/project
WORKDIR /home/gradle/project
RUN ./gradlew build -x test --no-daemon

# 2단계: JAR 실행을 위한 이미지 구성
FROM eclipse-temurin:17-jdk
EXPOSE 8080
COPY --from=build /home/gradle/project/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]