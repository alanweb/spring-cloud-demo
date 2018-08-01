#!/bin/sh

#--------------------------------------------
# 功能：启动通知服务alan-message.jar
# by xxq
#--------------------------------------------


#输入参数判断
#if [ ! -n "$1" ] ;then
#    echo "you have not input a script param! you can input such as:'sh start.sh prod' or 'sh start.sh test' or 'sh start.sh dev'"
#    exit
#fi




echo "you will start server with properties file:'application.properties'"
echo "please waiting ...."


#------------------变量设置--------------------------#
#项目jar
PROJECT_NAME="alan-message.jar"
#start.sh脚本所在目录
SH_WORKDIR=$(dirname $0)
cd ${SH_WORKDIR}
SH_WORKDIR=`pwd`
#micro-notify.jar路径
PROJECT_JAR_PATH="${SH_WORKDIR}/lib/${PROJECT_NAME}"
#conf路径
PROJECT_CONF_PATH="${SH_WORKDIR}/conf/"
#启动log
START_APPEND_LOG="${SH_WORKDIR}/logs/stdout.log"

#------------------启动--------------------------#

#------------ -判断文件是否存在------------------#
if [ ! -d "${PROJECT_CONF_PATH}" ];then
 echo "${PROJECT_CONF_PATH} dont't exist! "
 exit
fi

#stop server关闭进程
PIDS=(`ps -ef|grep ${PROJECT_JAR_PATH}|grep -v grep|awk {'print $2'}`)
#if [[ ${PIDS} ]] ;then
#   for s in ${PIDS[@]}
#   do
#      kill -9 $s
#   done
#fi

if [ -n "$PIDS" ]; then
    echo "ERROR: The ${SERVER_NAME} already started!"
    echo "PID: $PIDS"
    exit 1
fi



#启动项目
nohup nice java -d64 -server -Xms256m -Xmx2048m -XX:-DisableExplicitGC -XX:+UseG1GC -XX:+PrintGCDetails -XX:+PrintGCApplicationConcurrentTime -XX:+PrintGCApplicationStoppedTime -XX:+PrintGCDateStamps -Xloggc:logs/gc.log -XX:+HeapDumpOnOutOfMemoryError -XX:CompileThreshold=1500 -jar -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 -Dspring.config.location=${PROJECT_CONF_PATH}  ${PROJECT_JAR_PATH} > ${START_APPEND_LOG} 2>&1 &

COUNT=0
while [ $COUNT -lt 1 ]; do    
    echo -e ".\c"
    sleep 1 
    COUNT=`ps -f | grep java | grep "${SH_WORKDIR}" | awk '{print $2}' | wc -l`
    if [ $COUNT -gt 0 ]; then
        break
    fi
done

echo "OK!"
PIDS=`ps -f | grep java | grep "${SH_WORKDIR}" | awk '{print $2}'`
echo "PID: $PIDS"
echo "STDOUT: $START_APPEND_LOG"
