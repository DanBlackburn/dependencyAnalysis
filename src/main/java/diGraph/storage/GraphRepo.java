package diGraph.storage;

import diGraph.model.ClassInfo;

/**
 * Created by thargor on 8/26/14.
 */
public interface GraphRepo {

    public void storeClassInfo(ClassInfo classInfo);

    public void close();
}
