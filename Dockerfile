FROM gradle:8.5-jdk17 AS build

COPY --chown=gradle:gradle . /home/gradle/project
WORKDIR /home/gradle/project

# gradlew 실행 권한 부여
RUN chmod +x ./gradlew

# 테스트 생략하고 빌드
RUN ./gradlew build -x test --no-daemon

# JAR 실행을 위한 이미지
FROM eclipse-temurin:17-jdk
EXPOSE 8080
COPY --from=build /home/gradle/project/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
