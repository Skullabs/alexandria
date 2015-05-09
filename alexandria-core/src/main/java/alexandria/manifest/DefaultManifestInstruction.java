package alexandria.manifest;

import static power.util.Util.str;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@Getter
@RequiredArgsConstructor
public class DefaultManifestInstruction implements ManifestInstruction {

	final String name;
	final String rawParams;

	public static DefaultManifestInstruction parse( String instruction ) {
		try {
			int nextSpace = instruction.indexOf(' ');
			String name = instruction.substring(0, nextSpace);
			String rawParams = instruction.substring(nextSpace+1);
			return new DefaultManifestInstruction( name, rawParams );
		} catch ( StringIndexOutOfBoundsException cause ) {
			throw new InvalidInstructionException( instruction );
		}
	}

	public String toString(){
		return str( "%s %s", name, rawParams );
	}
}
