#!/bin/bash

JAVA_OPTIONS="-DAPP_HOME=`pwd`"

while read option
do
JAVA_OPTIONS=${option}" "${JAVA_OPTIONS}
done < ./jvm.options

JAVA_OPTIONS=${JAVA_OPTIONS}" -DAPP_HOME=/opt/app/${APP_SN}"

java -Xmx512m -Xms512m -DAPP_SN=api-gateway-manage ${JAVA_OPTS} -jar ../../manage/lib/api-gateway-manage-1.0.jar &
sleep 5
java -Xmx512m -Xms512m -DAPP_SN=api-gateway-master ${JAVA_OPTS} -jar ../../master/lib/api-gateway-master-1.0.jar
