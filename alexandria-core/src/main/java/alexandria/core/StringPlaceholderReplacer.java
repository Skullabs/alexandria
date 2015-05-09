package alexandria.core;

import static power.util.Util.iterate;

import java.util.Iterator;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StringPlaceholderReplacer {

	static final String EMPTY_STRING = "";

	final String original;

	public String parse( Map<String, String> placeholderValues ) {
		StringBuilder buffer = new StringBuilder();
		for ( Replaceble replaceble : iterate( new Tokenizer() ) )
			buffer.append( replaceble.replace( placeholderValues ) );
		return buffer.toString();
	}

	class Tokenizer implements Iterator<Replaceble> {

		int cursor = 0;

		@Override
		public boolean hasNext() {
			return cursor < original.length();
		}

		@Override
		public Replaceble next() {
			if ( original.charAt( cursor ) == '{' )
				return nextPlaceHolderValue();
			return nextStaticValue();
		}

		Replaceble nextPlaceHolderValue() {
			int start = ++cursor;
			int end = original.indexOf( '}', cursor );
			String placeholder = original.substring( start, end );
			cursor = end + 1;
			return ( v ) -> v.getOrDefault( placeholder, EMPTY_STRING );
		}

		Replaceble nextStaticValue() {
			int start = cursor;
			int end = original.indexOf( '{', cursor );
			if ( end < 0 )
				end = original.length();
			String staticvalue = original.substring( start, end );
			cursor = end;
			return ( v ) -> staticvalue;
		}

	}

	interface Replaceble {
		String replace( Map<String, String> values );
	}
}