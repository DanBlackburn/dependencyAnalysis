package diGraph;

import diGraph.analyse.AnalyserFactory;
import diGraph.model.ClassInfo;
import diGraph.storage.GraphRepo;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class FileReader {

    private static final Logger log = LoggerFactory.getLogger(FileReader.class);
    private GraphRepo graphRepo;

    public static final IOFileFilter JAR_FILE_FILTER = FileFilterUtils.suffixFileFilter("jar");
    static final IOFileFilter EAR_FILE_FILTER = FileFilterUtils.suffixFileFilter("ear");
    static final IOFileFilter WAR_FILE_FILTER = FileFilterUtils.suffixFileFilter("war");
    static final IOFileFilter WAR_EAR_JAR_FILTER = FileFilterUtils.or(JAR_FILE_FILTER, EAR_FILE_FILTER, WAR_FILE_FILTER);

    public FileReader(GraphRepo graphRepo) {
        this.graphRepo = graphRepo;
    }

    public void processPaths(Collection<File> paths) {
        log.info("processing paths: {}", paths);

        List<File> files = new ArrayList<File>();
        List<File> dirs = new ArrayList<File>();
        for (File file : paths) {

            if (file.isFile()) {
                files.add(file);
            } else if (file.isDirectory()) {
                dirs.add(file);
            } else {
                log.info("unkown type: {}", file);
            }
        }

        log.info("files: {}", files);
        log.info("dirs: {}", dirs);

        processZipFiles(files);
        processDirectories(dirs);
    }

    private void processZipFiles(List<File> files) {
        for (File file : files) {
            if (file.canRead() && file.isFile() && WAR_EAR_JAR_FILTER.accept(file)) {
                try {
                    log.info("read zip: {}", file);
                    ZipFile zipFile = new ZipFile(file.getAbsolutePath());
                    processZipFile(zipFile, file.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                log.info("path ignore: {}", file);
            }
        }
    }

    private void processDirectories(List<File> dirs) {
        for (File dir : dirs) {
            log.info("listing: {}", dir);
            Collection<File> files = FileUtils.listFilesAndDirs(dir, WAR_EAR_JAR_FILTER, TrueFileFilter.INSTANCE);

            // DirFilter in IO doesn't work on parent directory
            Iterator<File> iter = files.iterator();
            while (iter.hasNext()) {
                File file = iter.next();
                if (file.getPath().equals(dir.getPath())) {
                    iter.remove();
                    break;
                }
            }

            processPaths(files);
        }

    }

    private void readInnerZipFile(ZipFile zipFile, ZipArchiveEntry innerZipFileEntry) {
        File tempFile = null;
        FileOutputStream tempOut = null;
        ZipFile innerZipFile = null;
        try {
            tempFile = File.createTempFile("tempFile", "zip");
            tempOut = new FileOutputStream(tempFile);
            IOUtils.copy(
                    zipFile.getInputStream(innerZipFileEntry),
                    tempOut);
            innerZipFile = new ZipFile(tempFile);
            processZipFile(innerZipFile, innerZipFileEntry.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void processZipFile(ZipFile zipFile, String container) throws IOException {
        Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
        while (entries.hasMoreElements()) {
            ZipArchiveEntry entry = entries.nextElement();
            if (!entry.isDirectory() && StringUtils.endsWith(entry.getName(), ".class")) {
                InputStream inputStream = zipFile.getInputStream(entry);
                log.info("----------------------");
                log.info("analysing: {}", entry.getName());

                ClassInfo classInfo = AnalyserFactory.create().analyse(inputStream, container);
                graphRepo.storeClassInfo(classInfo);
                log.info("----------------------");
            } else if (!entry.isDirectory() && (StringUtils.endsWith(entry.getName(), ".jar")
                    || StringUtils.endsWith(entry.getName(), ".war")
                    || StringUtils.endsWith(entry.getName(), ".ear"))) {
                log.info("inner zip: {}", entry.getName());
                readInnerZipFile(zipFile, entry);
            } else {
                log.info("zip entry ignored: {}", entry.getName());
            }
        }
    }







}
