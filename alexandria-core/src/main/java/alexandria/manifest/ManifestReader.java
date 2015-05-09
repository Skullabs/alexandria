package alexandria.manifest;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import lombok.RequiredArgsConstructor;
import power.io.File;
import power.io.LineReaderIterable;

@RequiredArgsConstructor
public class ManifestReader implements Iterable<ManifestInstruction>, Closeable {

	static final String COMMENT_PREFIX = "#";
	static final char SPACE = ' ';

	final LineReaderIterable lineReader;

	public static ManifestReader from(File file) {
		return new ManifestReader( file.readLines() );
	}

	public static ManifestReader from(InputStream file) {
		return new ManifestReader( new LineReaderIterable(file) );
	}

	@Override
	public Iterator<ManifestInstruction> iterator() {
		return new ManifestInstructionIterator();
	}
	
	@Override
	public void close() throws IOException {
		lineReader.close();
	}

	class ManifestInstructionIterator implements Iterator<ManifestInstruction> {
		
		final Iterator<String> lineIterator = lineReader.iterator();

		@Override
		public boolean hasNext() {
			return lineIterator.hasNext();
		}

		@Override
		public DefaultManifestInstruction next() {
			final String instructionLine = readNextInstructionLine();
			return DefaultManifestInstruction.parse( instructionLine );
		}

		String readNextInstructionLine() {
			final StringBuilder buffer = new StringBuilder();
			String line;
			do {
				line = trim( lineIterator.next() );
				if ( line.startsWith(COMMENT_PREFIX) || line.isEmpty() )
					continue;
				buffer.append(SPACE).append( normalize ( line ) );
			} while ( shouldReadAnotherLine(line) );
			return buffer.toString().trim();
		}

		boolean shouldReadAnotherLine( String line ) {
			return ( line.startsWith(COMMENT_PREFIX) || line.isEmpty() || line.endsWith("\\") )
					&& lineIterator.hasNext();
		}

		String trim( String line ) {
			return line.replaceFirst("^[ \t]+", "").trim();
		}
		
		String normalize( String line ) {
			return line.replaceFirst("\\\\$", "").trim();
		}
	}
}
