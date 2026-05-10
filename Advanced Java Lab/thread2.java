public class thread2 implements Runnable{
    String msg;
     thread2(String mg){
        msg=mg;
    }

    public void run(){
        for(int i=0;i<=5;i++){
            System.out.println("Run Method : "+ msg);

            Thread.yield();
        }
    }

 
}
