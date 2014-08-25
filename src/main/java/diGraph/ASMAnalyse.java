package diGraph;

import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by thargor on 8/25/14.
 */
public class ASMAnalyse {

    private static final Logger log = LoggerFactory.getLogger(ASMAnalyse.class);

    private static Set<String> ignorePackages = new HashSet<>();

    public static String[] getIgnorePackages() {
        return ignorePackages.toArray(new String[] {});
    }

    public static void addIgnorePackage(String ignorePackage) {
        ignorePackage = packageToByteCodeDesc(ignorePackage);
        ignorePackages.add(ignorePackage);
    }

    private static String packageToByteCodeDesc(String ignorePackage) {
        if(!StringUtils.startsWith(ignorePackage, "L")) {
            ignorePackage = "L"+ignorePackage;
        }
        ignorePackage = StringUtils.replace(ignorePackage, ".", "/");
        return ignorePackage;
    }

    private static String byteCodeDescToPackage(String ignorePackage) {
        ignorePackage = StringUtils.removeStart(ignorePackage, "L");
        ignorePackage = StringUtils.replace(ignorePackage, "/", ".");
        return ignorePackage;
    }

    static ClassVisitor CLASSVISITOR = new ClassVisitor(Opcodes.ASM5) {
        /**
         * Called when a class is visited. This is the method called first
         */
        @Override
        public void visit(int version, int access, String name,
                          String signature, String superName, String[] interfaces) {
            log.info("Visiting class: " + name);
            super.visit(version, access, name, signature, superName, interfaces);
        }

        /**
         * When a field is encountered
         */
        @Override
        public FieldVisitor visitField(int access, String name,
                                       String desc, String signature, Object value) {
            Type type = Type.getType(desc);
            if(Type.OBJECT == type.getSort()) {
                if(StringUtils.startsWithAny(desc, getIgnorePackages())) {
                    log.info("ignored package: " + name + " " + desc);
                } else {
                    log.info("Field: " + name + " " + desc);
                }
            } else {
                log.info("primitive field: {}", name );
            }
            return super.visitField(access, name, desc, signature, value);
        }

    };
}
