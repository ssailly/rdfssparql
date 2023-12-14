import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.impl.InfModelImpl;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.vocabulary.ReasonerVocabulary;

/**
 * ReasonerRunner class
 */
public class ReasonerRunner {
	private Reasoner reasoner;
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
	public ReasonerRunner(String rdfFile, String rdfsFile, String compliance,
	boolean debug) {
		rdf.read(rdfFile);
		rdfs.read(rdfsFile);
		reasoner = ReasonerRegistry.getRDFSReasoner();
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
