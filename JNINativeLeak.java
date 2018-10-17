
/**
 *
 * @author poonam
 */

public class JNINativeLeak {

    static {
    System.loadLibrary("JNINativeLeak");
    }
    native void allocateMemory(int bytes);

    public static void main(String[] args) {
        JNINativeLeak leak = new JNINativeLeak();

        for (int i=0; i<100000; i++) {
            leak.allocateMemory(1000);
            try {
                Thread.sleep(2);

            } catch (Exception e) {}            
        }
    }
}

