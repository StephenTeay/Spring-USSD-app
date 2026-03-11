FROM openjdk:21-jdk
ADD target/ussd.jar ussd.jar
LABEL authors="Ayomide Taiwo"
ENTRYPOINT ["java","-jar","ussd.jar"]