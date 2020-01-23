FROM openjdk:8

WORKDIR /opt/app

COPY build/libs/WordCountWorkflow.jar WordCountWorkflow.jar

ENTRYPOINT ["java", "-jar", "WordCountWorkflow.jar"]