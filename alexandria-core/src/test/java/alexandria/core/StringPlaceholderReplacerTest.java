package alexandria.core;

import static org.junit.Assert.assertEquals;
import static power.util.Util.stringMap;

import java.util.Map;

import org.junit.Test;

public class StringPlaceholderReplacerTest {

	final Map<String, String> params = createParams();

	@Test
	public void ensureThatCanReplaceValues() {
		assertEquals( ".m2", parse( ".m2" ) );
		assertEquals( "/home/linus/.m2", parse( "{HOME}/.m2" ) );
		assertEquals( "/home/linus/.m2/pt_BR", parse( "{HOME}/.m2/{LOCALE}" ) );
		assertEquals( "LOCALE: pt_BR", parse( "LOCALE: {LOCALE}" ) );
	}

	String parse( String original ) {
		return new StringPlaceholderReplacer( original ).parse( params );
	}

	Map<String, String> createParams() {
		Map<String, String> params = stringMap();
		params.put( "HOME", "/home/linus" );
		params.put( "LOCALE", "pt_BR" );
		return params;
	}
}
