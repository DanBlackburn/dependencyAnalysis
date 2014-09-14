package diGraph.analyse;

import diGraph.model.ClassInfo;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by thargor on 8/25/14.
 */
public class ASMAnalyse implements Analyser {

    private static final Logger log = LoggerFactory.getLogger(ASMAnalyse.class);

    private static Set<String> ignorePackages = new HashSet<>();
    private ClassInfo classInfo;

    public static String[] getIgnorePackages() {
        return ignorePackages.toArray(new String[]{});
    }

    public static void addIgnorePackage(String ignorePackage) {
        ignorePackage = packageToByteCodeDesc(ignorePackage);
        ignorePackages.add(ignorePackage);
    }

    private static String packageToByteCodeDesc(String packageName) {
        if (!StringUtils.startsWith(packageName, "L")) {
            packageName = "L" + packageName;
        }
        packageName = StringUtils.replace(packageName, ".", "/");
        return packageName;
    }

    private static String byteCodeDescToPackage(String desc) {
        desc = StringUtils.removeStart(desc, "L");
        desc = StringUtils.replace(desc, "/", ".");
        desc = StringUtils.removeEnd(desc, ";");
        return desc;
    }

    public ClassInfo analyse(InputStream inputStream, String container) throws IOException {
        classInfo = new ClassInfo();
        classInfo.setContainer(container);

        ClassReader classReader = new ClassReader(inputStream);
        classReader.accept(getClassVisitor(), 0);

        return classInfo;
    }

    public ClassInfo getClassInfo() {
        return classInfo;
    }

    public ClassVisitor getClassVisitor() {

        return new ClassVisitor(Opcodes.ASM5) {

            /**
             * Called when a class is visited. This is the method called first
             */
            @Override
            public void visit(int version, int access, String name,
                              String signature, String superName, String[] interfaces) {
                log.info("Visiting class: " + name);
                // FIXME detect EJBs and CDI-Beans
                String fullClassname = byteCodeDescToPackage(name);
                classInfo.setPackage(StringUtils.substringBeforeLast(fullClassname, "."));
                classInfo.setClassName(StringUtils.substringAfterLast(fullClassname,"."));
                super.visit(version, access, name, signature, superName, interfaces);
            }

            /**
             * When a field is encountered
             */
            @Override
            public FieldVisitor visitField(int access, String name,
                                           String desc, String signature, Object value) {
                Type type = Type.getType(desc);
                if (Type.OBJECT == type.getSort()) {
                    if (StringUtils.startsWithAny(desc, getIgnorePackages())) {
                        log.info("ignored package: " + name + " " + desc);
                    } else {
                        log.info("Field: " + name + " " + desc);
                        // FIXME detect DI Annotations
                        classInfo.addRef(byteCodeDescToPackage(desc));
                    }
                } else {
                    log.info("primitive field: {}", name);
                }
                return super.visitField(access, name, desc, signature, value);
            }

        };
    }


}
