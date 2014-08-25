package diGraph;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by thargor on 8/25/14.
 */
public class ASMAnalyse {

    private static final Logger log = LoggerFactory.getLogger(ASMAnalyse.class);

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
            log.info("Field: " + name + " " + desc + " value:" + value);
            return super.visitField(access, name, desc, signature, value);
        }

    };
}
