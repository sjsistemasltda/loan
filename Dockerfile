FROM jelastic/maven:3.9.5-openjdk-21 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -DskipTests

FROM openjdk:21-jdk
COPY --from=build /home/app/target/loan-0.0.1-SNAPSHOT.jar /usr/local/lib/loan.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/loan.jar"]