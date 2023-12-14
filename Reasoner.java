import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.impl.InfModelImpl;
import org.apache.jena.vocabulary.ReasonerVocabulary;

/**
 * Reasoner class
 * @see https://github.com/apache/jena/blob/main/jena-core/src/main/resources/etc/rdfs.rules
 */
public class Reasoner {
	private org.apache.jena.reasoner.Reasoner reasoner;
	private Model rdf = ModelFactory.createDefaultModel();
	private Model rdfs = ModelFactory.createDefaultModel();
	private boolean debug;

	/**
	 * Constructor
	 * @param rdfFile path to RDF file
	 * @param rdfsFile path to RDFS file
	 * @param compliance Compliance level
	 * @param debug Debug mode
	 */
	public Reasoner(String rdfFile, String rdfsFile, String compliance,
	boolean debug) {
		rdf.read(rdfFile);
		rdfs.read(rdfsFile);
		reasoner = org.apache.jena.reasoner.ReasonerRegistry.getRDFSReasoner();
		reasoner.setParameter(ReasonerVocabulary.PROPsetRDFSLevel, compliance);
		reasoner.setParameter(ReasonerVocabulary.PROPtraceOn, debug);
		reasoner.setParameter(ReasonerVocabulary.PROPderivationLogging, debug);
		this.debug = debug;

		if (debug) {
			System.out.println("Created reasoner with parameters:");
			System.out.println("- RDF file: " + rdfFile);
			System.out.println("- RDFS file: " + rdfsFile);
			System.out.println("- Compliance: " + compliance);
		}
	}

	/**
	 * Run the reasoner
	 */
	public void run() {
		InfModel infered = new InfModelImpl(
			reasoner.bindSchema(rdfs).bind(rdf.getGraph())
		);
		if (debug) {
			System.out.println("Reasoner run");
		}
		infered.write(System.out, "TURTLE");
	}
}
