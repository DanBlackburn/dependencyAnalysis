package diGraph.analyse;

import java.util.*;

/**
 * Created by thargor on 9/14/14.
 */
public class AnalyserConfig {

    private Set<String> ignorePackages = new HashSet<>();

    public Collection<String> getIgnorePackages() {
        return Collections.unmodifiableSet(ignorePackages);
    }

    public void addIgnorePackage(String packageString) {
        ignorePackages.add(packageString);
    }
}
