#!/bin/sh

#--------------------------------------------
# 功能：停止通知服务 alan-message.jar
# by xxq
#--------------------------------------------


PROJECT_NAME="alan-message.jar"
#stop.sh脚本所在目录
SH_WORKDIR=$(dirname $0)
cd ${SH_WORKDIR}
SH_WORKDIR=`pwd`
#oc-front.jar路径
PROJECT_JAR_PATH="${SH_WORKDIR}/lib/${PROJECT_NAME}"

#------------------关闭服务--------------------------#
#stop server
PIDS=(`ps -ef|grep ${PROJECT_JAR_PATH}|grep -v grep|awk {'print $2'}`)



if [ -z "$PIDS" ]; then
    echo "ERROR: The $PROJECT_NAME does not started!"
    exit 1
fi


echo -e "Stopping the $PROJECT_NAME ...\c"
for PID in $PIDS ; do
    kill $PID > /dev/null 2>&1
done

COUNT=0
while [ $COUNT -lt 1 ]; do    
    echo -e ".\c"
    sleep 1
    COUNT=1
    for PID in $PIDS ; do
        PID_EXIST=`ps -f -p $PID | grep java`
        if [ -n "$PID_EXIST" ]; then
            COUNT=0
            break
        fi
    done
done

echo "OK!"
echo "PID: $PIDS"





