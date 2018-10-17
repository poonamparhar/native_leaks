import java.io.*;
import java.util.*;

/**
 * Classloader that generates classes on the fly.
 */

class TemplateClassLoader extends ClassLoader {
    public synchronized Class loadClass(String name) throws ClassNotFoundException {
        return loadClass(name, false);
    }

    public synchronized Class loadClass(String name, boolean resolve)
            throws ClassNotFoundException {
        Class c = findLoadedClass(name);
        if (c != null) {
            return c;
        }
        if (!name.startsWith(PREFIX)) {
            return super.loadClass(name, resolve);
        }
        byte[] bytecode = getPatchedByteCode(name);
        c = defineClass(name, bytecode, 0, bytecode.length);
        if (resolve) {
            resolveClass(c);
        }
        return c;
    }
    /**
     * Create generating class loader that will use class file for given class
     * from classpath as template.
     */
    TemplateClassLoader(String templateClassName) {
        this.templateClassName = templateClassName;
        classPath = System.getProperty("java.class.path").split(File.pathSeparator);
    }
    TemplateClassLoader() {
        this(TemplateClass.class.getName());
    }

    int getNameLength() {
        return templateClassName.length();
    }

    private byte[] getPatchedByteCode(String name) throws ClassNotFoundException {
        // System.out.println("Class: "+name);
        try {
            byte[] bytecode = getByteCode();
            String fname = name.replace(".", File.separator);
            byte[] replaceBytes = fname.getBytes(encoding);
            for (int offset : offsets) {
                for (int i = 0; i < replaceBytes.length; ++i) {
                    bytecode[offset + i] = replaceBytes[i];
                }
            }
            return bytecode;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] getByteCode() throws ClassNotFoundException {
        if (bytecode == null) {
            readByteCode();
        }
        if (offsets == null) {
            getOffsets(bytecode);
            if (offsets == null) {
                throw new RuntimeException("Class name not found in template class file");
            }
        }
        return (byte[]) bytecode.clone();
    }
    private void readByteCode() throws ClassNotFoundException {
        String fname = templateClassName.replace(".", File.separator) + ".class";
        File target = null;
        for (int i = 0; i < classPath.length; ++i) {
            target = new File(classPath[i] + File.separator + fname);
            if (target.exists()) {
                break;
            }
        }
        if (target == null || !target.exists()) {
            throw new ClassNotFoundException("File not found: " + target);
        }
        try {
            bytecode = readFile(target);
        } catch (IOException e) {
            throw new ClassNotFoundException(templateClassName, e);
        }
    }

    private void getOffsets(byte[] bytecode) {
        List<Integer> offsets = new ArrayList<Integer>();
        if (this.offsets == null) {
            String pname = templateClassName.replace(".", "/");
            try {
                byte[] pnameb = pname.getBytes(encoding);
                int i = 0;
                while (true) {
                    while (i < bytecode.length) {
                        int j = 0;
                        while (j < pnameb.length && bytecode[i + j] == pnameb[j]) {
                            ++j;
                        }
                        if (j == pnameb.length) {
                            break;
                        }
                        i++;
                    }
                    if (i == bytecode.length) {
                        break;
                    }
                    offsets.add(new Integer(i));
                    i++;
                }
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            this.offsets = new int[offsets.size()];
            for (int i = 0; i < offsets.size(); ++i) {
                this.offsets[i] = offsets.get(i).intValue();
            }
        }
    }
    public static byte[] readFile(File file) throws IOException {
        InputStream in = new FileInputStream(file);
        long countl = file.length();
        if (countl > Integer.MAX_VALUE) {
            throw new IOException("File is too huge");
        }
        int count = (int) countl;
        byte[] buffer = new byte[count];
        int n = 0;
        try {
            while (n < count) {
                int k = in.read(buffer, n, count - n);
                if (k < 0) {
                    throw new IOException("Unexpected EOF");
                }
                n += k;
            }
        } finally {
            in.close();
        }
        return buffer;
    }

    static final String DEFAULT_CLASSNAME = TemplateClass.class.getName();
    static final String PREFIX = "Class";

    private final String[] classPath;
    private byte[] bytecode;
    private int[] offsets;
    private final String encoding = "UTF8";
    private final String templateClassName;
}

class TemplateClass {
}