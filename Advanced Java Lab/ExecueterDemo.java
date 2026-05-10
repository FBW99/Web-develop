import java.util.concurrent.*;
public class ExecueterDemo {
public static void main(String[] args)
{ //Create a fixed thread pool with maximum three threads
ExecutorService executor =Executors.newCachedThreadPool();// Submit runnable tasks to the executor
executor.execute(new PrintChar('a', 5));
executor.execute( new PrintChar('b', 5));
executor.execute( new PrintNum(100,5));
// Shut down the executor
executor.shutdown();
}}