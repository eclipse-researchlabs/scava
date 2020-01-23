FROM openjdk:8

WORKDIR /opt/app

COPY build/libs/SearchWorkflow.jar SearchWorkflow.jar

ENTRYPOINT ["java", "-jar", "SearchWorkflow.jar"]