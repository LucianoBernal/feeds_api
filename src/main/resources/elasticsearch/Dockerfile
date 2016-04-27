FROM anapsix/alpine-java:jdk8

MAINTAINER santiago@etermax.com

ENV ELASTIC_VERSION=2.3.1

RUN apk update
RUN apk add curl
RUN apk add procps

RUN adduser -S elasticsearch

RUN wget https://download.elastic.co/elasticsearch/release/org/elasticsearch/distribution/tar/elasticsearch/${ELASTIC_VERSION}/elasticsearch-2.3.1.tar.gz
RUN mv elasticsearch-${ELASTIC_VERSION}.tar.gz elasticsearch.tar.gz
RUN tar -zxvf elasticsearch.tar.gz
RUN mv elasticsearch-${ELASTIC_VERSION} elasticsearch

RUN chown -R elasticsearch elasticsearch

USER elasticsearch
WORKDIR elasticsearch

COPY ./create_index.sh .
COPY ./reset_index.sh .
COPY ./elasticsearch.yml config/elasticsearch.yml

EXPOSE 9200
EXPOSE 9300

CMD bin/elasticsearch