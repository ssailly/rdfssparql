import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class Main {
	public static void main(String[] args) throws Exception {
		String rdfFile = "", rdfsFile = "", sparqlFile = "";
		String compliance = "default", output = "TURTLE";
		boolean newfacts = false, debug = false;
		for (String s : args) {
			if (s.equals("debug")) debug = true;
			else {
				String[] arg = s.split("=");
				switch (arg[0]) {
					case "rdf" -> rdfFile = arg[1];
					case "rdfs" -> rdfsFile = arg[1];
					case "sparql" -> sparqlFile = arg[1];
					case "compliance" -> compliance = arg[1];
					case "output" -> output = arg[1];
					case "newfacts" -> newfacts = true;
					case "debug" -> debug = true;
				}
			}
		}
		if (rdfFile.isEmpty() || !new File(rdfFile).isFile()) {
			throw new IllegalArgumentException("Invalid RDF file");
		}
		if (rdfsFile.isEmpty() || !new File(rdfsFile).isFile()) {
			throw new IllegalArgumentException("Invalid RDFS file");
		}
		if (sparqlFile.isEmpty() || !new File(sparqlFile).isFile()) {
			throw new IllegalArgumentException("Invalid SPARQL file");
		}
		if (
			compliance.isEmpty()
			|| (
				!compliance.equals("full")
				&& !compliance.equals("default")
				&& !compliance.equals("simple")
				&& !compliance.equals("none")
			)
		) {
			throw new IllegalArgumentException("Invalid compliance level");
		}
		if (debug) {
			System.out.println("RDF file: " + rdfFile);
			System.out.println("RDFS file: " + rdfsFile);
			System.out.println("SPARQL file: " + sparqlFile);
			System.out.println("Compliance: " + compliance);
			System.out.println("New facts: " + newfacts);
			System.out.println("Output: " + output);
		}
		if (!compliance.equals("none")) {
			ReasonerRunner reasoner = new ReasonerRunner(
				rdfFile, rdfsFile, compliance, newfacts, debug
			);
			String rdf = reasoner.run();
			rdfFile = "reasoned.xml";
			File f = new File(rdfFile);
			f.deleteOnExit();
			BufferedWriter writer = new BufferedWriter(new FileWriter(f));
			writer.write(rdf);
			if (debug) {
				System.out.println("RDF facts written to " + rdfFile);
			}
			writer.close();
		} else if (debug) {
			System.out.println("No reasoning performed");
		}
		QueryRunner qr = new QueryRunner(rdfFile, sparqlFile, output, debug);
		qr.run();
	}
}