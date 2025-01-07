ProcessNo=`ps -ef|grep api-gateway-master|grep -v grep|awk '{print $2}'`
if [ -n "${ProcessNo}" ];then
echo "kill ${ProcessNo}"
kill ${ProcessNo}
fi

ProcessNo=`ps -ef|grep api-gateway-manage|grep -v grep|awk '{print $2}'`
if [ -n "${ProcessNo}" ];then
echo "kill ${ProcessNo}"
kill ${ProcessNo}
fi
exit;