package alexandria.core;

import static power.util.Util.str;
import alexandria.manifest.DefaultManifestInstruction;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class Logger {

	public static void detail( String string, Object... params ) {
		log.info( str( " > " + string, params ) );
	}

	public static void command( DefaultManifestInstruction instruction ) {
		log.info( "$ " + instruction.toString() );
	}

	public static void info( String string, Object... params ) {
		log.info( str( string, params ) );
	}
}
