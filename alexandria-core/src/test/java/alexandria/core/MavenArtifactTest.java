package alexandria.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import alexandria.core.resolver.MavenArtifact;

public class MavenArtifactTest {

	@Test
	public void ensureThatCanRetrieveRelativePathFromMavenArtifact(){
		MavenArtifact artifact = MavenArtifact.from("junit:junit:4.12:jar");
		assertEquals( "junit/junit/4.12/junit-4.12.jar", artifact.getRelativeMavenPath() );
		artifact = MavenArtifact.from("org.projectlombok:lombok:1.14.8:jar");
		assertEquals( "org/projectlombok/lombok/1.14.8/lombok-1.14.8.jar", artifact.getRelativeMavenPath() );
	}
}
