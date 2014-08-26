package diGraph.storage;

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by thargor on 8/26/14.
 */
public class GraphRepoNeo4J implements GraphRepo {

    private static final Logger log = LoggerFactory.getLogger(GraphRepoNeo4J.class);

    public static final Label LABEL_CLASS = DynamicLabel.label("class");
    public static final String PROPERTY_NAME = "name";
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

    public void storeClassAnalyse(String clazz, List<String> references) {
        log.info("store analyse for {}", clazz);

        try ( Transaction tx = graphDb.beginTx() )
        {
            Iterable<Relationship> classNodeRelationships = null;
            Node classNode = getNodeByName(clazz);
            if(classNode == null) {
                classNode = createNode(clazz);
            } else {
                classNodeRelationships = classNode.getRelationships(RelTypes.KNOWS, Direction.OUTGOING);
            }

            for(String ref : references) {

                // do we know you?
                Node refNode = getNodeByName(ref);
                boolean refExists = false;

                // nope, lets create you
                if(refNode == null) {
                    refNode = createNode(ref);
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

    private Node createNode(String name) {
        Node node = graphDb.createNode();
        log.info("create node {} {}", name, node);
        node.setProperty(PROPERTY_NAME, name);
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
