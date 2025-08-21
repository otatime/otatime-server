FROM openjdk:17-jdk-alpine
VOLUME /tmp
COPY build/libs/*.jar otatime-server.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/otatime-server.jar"]
