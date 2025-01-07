FROM openjdk:8

MAINTAINER default

ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /opt/app/api-gateway

ENV LANG=C.UTF-8

COPY / .

# ENTRYPOINT ["/bin/bash", "/opt/app/api-gateway/deploy/docker/start.sh"]
