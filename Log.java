


public class Log {
    static { 
        System.loadLibrary("Log"); 
    }
    public native void Log(String str);
    public native void LogPassword(String str);
}
