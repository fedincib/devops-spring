FROM openjdk:8-jdk-alpine
EXPOSE 8089
ADD target/pifinity-0.0.1.jar pifinity-0.0.1.jar

ENTRYPOINT ["java","-jar","/ pifinity-0.0.1.jar"]