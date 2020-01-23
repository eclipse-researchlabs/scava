FROM openjdk:8

WORKDIR /opt/app

COPY build/libs/RWorkflow.jar RWorkflow.jar

ENTRYPOINT ["java", "-jar", "RWorkflow.jar"]