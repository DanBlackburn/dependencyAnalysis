package diGraph;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

/**
 * Hello world!
 *
 */
public class App
{

    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static final IOFileFilter JAR_FILE_FILTER = FileFilterUtils.suffixFileFilter("jar");

    static ClassVisitor cl = new ClassVisitor(Opcodes.ASM5) {
        /**
         * Called when a class is visited. This is the method called first
         */
        @Override
        public void visit(int version, int access, String name,
                          String signature, String superName, String[] interfaces) {
            log.info("Visiting class: " + name);
            log.info("Class Major Version: " + version);
            log.info("Super class: " + superName);
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

    public static void main( String[] args )
    {

        log.info("Args: {}", args);
        
        for(String arg : args) {
            log.info("arg: {}", arg );
            File file = FileUtils.getFile(arg);
            // FIXME war, ear
            if(file.canRead() && file.isFile() && JAR_FILE_FILTER.accept(file)) {
                readZipFile(file);
            } else {
                log.info("invalid: {}", arg);
            }

            // FIXME directories
        }

    }

    public static String readZipFile(File zip) {
    try {
        log.info("read zip: {}", zip);
        ZipFile zipFile = new ZipFile(zip.getAbsolutePath());
        Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
        while (entries.hasMoreElements()) {
            ZipArchiveEntry entry = entries.nextElement();
            if (!entry.isDirectory() && StringUtils.endsWith(entry.getName(), ".class")) {
                InputStream inputStream = zipFile.getInputStream(entry);
                log.info("----------------------");
                log.info("analysing: {}", entry.getName());
                ClassReader classReader = classReader = new ClassReader(inputStream);
                    classReader.accept(cl, 0);
                log.info("----------------------");
            } else {
                log.info("ignored: {}", entry.getName());
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return null;
}
}
