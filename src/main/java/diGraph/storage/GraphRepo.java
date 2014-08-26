package diGraph.storage;

import java.util.List;

/**
 * Created by thargor on 8/26/14.
 */
public interface GraphRepo {

    public void storeClassAnalyse(String clazz, List<String> references);

    public void close();
}
