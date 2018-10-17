import java.util.ArrayList;
import java.util.List;
import java.lang.ref.SoftReference;

public class MetaspaceMemoryLeak {
    List<Object> classesCache = new ArrayList<>();
    public static void main(String[] args) {
        MetaspaceMemoryLeak memoryLeak = new MetaspaceMemoryLeak();
        memoryLeak.eatMetaspace();
    }
    public void eatMetaspace() {
        ClassCreator classCreator = new ClassCreator();
        while(true) {
            // create a new class and store its reference into the classesCache
            Class cl = classCreator.createClass();
            try {
              Thread.sleep(1);
            } catch(Exception e) {}
            classesCache.add(cl);

            // classes can be cached as weak/soft references to avoid storing them
            // as strong references and preventing them from being garbage collected.
            // classesCache.add(new SoftReference(cl));
        }

    }
}
