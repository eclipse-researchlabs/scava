FROM openjdk:8

WORKDIR /opt/app

COPY build/libs/AckExample.jar AckExample.jar

ENTRYPOINT ["java", "-jar", "AckExample.jar"]