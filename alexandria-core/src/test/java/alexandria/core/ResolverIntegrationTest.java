package alexandria.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static power.io.IO.file;

import java.util.concurrent.Future;

import lombok.SneakyThrows;

import org.junit.Before;
import org.junit.Test;

import power.io.File;
import alexandria.core.resolver.MavenArtifact;
import alexandria.core.resolver.Resolver;
import alexandria.manifest.ManifestReader;
import trip.spi.Provided;
import trip.spi.ServiceProvider;

public class ResolverIntegrationTest {

	static final String DEFAULT = "src/main/resources/Manifest.default";
	static final String DEFAULT_TEST = "src/test/resources/Manifest";
	static final MavenArtifact EMPTY_TXT = MavenArtifact.from( "groupid.empty:empty:0.1.0-SNAPSHOT:txt" );
	static final MavenArtifact JUNIT = MavenArtifact.from( "junit:junit:4.12:jar" );
	static final File OUTPUT_DIR = file( "target/m2" );

	final ServiceProvider provider = spy( new ServiceProvider() );
	
	@Provided
	Resolver resolver;
	
	@Provided
	RuntimeContext context;

	@Provided
	ManifestRunner runner;
	
	@Test( timeout=30000 )
	@SneakyThrows
	public void ensureThatCanReadLocalFiles(){
		runCommands();
		Future<File> future = resolver.resolve( EMPTY_TXT );
		File file = future.get();
		assertTrue( file.exists() );
		assertEquals("empty-file: 0.1.0-SNAPSHOT", file.read());
	}

	@Test( timeout=30000 )
	@SneakyThrows
	public void ensureThatCanDownloadArtifactIntoLocalRepository(){
		runCommands();
		context.setProperty( Resolver.LOCAL_MAVEN_REPO, "target/m2");
		Future<File> future = resolver.resolve( JUNIT );
		File file = future.get();
		assertTrue( file.exists() );
	}

	void runCommands() throws Exception {
		runner.read( ManifestReader.from( file( DEFAULT ) ) );
		runner.read( ManifestReader.from( file( DEFAULT_TEST ) ) );
		runner.start();
	}

	@Before
	@SneakyThrows
	public void provideDependencies(){
		provider.providerFor(ServiceProvider.class, provider);
		provider.provideOn( this );
	}

	@Before
	public void removeOutputDirBeforeStartTests(){
		assertTrue( OUTPUT_DIR.removeRecursively() );
	}
}
