FROM openjdk:21-jdk as builder
ARG JAR_FILE=target/*.jar
COPY ./target/authenticify-1.0.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

ENV AUTHENTICIFY_DATABASE_USERNAME=default_username
ENV AUTHENTICIFY_DATABASE_PASSWORD=default_password
ENV AUTHENTICIFY_DATABASE_URL=default_url
ENV AUTHENTICIFY_MAIL_HOST=default_host
ENV AUTHENTICIFY_MAIL_PORT=default_port
ENV AUTHENTICIFY_MAIL_USERNAME=default_username
ENV AUTHENTICIFY_MAIL_PASSWORD=default_password
