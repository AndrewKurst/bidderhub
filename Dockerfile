FROM bellsoft/liberica-openjdk-alpine:17 AS builder
COPY . .
RUN ./gradlew clean build

FROM bellsoft/liberica-openjdk-alpine:17
COPY --from=builder app/build/libs/app.jar /app.jar
CMD exec java ${JVM_OPTS} -jar /app.jar