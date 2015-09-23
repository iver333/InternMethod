//def pidCommand ="netstat -nltp | grep 5554 | awk \'{print \$7}\' | awk -F \"/\" \'{print \$1}\'".execute()

//def sout = new StringBuffer()
//def serr = new StringBuffer()
//def proc = pullResultCommansout, serr)
//pidCommand.consumeProcessOutput(sout, serr)

//pidCommand.waitForOrKill(50000)
//System.out.println("PULL RETURN CODE TRUE "+sout)

   // println pidCommand.text
def writeSh(s,filename){

new File(filename).withPrintWriter {
     printWriter ->
     printWriter.println('#!/bin/bash')
     printWriter.print(s)
     printWriter.flush()
     printWriter.close()
}
     Thread.sleep(3)
    def command1 = "chmod 755 "+filename
    Runtime.getRuntime().exec(command1).waitFor()

}
def pidCommand="pid=`netstat -nltp | grep 5554 | awk \'{print \$7}\' | awk -F \"/\" \'{print \$1}\'` \n kill -9 \$pid \n sleep 3"

        writeSh(pidCommand,"close.sh")
        def command2 ="./close.sh"
        Runtime.getRuntime().exec(command2).waitFor()
        Thread.sleep(2)

