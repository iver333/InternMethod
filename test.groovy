import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


def libraryScript = new GroovyScriptEngine(caseDir).with {
		loadScriptByName('EmulatorRelatedLib.groovy')
	}
this.metaClass.mixin libraryScript

def  TAG="testGroovy"
try{
println avdName
println target
println skin
println portNumber
println device
log(TAG,assetNumber,"test groovy..")

//createMyAVD(avdName,target,skin,sdCardSize,assetNumber,abi)
//sleep(4000)
//startEmulator(avdName,portNumber,ramSize,assetNumber,abi,adb)

//wait for emulator launch
//sleep(100000)
//println "sleep,u can call me"
installApkAndStart(device,packageName,apkPath,launchActivity,cpu_usage,memory_usage,screen_orientation,location)

//setNetworkType(networkType,emulatorIP,portNumber,assetNumber)
//incomingCall(emulatorIP,portNumber,assetNumber)
//incomingMsg(emulatorIP,portNumber,assetNumber)
//setBatteryPower(powerCapacity,emulatorIP,portNumber,assetNumber)
//closeEmulator(portNumber,assetNumber)
}
catch(Exception e) {
	print e
}
