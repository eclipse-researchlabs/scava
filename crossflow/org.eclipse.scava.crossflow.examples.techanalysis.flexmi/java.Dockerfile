FROM openjdk:8

WORKDIR /opt/app

COPY build/libs/TechnologyAnalysis.jar TechnologyAnalysis.jar

ENTRYPOINT ["java", "-jar", "TechnologyAnalysis.jar"]