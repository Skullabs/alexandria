package alexandria.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static power.io.IO.file;
import lombok.SneakyThrows;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import power.io.File;
import trip.spi.Provided;
import trip.spi.ServiceProvider;
import alexandria.core.resolver.Resolver;
import alexandria.manifest.InvalidInstructionException;
import alexandria.manifest.ManifestReader;

@Ignore
@RunWith( MockitoJUnitRunner.class )
public class ManifestRunnerIntegrationTest {

	static final String DEFAULT = "src/main/resources/Manifest.default";
	static final String DEFAULT_TEST = "src/test/resources/Manifest";
	static final String DEFAULT_INVALID = "src/test/resources/Manifest.invalid";
	static final String REPO_SONATYPE = "http://central.maven.org/maven2";
	static final String REPO_CENTRAL = "http://repo1.maven.org/maven2";
	static final File LOCAL_REPO_DIR = file( "target/m2" );
	static final File OUTPUT_DIR = file( "output" );

	final ServiceProvider provider = spy( new ServiceProvider() );

	@Provided
	ManifestRunner runner;

	@Provided
	RuntimeContext context;

	@Test
	@SneakyThrows
	public void ensureThatCanReadPackageDefinition(){
		runCommands();
		assertEquals( "Ibratan Ltda.", context.packageDescription.getMaintainer() );
		assertEquals( "ttm-cobol-coboss", context.packageDescription.getName() );
		assertEquals( "1.2.0-SNAPSHOT", context.packageDescription.getVersion() );
	}

	@Test
	@SneakyThrows
	public void ensureThatCanReadAllDefinedRepos(){
		runCommands();
		assertTrue( context.repositoryList.contains(REPO_CENTRAL) );
		assertTrue( context.repositoryList.contains(REPO_SONATYPE) );
	}

	@Test
	@SneakyThrows
	public void ensureThatCanOverrideBuildDirectoryAndHaveCreatedIt(){
		runCommands();
		final String buildDir = context.getProperty( ConfCommandFactory.BUILD_DIRECTORY );
		assertEquals( "target/output", buildDir );
		assertTrue( file( buildDir ).exists() );
	}

	@Test( expected = InvalidInstructionException.class )
	public void ensureThatThrowsExceptionWhenUnknowInstructionIsSetInManifestFile() {
		runner.read( ManifestReader.from( file( DEFAULT_INVALID ) ) );
	}

	void runCommands() throws Exception {
		runner.read( ManifestReader.from( file( DEFAULT ) ) );
		runner.read( ManifestReader.from( file( DEFAULT_TEST ) ) );
		context.setProperty( Resolver.LOCAL_MAVEN_REPO, "target/m2");
		runner.start();
	}

	@Before
	@SneakyThrows
	public void provideDependencies(){
		provider.providerFor(ServiceProvider.class, provider);
		provider.provideOn( this );
		assertTrue( LOCAL_REPO_DIR.removeRecursively() );
	}

	@After
	public void removeUnneededFiles() {
		assertTrue( OUTPUT_DIR.removeRecursively() );
	}
}
