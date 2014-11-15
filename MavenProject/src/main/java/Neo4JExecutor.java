import java.io.File;
import java.util.ArrayList;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/**
 * Created by trannguyen on 11/14/14.
 */
public class Neo4JExecutor implements QueryExecutor {
	
	private GraphDatabaseService graphDb;
	private ArrayList<Relationship> relationships;
	private String db_path = "target/neo4j-test";
	
	public Neo4JExecutor() throws Exception{
		this.relationships = new ArrayList<Relationship>();
		if(!connect()){
			throw new Exception("Could not initiate the the Neo4JExecutor Executor");
		}
	}
	
	public Neo4JExecutor(String db_path) throws Exception{
		this.relationships = new ArrayList<Relationship>();
		this.db_path = db_path;
		if(!connect()){
			throw new Exception("Could not initiate the the Neo4JExecutor Executor");
		}
	} 
    /**
     * Each implementation of this will take in
     * the raw sql query and execute in its underlying database
     * returning how long it takes to execute that statement
     *
     * @param rawSQLQuery SQL statement
     * @return time it takes to excute those in mili seconds
     */
    @Override
    public long executeQuery(String rawSQLQuery) {
    	long fStart = System.currentTimeMillis();
    	createDb();
        removeData();
        long fEnd = System.currentTimeMillis();
    	return (fStart - fEnd);
    }
    
	@Override
	public boolean connect() {
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(db_path);
		registerShutdownHook( graphDb );
		return true;
	}
	
    void createDb()
    {
        deleteFileOrDirectory( new File(db_path) );
        // START SNIPPET: startDb
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(db_path);
        registerShutdownHook( graphDb );
        // END SNIPPET: startDb
        // START SNIPPET: transaction
        try ( Transaction tx = graphDb.beginTx() )
        {
            // Database operations go here
            // END SNIPPET: transaction
            // START SNIPPET: addData
//            firstNode = graphDb.createNode();
//            firstNode.setProperty( "message", "Hello, " );
//            secondNode = graphDb.createNode();
//            secondNode.setProperty( "message", "World!" );
//
//            relationship = firstNode.createRelationshipTo( secondNode, RelTypes.KNOWS );
//            relationship.setProperty( "message", "brave Neo4j " );
//            // END SNIPPET: addData
//
//            // START SNIPPET: readData
//            System.out.print( firstNode.getProperty( "message" ) );
//            System.out.print( relationship.getProperty( "message" ) );
//            System.out.print( secondNode.getProperty( "message" ) );
//            // END SNIPPET: readData
//
//            greeting = ( (String) firstNode.getProperty( "message" ) )
//                       + ( (String) relationship.getProperty( "message" ) )
//                       + ( (String) secondNode.getProperty( "message" ) );

            // START SNIPPET: transaction
            tx.success();
        }
        // END SNIPPET: transaction
    }

    void removeData()
    {
        try ( Transaction tx = graphDb.beginTx() )
        {
            // START SNIPPET: removingData
            // let's remove the data
//            firstNode.getSingleRelationship( RelTypes.KNOWS, Direction.OUTGOING ).delete();
//            firstNode.delete();
//            secondNode.delete();
            // END SNIPPET: removingData

            tx.success();
        }
    }

    void shutDown()
    {
        System.out.println();
        System.out.println( "Shutting down database ..." );
        // START SNIPPET: shutdownServer
        graphDb.shutdown();
        // END SNIPPET: shutdownServer
    }

    // START SNIPPET: shutdownHook
    private static void registerShutdownHook( final GraphDatabaseService graphDb )
    {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
                graphDb.shutdown();
            }
        } );
    }
    // END SNIPPET: shutdownHook

    private static void deleteFileOrDirectory( File file )
    {
        if ( file.exists() )
        {
            if ( file.isDirectory() )
            {
                for ( File child : file.listFiles() )
                {
                    deleteFileOrDirectory( child );
                }
            }
            file.delete();
        }
    }
	@Override
	public void cleanUP() {
        shutDown();
	}
    
}
