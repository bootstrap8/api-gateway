#!/bin/bash

export op="docker-build"

if [[ -f "../setenv.sh" ]];then
. ../setenv.sh
fi

name=api-gateway-img
ver=${tag}
tag=${name}:${ver}

echo "Stop Docker Containers ..."
cid=`docker ps -a|grep ${name}|grep ${ver}|awk '{print $1}'`
if [[ -n "${cid}" ]]; then
  docker rm -f ${cid}
  echo "${tag},${cid} was stop."
fi

echo "Uninstall Docker Images ..."
mid=`docker images|grep "${name}"|grep "${ver}"|awk '{print $3}'`
if [[ -n "${mid}" ]]; then
  docker rmi -f ${mid}
  echo "${tag},${mid} was uninstalled."
fi

if [[ "$platform" == "linux/arm64"* ]]; then
    cat Dockerfile | sed "s/FROM nexus.cmss.com/# FROM nexus.cmss.com/g" | sed "s/# FROM openjdk:8/FROM openjdk:8/g" > ../../Dockerfile
else
    cat Dockerfile | sed "s/FROM nexus.cmss.com/# FROM nexus.cmss.com/g" | sed "s/# FROM cicdcsy.harbor.cmss.com/FROM cicdcsy.harbor.cmss.com/g" > ../../Dockerfile
fi

#cp Dockerfile ../../
echo "Start Docker Images Building ..."
if [[ "$platform" == "linux/arm64"* ]]; then
  docker build --platform ${platform} -t ${docker_prefix}/${tag} ../../
else
  os=`uname -a|awk '{print $1}'`
  if [[ "$os" == "Darwin" ]]; then
    docker build --platform ${platform} -t ${docker_prefix}/${tag} ../../
  else
    docker build -t ${docker_prefix}/${tag} ../../
  fi
fi
