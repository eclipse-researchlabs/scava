FROM openjdk:8

WORKDIR /opt/app

COPY build/libs/TechrankWorkflow.jar TechrankWorkflow.jar

ENTRYPOINT ["java", "-jar", "TechrankWorkflow.jar"]