package diGraph.analyse;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by thargor on 9/14/14.
 */
public class AnalyserFactory {

    private static Type type;

    private static AnalyserConfig config;


    public enum Type {

        ASM(ASMAnalyse.class);

        // TODO why can we use the interface here?
        private Class<?> clazz;

        Type(Class<?> clazz) {
            this.clazz = clazz;
        }
    }

    public static Analyser create() {

        ASMAnalyse analyse = new ASMAnalyse();
        analyse.setConfig(config);
        return analyse;
    }

    public static void setType(Type type) {
        AnalyserFactory.type = type;
    }

    public static void setConfig(AnalyserConfig config) {
        AnalyserFactory.config = config;
    }
}
