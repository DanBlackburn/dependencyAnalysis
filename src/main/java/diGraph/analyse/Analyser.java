package diGraph.analyse;

import diGraph.model.ClassInfo;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by thargor on 9/11/14.
 */
public interface Analyser {

    public ClassInfo analyse(InputStream inputStream, String artefactName) throws IOException;

}
