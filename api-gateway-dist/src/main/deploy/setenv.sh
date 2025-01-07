#!/bin/bash
export docker_prefix="docker.for.mac.host.internal:6000/workbench"
export docker_registry_user=""
export docker_registry_pwd=""
export k8s_ns="default"
export spring_cloud_zookeeper_enabled="false"
export spring_cloud_zookeeper_connectString=""
export spring_cloud_zookeeper_auth_info=""
export spring_profiles_active="default"

if [ "${op}" == "docker-build" ];then
echo "-- please <select/input> running platform --"
echo "1) x86"
echo "2) ARM"
echo "if you input Enter, will select 2"
echo
# shellcheck disable=SC2162
read -p "> " inputPlatform
if [ -z "${inputPlatform}" ]; then
  platform="linux/arm64"
else
  case ${inputPlatform} in
  1)
  platform="linux/amd64";
  ;;
  2)
  platform="linux/arm64";
  ;;
  *)
  platform=${platformNum}
  ;;
  esac
fi
echo "you selected running platform: ${platform}"
export platform=${platform}
echo
fi

if [ "${op}" == "docker-build" -o "${op}" == "docker-push" -o "${op}" == "docker-start" -o "${op}" == "k8s-apply" -o "${op}" == "k8s-delete" ];then
echo "-- please <select/input> docker registry --"
echo "1) docker.for.mac.host.internal:6000/workbench"
echo "2) 测试环境仓库，cicdcsy.harbor.cmss.com:18080/cloudsecuritycenter"
echo "if you input Enter, will select 1"
echo
# shellcheck disable=SC2162
read -p "> " inputDockerPrefix
if [ -z "${inputDockerPrefix}" ]; then
  docker_prefix="docker.for.mac.host.internal:6000/workbench"
else
case ${inputDockerPrefix} in
  1)
  docker_prefix="docker.for.mac.host.internal:6000/workbench";
  ;;
  2)
  docker_prefix="cicdcsy.harbor.cmss.com:18080/cloudsecuritycenter";
  ;;
  *)
  docker_prefix=${inputDockerPrefix}
  ;;
esac
fi
echo "you selected docker registry: ${docker_prefix}"
export docker_prefix=${docker_prefix}
echo
fi


if [ "${op}" == "bootstrap-start" -o "${op}" == "bootstrap-stop" -o "${op}" == "bootstrap-check" -o "${op}" == "ar" ]; then
echo
else
echo
echo "-- please <select/input> docker image tag --"
echo "1) current_date"
echo "2) current_hour"
echo "3) latest"
echo "if you input Enter, will select 3"
echo
# shellcheck disable=SC2162
read -p "> " inputTag
if [ -z "${inputTag}" ]; then
  tag="latest"
else
case ${inputTag} in
  1)
  tag="`date '+%Y%m%d'`"
  ;;
  2)
  tag="`date '+%Y%m%d%H'`"
  ;;
  3)
  tag="latest";
  ;;
  *)
  tag=${inputTag}
  ;;
esac
fi
echo "you input docker image tag: ${tag}"
export tag=${tag}
fi


if [ "${op}" == "docker-start" -o "${op}" == "k8s-apply" -o "${op}" == "k8s-delete" -o "${op}" == "k8s-check" -o "${op}" == "k8s-describe" ];then
echo "-- please <select/input> [spring.profiles.active] --"
echo "1) default"
echo "2) mysql"
echo "3) dev"
echo "4) dev-k8s"
echo "5) st-nine"
echo "6) dmz-3az"
echo "if you input Enter, will select 1"
echo
# shellcheck disable=SC2162
read -p "> " inputProfile
if [ -z "${inputProfile}" ]; then
  spring_profiles_active="default"
else
case ${inputProfile} in
  1)
  spring_profiles_active="default";
  ;;
  2)
  spring_profiles_active="mysql";
  ;;
  3)
  spring_profiles_active="dev";
  ;;
  4)
  spring_profiles_active="dev-k8s";
  ;;
  5)
  spring_profiles_active="st-nine";
  ;;
  6)
  spring_profiles_active="dmz-3az";
  ;;
  *)
  spring_profiles_active=${inputProfile}
  ;;
esac
fi
echo "you input [spring.profiles.active]: ${spring_profiles_active}"
export spring_profiles_active=${spring_profiles_active}
fi



if [ "${op}" == "k8s-apply" -o "${op}" == "k8s-delete" -o "${op}" == "k8s-check" -o "${op}" == "k8s-describe" ];then
echo "-- please <select/input> [kubernetes namespace] --"
echo "1) default (本地集群)"
echo "if you input Enter, will select 3"
echo
# shellcheck disable=SC2162
read -p "> " inputKNS
if [ -z "${inputKNS}" ]; then
  k8s_ns="default"
else
case ${inputKNS} in
  1)
  k8s_ns="default";
  ;;
  *)
  k8s_ns=${inputKNS}
  ;;
esac
fi
echo "you input kubernetes namespace: ${k8s_ns}"
export k8s_ns=${k8s_ns}
fi

if [ "${op}" == "k8s-apply" -o "${op}" == "k8s-delete" ];then
echo "-- please <select/input> deploy way [NodePort/ClusterIP] --"
echo "1) NodePort"
echo "2) ClusterIP"
echo "if you input Enter, will select 1"
echo
# shellcheck disable=SC2162
read -p "> " inputDeployWay
if [ -z "${inputDeployWay}" ]; then
  yamlFile="service-nodeport.yaml"
else
  case ${inputDeployWay} in
  1)
  yamlFile="service-nodeport.yaml"
  ;;
  2)
  yamlFile="service-clusterip.yaml"
  ;;
  *)
  yamlFile=${inputDeployWay}
  ;;
  esac
fi
echo "you selected deploy way: ${yamlFile}"
export yamlFile=${yamlFile}
echo
fi