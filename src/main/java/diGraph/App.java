package diGraph;

import diGraph.analyse.ASMAnalyse;
import diGraph.storage.GraphRepoNeo4J;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * Hello world!
 */
public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    // FIXME Threading ffs...
    // FIXME make the analyse configurable (only DI? or just do it on the view in neo4j?)
    public static void main(String[] args) {

        log.info("Initialising");
        GraphRepoNeo4J graphRepo = new GraphRepoNeo4J("target/neo4j");
        FileReader fileReader = new FileReader(graphRepo);

        log.info("Args: {}", args);

        List<File> argFiles = new ArrayList<>();
        for (String arg : args) {
            log.info("arg: {}", arg);
            argFiles.add(FileUtils.getFile(arg));
        }

        ASMAnalyse.addIgnorePackage("java");

        log.info("Process Files/Directories");
        fileReader.processPaths(argFiles);

        graphRepo.close();
    }

}
