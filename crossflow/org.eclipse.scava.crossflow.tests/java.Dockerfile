FROM openjdk:8

WORKDIR /opt/app

COPY build/libs/AckWorkflow.jar AckWorkflow.jar

ENTRYPOINT ["java", "-jar", "AckWorkflow.jar"]