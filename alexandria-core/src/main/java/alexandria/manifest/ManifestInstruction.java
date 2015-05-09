package alexandria.manifest;

public interface ManifestInstruction extends Iterable<String> {

	String getName();

	String getRawParams();

	default ParamTokenizer iterator() {
		return new ParamTokenizer( getRawParams() );
	}
}
