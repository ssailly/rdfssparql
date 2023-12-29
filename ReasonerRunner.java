import org.apache.commons.io.output.ByteArrayOutputStream;
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
	private boolean newfacts;
	private boolean debug;

	/**
	 * Constructor
	 * @param rdfFile path to RDF file
	 * @param rdfsFile path to RDFS file
	 * @param compliance Compliance level
	 * @param newfacts Return new facts only
	 * @param debug Debug mode
	 */
	public ReasonerRunner(String rdfFile, String rdfsFile, String compliance,
	boolean newfacts, boolean debug) {
		rdf.read(rdfFile);
		rdfs.read(rdfsFile);
		reasoner = ReasonerRegistry.getRDFSReasoner();
		reasoner.setParameter(ReasonerVocabulary.PROPsetRDFSLevel, compliance);
		reasoner.setParameter(ReasonerVocabulary.PROPtraceOn, debug);
		reasoner.setParameter(ReasonerVocabulary.PROPderivationLogging, debug);
		this.newfacts = newfacts;
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
	public String run() {
		Model infered = new InfModelImpl(
			reasoner.bindSchema(rdfs).bind(rdf.getGraph())
		);
		if (debug) {
			System.out.println("Reasoner run");
		}
		if (newfacts) {
			if (debug) {
				System.out.println("Keeping new facts only");
			}
			infered = infered.difference(rdf).difference(rdfs);
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		infered.write(out);
		rdf.close();
		rdfs.close();
		infered.close();
		try {
			return out.toString("UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
