FROM openjdk:8

WORKDIR /opt/app

COPY build/libs/NBody.jar NBody.jar

ENTRYPOINT ["java", "-jar", "NBody.jar"]