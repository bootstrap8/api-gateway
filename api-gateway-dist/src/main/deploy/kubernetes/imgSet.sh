#!/bin/bash

export op="k8s-apply"

if [[ -f "../setenv.sh" ]];then
. ../setenv.sh
fi

if [[ -z "$k8s_api_version" ]]; then
    export k8s_api_version=extensions/v1beta1
fi
echo "k8s_api_version:$k8s_api_version"

APP_NAME=api-gateway-manage-deployment
kubectl set image deploy ${APP_NAME} ${APP_NAME}=${docker_prefix}/api-gateway-img:${tag} -n ${k8s_ns}
APP_NAME=api-gateway-master-deployment
kubectl set image deploy ${APP_NAME} ${APP_NAME}=${docker_prefix}/api-gateway-img:${tag} -n ${k8s_ns}
