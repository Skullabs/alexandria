package alexandria.core;

import static org.junit.Assert.assertEquals;
import static power.util.Util.array;
import static power.util.Util.list;
import lombok.val;

import org.junit.Test;

import alexandria.manifest.InvalidInstructionException;
import alexandria.manifest.DefaultManifestInstruction;

public class ManifestInstructionTest {
	
	static final String INSTRUCTION = "EXTRACT to \"lib\" \"xcom-broker.jar\"";
	static final String NON_FORMATTED_INSTRUCTION = "EXTRACT to  \"lib\"   \"xcom-broker.jar\"";

	@Test
	public void ensureThatCanIterateOverParams() {
		val expected = list(array("to", "lib", "xcom-broker.jar"));
		int cursor = 0;
		for (val param : DefaultManifestInstruction.parse(INSTRUCTION))
			assertEquals(expected.get(cursor++), param);
	}

	@Test
	public void ensureThatCanIterateOverParamsForNonFormattedInstruction() {
		val expected = list(array("to", "lib", "xcom-broker.jar"));
		int cursor = 0;
		for (val param : DefaultManifestInstruction.parse(NON_FORMATTED_INSTRUCTION))
			assertEquals(expected.get(cursor++), param);
	}

	@Test
	public void ensureThatCanRetrieveTheRawParameterString() {
		val expected = "to \"lib\" \"xcom-broker.jar\"";
		val instruction = DefaultManifestInstruction.parse(INSTRUCTION);
		assertEquals( expected, instruction.getRawParams() );
	}

	@Test
	public void ensureThatCanRetrieveTheInstructionName() {
		val expected = "EXTRACT";
		val instruction = DefaultManifestInstruction.parse(INSTRUCTION);
		assertEquals( expected, instruction.getName() );
	}

	@Test
	public void ensureThatInstructionsWithSameNameAndRawParametersAreEquals(){
		assertEquals( DefaultManifestInstruction.parse(INSTRUCTION), DefaultManifestInstruction.parse(INSTRUCTION) );
	}
	
	@Test( expected=InvalidInstructionException.class)
	public void ensureThatCantParseInvalidInstruction(){
		DefaultManifestInstruction.parse( "INVALID" );
	}
}
