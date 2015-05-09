package alexandria.core;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import alexandria.manifest.ManifestInstruction;

@RequiredArgsConstructor( staticName = "wrap" )
public class ContextualManifestInstruction implements ManifestInstruction {

	final Map<String, String> params;
	final ManifestInstruction wrapped;

	@Override
	public String getName() {
		return parse( wrapped.getName() );
	}

	@Override
	public String getRawParams() {
		return parse( wrapped.getRawParams() );
	}

	String parse( String original ) {
		return new StringPlaceholderReplacer( original ).parse( params );
	}

	@Override
	public String toString() {
		return wrapped.toString();
	}
}
