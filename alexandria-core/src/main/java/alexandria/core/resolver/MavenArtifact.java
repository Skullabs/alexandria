package alexandria.core.resolver;

import static power.util.Util.str;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MavenArtifact {

	final String groupId;
	final String artifactId;
	final String version;
	final String packaging;

	public String getRelativeMavenPath(){
		return str( "%s/%s/%s/%s-%s.%s",
				groupId.replace( '.', '/' ), artifactId, version,
			artifactId, version, packaging );
	}
	
	public String toString(){
		return str( "%s:%s:%s:%s",
				groupId, artifactId, version, packaging );
	}

	public static MavenArtifact from( String artifactString ) {
		try {
			String[] tokens = artifactString.split( ":" );
			return new MavenArtifact( tokens[0], tokens[1], tokens[2], tokens[3] );
		} catch ( ArrayIndexOutOfBoundsException cause ) {
			throw new IllegalArgumentException( str( "Bad artifact definition: %s", artifactString ), cause );
		}
	}
}
