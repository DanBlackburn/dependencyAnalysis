package diGraph.analyse;

import java.io.InputStream;

/**
 * Created by thargor on 9/14/14.
 */
// TODO make configurable
public class AnalyserFactory {

    public static Analyser create() {
        return new ASMAnalyse();
    }
}
