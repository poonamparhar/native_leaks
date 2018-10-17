class ClassCreator {
    private int number;
    private String className;
    private StringBuilder sb = new StringBuilder();
    private int maxPerClassLoader = 100;  // one class loader will load max up to 100 classes
    private TemplateClassLoader loader = new TemplateClassLoader();

    ClassCreator() {
        this(TemplateClassLoader.DEFAULT_CLASSNAME);
    }
    ClassCreator(String className) {
        this.className = className;
    }

    // Generate new class name
    String getNewClassName() {
        sb.delete(0, sb.length());
        sb.append("Class");
        sb.append(number);
        int n = loader.getNameLength() - sb.length();
        for (int i = 0; i < n; ++i) {
            sb.append('#');
        }
        return sb.toString();
    }

    // Load a new class with TemplateClassLoader
    Class createClass() {
        try {
            // Create a new TemplateClassLoader if the number of classes loaded
            // by the current classloader is more then maxPerClassLoader
            if (number++ > maxPerClassLoader || loader == null) {
                loader = new TemplateClassLoader(className);
                number = 0;
            }
            return loader.loadClass(getNewClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}