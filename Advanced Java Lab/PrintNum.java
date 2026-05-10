public class PrintNum implements Runnable {
    private int num;
    private int times;

    public PrintNum(int n,int t){
        num=n;
        times=t;
    }
    public void run(){
        for(int i=1;i<=times;i++){
            System.out.println("Number : "+num);

            Thread.yield();
        }
    }
}
