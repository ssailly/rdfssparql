import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;

public class QueryRunner {
	private String rdfFile;
	private Query query;
	private String output;
	private boolean debug;

	/**
	 * Constructor
	 * @param rdfFile path to RDF file
	 * @param sparqlFile Path to SPARQL query file
	 * @param output Output format (if CONSTRUCT request)
	 * @param debug Debug mode
	 */
	public QueryRunner(String rdfFile, String sparqlFile, String output, 
	boolean debug) {
		this.rdfFile = rdfFile;
		query = QueryFactory.read(sparqlFile);
		this.output = output;
		this.debug = debug;

		if (debug) {
			System.out.println("Created query with parameters:");
			System.out.println("- RDF file: " + rdfFile);
			System.out.println("- SPARQL file: " + sparqlFile);
			System.out.println("- Query:\n" + query);
		}
	}
	
	/**
	 * Run the query
	 */
	public void run() {
		Dataset d = DatasetFactory.create(rdfFile);
		QueryExecution qexec = QueryExecutionFactory.create(query, d);
		if (debug) {
			System.out.println("Query run");
		}
		if (query.isAskType()) {
			System.out.println(qexec.execAsk());
		} else if (query.isSelectType()) {
			ResultSet results = qexec.execSelect();
			ResultSetFormatter.out(System.out, results);
			results.close();
		} else if (query.isConstructType()) {
			Model model = qexec.execConstruct();
			model.write(System.out, output);
			model.close();
		} else {
			throw new IllegalArgumentException("Invalid query type");
		}
	}
}
