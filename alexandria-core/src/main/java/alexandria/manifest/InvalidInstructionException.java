package alexandria.manifest;

import static power.util.Util.str;

public class InvalidInstructionException extends RuntimeException {

	private static final long serialVersionUID = 4363000626335948394L;

	public InvalidInstructionException( String instruction ) {
		super( str("Invalid instruction: %s", instruction ) );
	}
	
	public InvalidInstructionException( ManifestInstruction instruction ) {
		this( instruction.toString() );
	}
}
