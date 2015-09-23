//import com.android.ddmlib.IDevice;
//import com.android.ddmlib.IDevice.DeviceState;
//import com.android.ddmlib.ShellCommandUnresponsiveException
import com.android.ddmlib.*;
import org.apache.commons.logging.LogFactory
import java.lang.Exception

class EmulatorRelatedLib{


//def resultJsonMap=[:];
def  static TAG="EmulatorRelated"

def static logger = LogFactory.getLog(EmulatorRelatedLib.class);

def static log(tag, assetNumber, msg) {
	logger.info("${tag}: ${assetNumber} - ${msg}");
}
def static IDevice idevice
def static sleep(time) {
	try {
		Thread.sleep(time);
		} catch (e) {
			e.printStackTrace();
		}
	}
//def static toJSONString(map){
//	return JSON.toJSONString(map);
//	}

//str is toJSONString(map)
 //def generateResultJson( tag, assetNumber, caseResultDir, str ){
//	File f = new File((String)caseResultDir + "/result.json")
//	log(INFO, tag, assetNumber,"Result file path: ${f.absolutePath}\nresult json context:${str}")
//	writer = new PrintWriter(f)
//	writer.println(str)
//	writer.close()
//}


//create avd
/*
avdName is the avd's name you create   eg.MQCavd
target is the android API level 	   eg.19�?1�?2
skin is the emulator's skin            eg.Horizontal screen:640x480   Vertical screen:480x640
sdCardSize                             eg.128M
assetNumber is the id of the emulator  eg.emulator-5554
retry is used for retry createavd and retry startEmulator
abi is the linux kernel                eg.armeabi-v7a    x86
*/
def static createMyAVD(avdName,target,skin,sdCardSize,assetNumber,abi){
	def retry=0
	def createAvdCmd
	while(retry<3) {
		def pro1 = "echo no".execute()
		if(abi.equals("arm")){
		createAvdCmd = "android create avd --name "+avdName+" "+"--force --target android-"+target+" --abi armeabi-v7a --skin "+skin+" -c "+sdCardSize;
}
		else{
		createAvdCmd = "android create avd --name "+avdName+" "+"--force --target android-"+target+" --abi x86 --skin "+skin+" -c "+sdCardSize;
}
		def pro2 = createAvdCmd.execute()   	
		pro1 | pro2
		pro2.waitFor()
		def pro3 = "android list avd".execute()
		def pro4 = "grep ${avdName}".execute()
		pro3 | pro4
		pro4.waitFor()
		def result = pro4.getText()
		if(result==null || result==""){
			retry++
		}
		else
		break
	}
	if (retry==3) {
		log(TAG,assetNumber,"create AVD ${avdName} failure..")
		return false
	}
	else{
		log(TAG,assetNumber,"create AVD ${avdName} success..")
		return true	
	}
}

//start Emulator
/*
avdName is the avd's name you create         eg.MQCavd
portNumber is the port the emulator use      eg.5554�?556
ramSize is the memory size of the emulator   eg.1024
assetNumber is the id of the emulator        eg.emulator-5554
*/

def static startEmulator(avdName,portNumber,ramSize,assetNumber,abi,adb){
	def retry = 0
	def pro			
	Thread.start{
		while(retry<3){
			if(abi.equals("arm")){
			pro ="emulator -avd ${avdName} -port ${portNumber} -memory ${ramSize} -noaudio -nowindow -gpu on".execute() 
}			else{
			pro ="emulator -avd ${avdName} -port ${portNumber} -memory ${ramSize} -noaudio -gpu on -qemu -m 1024 -enable-kvm".execute()
}       
			pro.waitFor()
			sleep(5000)
			def  pro1 = "adb devices".execute()
			pro1.waitFor()
			def  pro2 = "grep ${portNumber}".execute()
			pro1 | pro2
			pro2.waitFor()	
			def result = pro2.getText()
			if(result==null || result==""){
				retry++
			}
			else
			break
		}
	}
	def waitTime=0
	while( waitTime < 300000){
		def  pro1 = "adb devices".execute()
		def  pro2 = "grep ${portNumber}.device".execute()
		pro1 | pro2
		pro2.waitFor()	
		def result = pro2.getText()
		if(result==null || result==""){
//wait 5s and retry checking wether the emulator is online
			waitTime+=5000
			sleep(5000)	
		}
		else{
//sleep 1min 40s until the screen is waken up for testing the incomingCall.Sometimes the period is long. 
			sleep(200000)
			break
		}		
	}
	println waitTime
	if(waitTime>=300000){
		log(TAG,assetNumber,"start avd  Emulator ${avdName} failure..")
                return null
		}
	else{
		IDevice[] devices = adb.getDevices();
		for (IDevice device : devices) {
		if(device.getSerialNumber().equals("emulator-${portNumber}")){
				idevice=device
				}			
		break
	}	
		log(TAG,assetNumber,"start emulator ${avdName} success..")
		return idevice
	}
}

//close Emulator
/*
portNumber is the port the emulator use      eg.5554�?556
assetNumber is the id of the emulator  eg.emulator-5554
*/

def static closeEmulator(portNumber,assetNumber){
	println "close emulator......"
	def pro1 = "adb -s emulator-${portNumber} emu kill".execute()
	pro1.waitFor()
	sleep(2000)
	def pro2 = "adb devices".execute()
	def pro3 = "grep ${portNumber}".execute()
	pro2 | pro3
	pro3.waitFor()
	def result = pro3.getText()
	println result
	if(result==null || result==""){
		log(TAG,assetNumber,"close emulator success")
		println "close success.."
		return true
	}
	return false
}
//simulate incoming calling
/*
emulatorIP is localhost IP             		 eg.127.0.0.1 
portNumber is the port the emulator use      eg.5554�?556
assetNumber is the id of the emulator  		 eg.emulator-5554
*/

def static incomingCall(emulatorIP,portNumber,assetNumber){
	def retry = 0
	while(retry<3) {
		println "start calling......"
		def pro1="echo gsm call 12530".execute()
		def pro2=("telnet "+emulatorIP+" "+portNumber).execute()
		pro1 | pro2
		pro2.waitFor()
		println pro1.exitValue()
		if(pro1.exitValue()!=0){
			retry++
		}
		else
		break
	}
	if (retry==3) {
		log(TAG,assetNumber,"simulate incomingCall failure..")
		return false
	}
	else{
		log(TAG,assetNumber,"simulate incomingCall success..")
		return true
	}	
}


//simulate incoming msg
def static incomingMsg(emulatorIP,portNumber,assetNumber){
    //def s ="(sleep 1;echo \"sms send 10086 hello \";echo \"qiut\")"+" "+"|"+" "+ "telnet"+" "+ emulatorIP+" "+portNumber;
    def retry = 0
    while(retry<3){
    	println "start simulate incomingMsg........"
    	def pro1 = "echo sms send 10086 hello".execute()
    	def pro2= "telnet ${emulatorIP} ${portNumber}".execute()
    	pro1 | pro2
    	pro2.waitForOrKill(5000)
    	println pro1.exitValue()
    	if(pro1.exitValue()!= 0){
    		retry++
    	}
    	else
    	break
    }
    if (retry==3) {
    	println "simulate incomingMsg failure..."
    	log(TAG,assetNumber,"simulate incomingMsg failure..")
    	return false
    }
    else{
    	log(TAG,assetNumber,"simulate incomingMsg success..")
    	return true
    }
}



//install apks that simulate cpu occupancy rate and location simulation.
/*
apk在intent中增加Extra参数可以配置相关功能，如不传递对应于某功能的参数，则该服务不会启动�?
CPU占用：参数名cpu_usage 参数类型float 取值范�?-1 含义 占用比例
内存占用：参数名memory_usage 参数类型float 取值范�?-1 含义 占用比例
屏幕方向：参数名screen_orientation 参数类型string 取值portrait|landscape 含义 竖屏|横屏
位置模拟：参数名location 参数类型string 取�?longitude,latitude" 含义 经度,纬度
*/
def static installApkAndStart(device,packageName,apkPath,launchActivity,cpu_usage,memory_usage,screen_orientation,location) {
	println "start install.."
	def s = "adb -s emulator-5556 install "+apkPath
	def p = s.execute()
	//def IDevice device
	//device = new IDevice()
	p.waitFor()
	sleep(2000)
	try{
	//def enterAdb = "adb -s emulator-5556 shell".execute()
	//Runtime.getRuntime().exec(enterAdb).waitFor()
	def command = "am start -n ${packageName}/${launchActivity} --ef cpu_usage ${cpu_usage} memory_usage ${memory_usage} --es screen_orientation ${screen_orientation} location ${location}"
	device.executeShellCommand(command,new NullOutputReceiver(),5000)
 	println "execute....."
 	//Runtime.getRuntime().exec(command).waitFor()
	}
	catch(Exception e){
	println e
	}	
}


//set Network Type 
/*
networkTpye is the network type              eg.2G 3G 4G WIFI
emulatorIP is localhost IP             		 eg.127.0.0.1 
portNumber is the port the emulator use      eg.5554 5556
assetNumber is the id of the emulator  		 eg.emulator-5554
*/
def static setNetworkType(networkType,emulatorIP,portNumber,assetNumber){
	if(networkType.equals("2G")){
		 println "setting network type to 2g"
                 def pro1 = "echo network speed gsm".execute()
                 def pro2 = "telnet ${emulatorIP} ${portNumber}".execute()
                 pro1 | pro2
                 pro2.waitFor()
		 if(pro1.exitValue()!=0){
			log(TAG,assetNumber,"set network type 2G failure..")
		}
		else
			log(TAG,assetNumber,"set network type 2G suceess..")
	}
	if(networkType.equals("3G")){
		 println "setting network type to 3g"
		 def pro1 = "echo network speed umts".execute()
                 def pro2 = "telnet ${emulatorIP} ${portNumber}".execute()
                 pro1 | pro2
                 pro2.waitFor()
		if(pro1.exitValue()!=0){
                        log(TAG,assetNumber,"set network type 3G failure..")
                }
                else
                        log(TAG,assetNumber,"set network type 3G suceess..")
	}
	else if(networkType.equals("WIFI")){
		 println "setting network type to wifi"
                 def pro1 = "echo network speed full".execute()
                 def pro2 = "telnet ${emulatorIP} ${portNumber}".execute()
                 pro1 | pro2
                 pro2.waitFor()
                if(pro1.exitValue()!=0){
                        log(TAG,assetNumber,"set network type wifi failure..")
                }
                else
                        log(TAG,assetNumber,"set network type wifi suceess..")
}
	else{
		println "setting network type to 4g"
            	def pro1 = "echo network speed 20480 81920".execute()
            	def pro2 = "telnet ${emulatorIP} ${portNumber}".execute()
            	pro1 | pro2
            	pro2.waitFor()
		if(pro1.exitValue()!=0){
                        log(TAG,assetNumber,"set network type 4G failure..")
                }
                else
                        log(TAG,assetNumber,"set network type 4G suceess..")
	}
}

//setBatteryPower
/*
powerCapacity is percentage of remaining battery	eg.45  70 90
emulatorIP is localhost IP             		 	eg.127.0.0.1 
portNumber is the port the emulator use      		eg.5554�?556
assetNumber is the id of the emulator  		 	eg.emulator-5554
*/

def static setBatteryPower(powerCapacity,emulatorIP,portNumber,assetNumber){
	//	def s = "(sleep 1;echo \"power capacity "+powerCapcity+";echo 'quit')|telnet "+emulatorIP+" "+portNumber
	def retry = 0
	while(retry<3){
		println "setting power capacity..."
		def pro1 = "echo power capacity ${powerCapacity}".execute()
		def pro2 = "telnet ${emulatorIP} ${portNumber}".execute()
		pro1 | pro2
		pro2.waitFor()
		println pro1.exitValue()
		if(pro1.exitValue()!= 0){
			retry++
		}
		else
		break
	}
	if (retry==3) {
		println "setPower capacity failure..."
		log(TAG,assetNumber,"set power capacity failure..")
		return false
	}

	else{
		log(TAG,assetNumber,"set power capacity success..")
		return true
	}
}
}

