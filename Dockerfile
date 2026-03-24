FROM amazoncorretto:21-alpine as build1

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew

RUN ./gradlew clean build -x test

FROM amazoncorretto:21-alpine

ENV SPRING_PROFILES_ACTIVE=docker

ENV TZ=Asia/Seoul

RUN apk add --no-cache curl tzdata
COPY --from=build1 /app/build/libs/*.jar app.jar

ENTRYPOINT ["sh", "-c", "exec java $JVM_OPTS -jar app.jar"]