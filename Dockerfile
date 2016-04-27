FROM docker.etermax.com/crackme-mvn-java8

MAINTAINER santiago@etermax.com

RUN mkdir app
WORKDIR app

COPY ./pom.xml .
RUN mvn compile

COPY ./src/main/java src/main/java
COPY ./src/test/java src/test/java
RUN mvn compile
RUN mvn verify

COPY ./src/main/resources src/main/resources

ENV CONVERSATIONS_CONFIG_PATH=src/main/resources/config.yml

EXPOSE 8080

CMD mvn exec:java -Dexec.args="server ${CONVERSATIONS_CONFIG_PATH}"
