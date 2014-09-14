package diGraph.analyse;

import org.apache.commons.lang3.StringUtils;

public class ByteCodeUtil {
    static String packageToByteCodeDesc(String packageName) {
        if (!StringUtils.startsWith(packageName, "L")) {
            packageName = "L" + packageName;
        }
        packageName = StringUtils.replace(packageName, ".", "/");
        return packageName;
    }

    static String byteCodeDescToPackage(String desc) {
        desc = StringUtils.removeStart(desc, "L");
        desc = StringUtils.replace(desc, "/", ".");
        desc = StringUtils.removeEnd(desc, ";");
        return desc;
    }
}