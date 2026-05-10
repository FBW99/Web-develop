public class PrintChar implements Runnable {
    private char charNum;
    private int times;

    public PrintChar(char c,int t){
        charNum=c;
        times=t;
    }
    public void run(){
        for(int i=0;i<=times;i++){
            System.out.println("Charactristics :"+ charNum);

            Thread.yield();
        }
    }
}


