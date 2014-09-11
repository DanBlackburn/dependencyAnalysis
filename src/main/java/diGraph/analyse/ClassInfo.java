package diGraph.analyse;

import java.util.*;

/**
 * Created by thargor on 9/11/14.
 */
public class ClassInfo {

    private String artefact;
    // FIXME store name and package seperate?
    // find common packages and label all nodes in it? levinstein?
    // at least the ones directly in one zip should have a label ...
    private String name;
    private Set<String> refs = new HashSet<>();

    public ClassInfo(String artefact) {
        this.artefact = artefact;
    }

    public String getName() {
        return name;
    }

    public Set<String> getRefs() {
        return Collections.unmodifiableSet(refs);
    }

    public void addRef(String ref) {
        refs.add(ref);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtefact() {
        return artefact;
    }

    public void setArtefact(String artefact) {
        this.artefact = artefact;
    }
}
