public class Mythread {
    public static void main(String[] args) {
        PrintChar p=new PrintChar('F', 5);
        PrintChar p1=new PrintChar('E', 5);
        PrintNum pn=new PrintNum(5, 5);
        thread2 p2=new thread2 ("Faruk Bati");
        thread1 p3=new thread1();
      

        Thread th1=new Thread(p);
        Thread th2=new Thread(p1);
        Thread th3=new Thread(pn);
        Thread th4=new Thread(p2);


        th1.start();
        th2.start();
        th3.start();
        th4.start();
        p3.start();
      
    }
}
