package diGraph.storage;

import java.util.List;
import java.util.Set;

/**
 * Created by thargor on 8/26/14.
 */
public interface GraphRepo {

    public void storeClassAnalyse(String clazz, Set<String> references);

    public void close();
}
