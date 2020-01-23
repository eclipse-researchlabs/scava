FROM openjdk:8

WORKDIR /opt/app

COPY build/libs/GitHubTechnologyAnalysis.jar GitHubTechnologyAnalysis.jar

ENTRYPOINT ["java", "-jar", "GitHubTechnologyAnalysis.jar"]