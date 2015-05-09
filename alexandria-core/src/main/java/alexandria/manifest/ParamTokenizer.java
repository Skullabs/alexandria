package alexandria.manifest;

import java.util.Iterator;

public class ParamTokenizer implements Iterator<String> {

	final String formatted;
	int cursor = 0;

	public ParamTokenizer( String rawParams ) {
		this.formatted = rawParams.replaceAll( "  +", " " );
	}

	@Override
	public boolean hasNext() {
		return cursor < formatted.length();
	}

	@Override
	public String next() {
		final String splitter = getNextSplitter();

		int end = formatted.indexOf( splitter, cursor );
		if ( end == -1 )
			end = formatted.length();

		final String next = formatted.substring( cursor, end );
		cursor = end + splitter.length();
		return next.replaceFirst( "\"$", "" );
	}

	private String getNextSplitter() {
		String splitter = " ";
		if ( formatted.charAt( cursor ) == '"' ) {
			cursor++;
			splitter = "\" ";
		}
		return splitter;
	}

	public String nextAsNamedParam( String name ) {
		if ( !hasNext() || !name.equals( next() ) )
			throw new InvalidInstructionException( name );
		return next();
	}
}