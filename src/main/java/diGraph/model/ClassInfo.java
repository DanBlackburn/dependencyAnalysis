package diGraph.model;

import java.util.*;

/**
 * Created by thargor on 9/11/14.
 */
public class ClassInfo {

    // Directory or compressed archive this class is part of
    private String container;
    // TODO packages are entities to, we need a constent datamodel
    // package of the class
    private String packageName;
    // className of the class
    private String className;
    private Set<String> refs = new HashSet<>();

    public String getName() {
        return className;
    }

    public Set<String> getRefs() {
        return Collections.unmodifiableSet(refs);
    }

    public void addRef(String ref) {
        refs.add(ref);
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }

    public String getPackage() {
        return packageName;
    }

    public void setPackage(String packageName) {
        this.packageName = packageName;
    }
}
