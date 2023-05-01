
import java.io.*;
import java.nio.charset.StandardCharsets;   


public class Hello { 
  
  public native void sayHi(String who, int times);

  public native void stringSanitize(String str);


  static { System.loadLibrary("HelloImpl"); }

  public static void main (String[] args) { 

    Console cnsl  = System.console();;
    String tmp = cnsl.readLine();
    
    // String tmp = new String("test string here");
    String testStr = new String(tmp.getBytes(), StandardCharsets.UTF_16);
    System.out.println("before");
    System.out.println(testStr);
    // System.out.println(testStr);
    System.out.println("passing to native");
    Hello hello = new Hello(); 
    hello.stringSanitize(testStr);
    
    System.out.println("after");

    System.out.println(testStr);



    // Hello hello = new Hello(); 
    // hello.sayHi("test person",10);
  }
}