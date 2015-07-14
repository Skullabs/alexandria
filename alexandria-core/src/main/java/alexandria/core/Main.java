package alexandria.core;

import static power.io.IO.file;
import static power.io.IO.resourceFile;

import java.io.FileNotFoundException;

import power.io.File;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;
import alexandria.manifest.ManifestReader;

public class Main {

	private static final String DEFAULT_MANIFEST = "Manifest.default";

	public static void main(String[] args) throws ServiceProviderException, FileNotFoundException {
		if ( args.length == 0 ){
			showUsage();
			System.exit(1);
		}

		final File file = file(args[0]);
		runManifestFile(file);
		System.exit(0);
	}

	private static void showUsage() {
		final String msg = "Usage: alexandria <my-manifest-file>";
		System.out.println( msg );
	}

	private static void runManifestFile(final File file) throws ServiceProviderException, FileNotFoundException
	{
		assertFileExists(file);
		final ServiceProvider provider = new ServiceProvider();
		final ManifestRunner runner = provider.load(ManifestRunner.class);
		runner.read( ManifestReader.from( resourceFile(DEFAULT_MANIFEST) ) );
		runner.read( ManifestReader.from( file ) );
		runner.start();
	}

	private static void assertFileExists(File file) throws FileNotFoundException {
		if ( !file.exists() )
			throw new FileNotFoundException(file.getName());
	}
}
