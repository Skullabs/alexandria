package alexandria.core;

import static power.util.Throwables.silently;
import static power.util.Util.len;

import java.io.IOException;
import java.io.InputStream;

import lombok.RequiredArgsConstructor;
import trip.spi.Singleton;
import alexandria.manifest.ManifestInstruction;

@Singleton( exposedAs = CommandFactory.class, name = "RUN" )
public class RunCommandFactory implements CommandFactory {

	@Override
	public RunnableCommand read( ManifestInstruction instruction ) {
		int size = len( instruction );
		String[] commandAndParams = new String[size];
		int counter = 0;
		for ( String param : instruction )
			commandAndParams[counter++] = param;

		return ( ) -> run( commandAndParams );
	}

	void run( final String[] commandLineString ) {
		silently( ( ) -> {
			final Runtime runtime = Runtime.getRuntime();
			final Process exec = runtime.exec( commandLineString );
			runtime.addShutdownHook( new ProcessDestroyer( exec ) );
			printAsynchronously( exec.getInputStream() );
			printAsynchronously( exec.getErrorStream() );
			if ( exec.waitFor() > 0 )
				throw new RuntimeException( "The command has failed to run." );
		} );
	}

	void printAsynchronously( final InputStream stream ) {
		new Thread( new ProcessOutputPrinter( stream ) ).start();
	}
}

@RequiredArgsConstructor
class ProcessDestroyer extends Thread {
	final Process process;

	@Override
	public void run() {
		process.destroy();
	}
}

class ProcessOutputPrinter implements Runnable {

	private InputStream pipe;

	ProcessOutputPrinter( InputStream pipe ) {
		if ( pipe == null )
			throw new NullPointerException( "bad pipe" );
		this.pipe = pipe;
	}

	@Override
	public void run() {
		try {
			doReading();
		} catch ( IOException e ) {
			e.printStackTrace();
		} finally {
			if ( this.pipe != null )
				try {
					this.pipe.close();
				} catch ( IOException e ) {}
		}
	}

	void doReading() throws IOException {
		byte buffer[] = new byte[2048];
		int read = this.pipe.read( buffer );
		while ( read >= 0 ) {
			System.out.write( buffer, 0, read );
			read = this.pipe.read( buffer );
		}
	}
}