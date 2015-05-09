package alexandria.core;

import static org.junit.Assert.assertTrue;
import static power.io.IO.file;
import static power.util.Util.array;
import static power.util.Util.list;
import static power.util.Util.str;

import java.util.List;

import lombok.SneakyThrows;

import org.junit.Test;

import alexandria.manifest.DefaultManifestInstruction;
import alexandria.manifest.ManifestInstruction;
import alexandria.manifest.ManifestReader;

public class ManifestReaderTest {
	
	// UNCHECKED: These strings represents lines read from Manifest, it could be longer than 160 chars
	static final String PACKAGE = "PACKAGE maintener \"Ibratan Ltda.\" name \"ttm-cobol-coboss\" version \"1.2.0-SNAPSHOT\"";
	static final String REPOS = "REPOS \"http://central.maven.org/maven2/\"";
	static final String CONF_BUILD = "CONF BUILD \"{build-directory}\"";
	static final String COPY = "COPY to \"lib\" \"org.jboss.resteasy:resteasy-undertow:3.0.4.Final:jar\" \"junit:junit:4.12:jar\" \"io.skullabs.powerlib:powerlib:0.1.0:jar\"";
	static final String EXTRACT = "EXTRACT to \"lib/power\" \"io.skullabs.powerlib:powerlib:0.1.0:jar\"";
	static final String CONF = "CONF build-directory \"target/output\" local-maven-repository \"src/test/resources/m2\"";
	static final String RUN = "RUN cat {BUILD}/lib/power/META-INF/MANIFEST.MF";
	// CHECKED
	
	@Test
	@SneakyThrows
	public void ensureThatCouldReadAllInstructions(){
		List<DefaultManifestInstruction> expectedInstructions = createExpectedInstructions();
		ManifestReader reader = ManifestReader.from( file( "src/test/resources/Manifest" ) );
		int cursor = 0;
		for ( ManifestInstruction instr : reader ) {
			DefaultManifestInstruction expected = expectedInstructions.get(cursor++);
			assertTrue(
				str("Different:\n\t%s\n\t%s\n", expected, instr),
				expected.equals( instr ) );
		}
		reader.close();
	}

	List<DefaultManifestInstruction> createExpectedInstructions(){
		List<String> instructionAsString = list( array( PACKAGE, REPOS, CONF_BUILD, COPY, EXTRACT, CONF, RUN ) );
		List<DefaultManifestInstruction> instructions = list();
		for ( String instr : instructionAsString )
			instructions.add( DefaultManifestInstruction.parse(instr) );
		return instructions;
	}
}
