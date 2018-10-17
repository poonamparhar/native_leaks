import java.util.Random;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class TestDeflater {
    Random rd = new Random();
    int size = 4000;

    public void doAction() {            
            byte [] data = new byte[size];
            for(int index=0; index<size; index++)
                data[index] = (byte)rd.nextInt();

            for(long i=0; i<100000; i++) {
                byte[] result = compressBytes(data);
                try {
                    Thread.sleep(1);
                } catch (Exception e) {}                    

            }
        }

    byte[] compressBytes(byte[] in) {
        Deflater compresser = new Deflater();
        compresser.setInput(in);
        compresser.finish();

        byte[] out = new byte[in.length];
        int outLen = compresser.deflate(out);
        return out;
    }

    public static void main(String [] args) throws Exception {
        TestDeflater deflater = new TestDeflater();
        deflater.doAction();
    }
}

