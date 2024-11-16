FROM openjdk:8-jdk-alpine
EXPOSE 8084
ADD target/pifinity-0.0.4.jar pifinity-0.0.4.jar

ENTRYPOINT ["java","-jar","/ pifinity-0.0.4.jar"]