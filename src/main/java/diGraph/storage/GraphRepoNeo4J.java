package diGraph.storage;

import diGraph.model.ClassInfo;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by thargor on 8/26/14.
 */
public class GraphRepoNeo4J implements GraphRepo {

    private static final Logger log = LoggerFactory.getLogger(GraphRepoNeo4J.class);

    public static final Label LABEL_CLASS = DynamicLabel.label("class");
    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_PACKAGE = "package";
    GraphDatabaseService graphDb;

    private static enum RelTypes implements RelationshipType
    {
        KNOWS
    }


    public GraphRepoNeo4J(String dbPath) {
        log.info("start neo4j on {}", dbPath);
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( dbPath );

        IndexDefinition indexDefinition;
        try ( Transaction tx = graphDb.beginTx() )
        {
            Schema schema = graphDb.schema();
            Iterable<IndexDefinition> indexes = schema.getIndexes(LABEL_CLASS);
            IndexDefinition nameIdx = null;
            // TODO finds only one property idxs
            for( IndexDefinition idx: indexes) {
                for(String property : idx.getPropertyKeys()) {
                    if(property.equals(PROPERTY_NAME)) {
                        nameIdx = idx;
                        break;
                    }
                }
                if(nameIdx != null)
                    break;
            }

            if(nameIdx == null) {
                log.info("create index on name");
                indexDefinition = schema.indexFor( LABEL_CLASS )
                        .on(PROPERTY_NAME)
                        .create();
            }

            tx.success();
        }

    }

    public void storeClassInfo(ClassInfo classInfo) {
        log.info("store analyse for {}", classInfo.getName());

        try ( Transaction tx = graphDb.beginTx() )
        {
            Iterable<Relationship> classNodeRelationships = null;
            // FIXME we need to identity a class with it's package and container ... also subcontainer?
            Node classNode = getNodeByName(classInfo.getName());
            if(classNode == null) {
                classNode = createNode(classInfo);
            } else {
                classNodeRelationships = classNode.getRelationships(RelTypes.KNOWS, Direction.OUTGOING);
            }

            for(String ref : classInfo.getRefs()) {

                // do we know you?
                Node refNode = getNodeByName(ref);
                boolean refExists = false;

                // nope, lets create you
                if(refNode == null) {
                    ClassInfo refClassInfo = new ClassInfo();
                    refClassInfo.setClassName(StringUtils.substringAfterLast(ref, "."));
                    refClassInfo.setPackage(StringUtils.substringBeforeLast(ref, "."));
                    refNode = createNode(refClassInfo);
                } else {
                    if(classNodeRelationships != null) {
                        for(Relationship rel : classNodeRelationships) {
                            if(rel.getOtherNode(classNode).equals(refNode)) {
                                log.info("found rel {} -> {}", classNode, refNode);
                                refExists = true;
                                break;
                            }
                        }
                    }
                }

                if(!refExists) {
                    log.info("create rel {} -> {}", classNode, refNode);
                    classNode.createRelationshipTo(refNode, RelTypes.KNOWS);
                }
            }

            // Database operations go here
            tx.success();
        }
    }

    private Node createNode(ClassInfo classInfo) {
        Node node = graphDb.createNode();
        log.info("create node {} {}", classInfo, node);
        node.setProperty(PROPERTY_NAME, classInfo.getName());
        node.setProperty(PROPERTY_PACKAGE, classInfo.getPackage());
        node.addLabel(LABEL_CLASS);
        return node;
    }

    private Node getNodeByName(String name) {
        Node node = null;
        ResourceIterable<Node> nodesWithLabel = graphDb.findNodesByLabelAndProperty(LABEL_CLASS, PROPERTY_NAME, name);
        ResourceIterator<Node> iter = nodesWithLabel.iterator();
        while(iter.hasNext()) {
            if(node != null)
                throw new IllegalArgumentException("there is more then one node for name: " + name);

            // Yep, you are known
            node = iter.next();
            break;
        }
        iter.close();

        if(node != null)
            log.info("found node {} {}", name, node);

        return node;
    }

    @Override
    public void close() {
        graphDb.shutdown();
    }

}
