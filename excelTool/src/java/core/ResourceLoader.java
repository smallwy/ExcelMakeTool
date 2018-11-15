package core;//


import java.io.IOException;
import java.io.InputStream;

public abstract class ResourceLoader {

    public abstract void load(InputStream var1) throws IOException;

    public String getResourceName() {
        String classSimpleName = this.getClass().getSimpleName();
        int subEnd = 0;
        if (classSimpleName.endsWith("Manager")) {
            subEnd = classSimpleName.length() - 7;
        }

        return classSimpleName.substring(0, subEnd);
    }

    public int getPriority() {
        return 10000;
    }

    public String afterLoad() {
        return null;
    }
}
