package alexandria.core;

import static power.io.IO.file;
import static power.util.Throwables.io;

import java.io.IOException;
import java.util.Iterator;

import power.io.File;
import trip.spi.Provided;
import trip.spi.Singleton;
import alexandria.manifest.ManifestInstruction;

@Singleton( exposedAs=CommandFactory.class, name="CONF" )
public class ConfCommandFactory implements CommandFactory {
	
	public static final String BUILD_DIRECTORY = "build-directory";
	
	@Provided
	RuntimeContext context;

	@Override
	public RunnableCommand read( ManifestInstruction instruction ) {
		Iterator<String> params = instruction.iterator();
		while ( params.hasNext() )
			context.setProperty( params.next(), params.next() );
		return this::createBuildDirectory;
	}

	void createBuildDirectory() throws IOException {
		File dir = file( context.getProperty(BUILD_DIRECTORY) );
		if ( !dir.mkdirs() && !dir.exists() )
			throw io( "Can't create: %s", dir.getAbsolutePath() );
	}
}
