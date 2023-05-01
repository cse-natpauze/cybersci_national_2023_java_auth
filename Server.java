
import java.io.*;
import java.nio.charset.StandardCharsets; 


public class Server { 
  
  public static Log logger;

  


  public static void main (String[] args) { 
    // setup
    logger = new Log();
    logger.Log("test");
    Console cnsl  = System.console();
    String testStr = cnsl.readLine();
    
    // String tmp = new String("test string here");
    // String testStr = new String(tmp.getBytes(), StandardCharsets.UTF_16);
    System.out.println("before");
    System.out.println(testStr);
    // System.out.println(testStr);
    System.out.println("passing to native");
    logger.Log(testStr);
    logger.LogPassword(testStr);
    
    System.out.println("after");

    System.out.println(testStr);



    // Hello hello = new Hello(); 
    // hello.sayHi("test person",10);
  }
}