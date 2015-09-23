#!/bin/bash

portNumber=5554
emulatorIP="localhost"
#emulatorPID=`ps -ef | grep "emulator" | grep "port 5554" | awk '{print $2;}'` 
emulatorPID=`netstat -nltp | grep "5554"`

if [ -n "$emulatorPID" ];then
while [ -n "$emulatorPID" ]
do
    echo "emulator port exits, open another port.."
    portNumber=$[$portNumber+2]
    emulatorPID=`netstat -nltp | grep "$portNumber"`
done
fi


echo $portNumber

   # kill -9 $emulatorPID    
############################## 
AvdName=`cat conf.txt | grep 'AvdName'|awk -F ":" '{print $2}'`
RAM=`cat conf.txt | grep 'Ram'|awk -F ":" '{print $2}'`
model=`cat conf.txt | grep 'NetworkModel'|awk -F ":" '{print $2}'`
Skin=`cat conf.txt | grep 'Skin'|awk -F ":" '{print $2}'`
Power=`cat conf.txt | grep 'Power'|awk -F ":" '{print $2}'`

#################################create AVD###############################
# create all avds in createAVD.sh

#################################start emulator###############################

echo $AvdName

echo "______________start emulator_________________"
emulator -avd $AvdName -port $portNumber -no-audio -memory $RAM &

sleep 8

echo "_________________check whether the emulator is ready___________"
num="emulator-$portNumber"
ready=`adb devices | grep "$num"`
echo $ready

if [ -z "$ready" ];then
	echo "emulator is not lunch."
    else
	ready=`adb devices | grep "$num.*offline"`
	

	if [ -n "$ready" ];then
		echo "reay but offline"

		adb -s $num wait-for-device
		echo "wait for devices.." 
		sleep 8
	fi
		
echo "emulator is lunching.......,waiting for running"
	sleep 5m
echo "install cpu and ram apk ."
	 adb -s $num install /root/zwb/EmulatorPreference.apk
   	 adb -s $num push /root/zwb/conf.txt /sdcard/emulator.conf
   	 adb -s $num shell am start -n "com.alibaba.mts.emulator/.MainActivity"

echo "setting networkmodem......."
		case "$model" in 
	   	 "2G")
			(sleep 1;echo 'network speed gsm';echo 'quit')|telnet $emulatorIP $portNumber
			echo "The network model is setted to 2G"
			;;
		"3G")
			(sleep 1;echo 'network speed umts';echo 'quit')|telnet $emulatorIP $portNumber
			echo "The network model is setted to 3G"
			;;
		"3.5G")
			(sleep 1;echo 'network speed hsdpa';echo 'quit')|telnet $emulatorIP $portNumber
			echo "The network model is setted to 3.5G"
			;;
    "4G")
			(sleep 1;echo 'network speed 20480 81920';echo 'quit')|telnet $emulatorIP $portNumber
			echo "The network model is setted to 4G"
			;;
    "WIFI")
			(sleep 1;echo 'network speed full';echo 'quit')|telnet $emulatorIP $portNumber
			echo "The network model is setted to WIFI"
			;;
		*)
			echo "please offer parameter"
			;;
		esac	
 
fi

echo "setting power capacity to $power"
(sleep 1;echo "power capacity $Power";echo 'quit')|telnet $emulatorIP $portNumber


