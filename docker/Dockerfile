from docker.io/maven:3.9-eclipse-temurin-25 AS build

RUN mkdir /opt/build
WORKDIR /opt/build
COPY pom.xml pom.xml
#Pull dependancies early so we can cache them in a seperate layer and prevent re-downloading all of it everytime we build.
#Not perfect but this will fetch most of them.
RUN mvn -B dependency:resolve-plugins dependency:resolve
COPY src src
RUN mvn clean package
RUN mv target/backend-*.jar /opt/launchpad.jar

ENTRYPOINT ["mv", "/opt/launchpad.jar", "/opt/output/launchpad.jar"]