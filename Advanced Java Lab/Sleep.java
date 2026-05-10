public class Sleep extends Thread {

    private int lastNum;

    public Sleep(int lastNum) {
        this.lastNum = lastNum;
    }

    @Override
    public void run() {

        Thread thread4 = new Thread(new PrintChar('c', 40));
        thread4.start();

        try {
            for (int i = 1; i <= lastNum; i++) {
                System.out.print(" " + i);

                if (i == 50) {
                    thread4.join();   // wait for thread4 to finish
                }
            }
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }
    }
}
