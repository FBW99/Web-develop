public class priority extends Thread {
    public void run(){
        for(int i=0;i<20;i++){
            System.out.println(getName()+" : "+i);
        }
}
public static void main(String[] args) {
    priority t1=new priority();
    priority t2=new priority();
    t2.setPriority(Thread.MIN_PRIORITY);
    t1.setPriority(Thread.MAX_PRIORITY);

    t1.start();
    t2.start();
}
}
