#!/bin/bash
id=$1
pid=`netstat -nltp | grep "$id" | awk '{print $7}' | awk -F "/" '{print $1}'`

kill -9 $pid
sleep 3
adb devices
