package alexandria.core;

import static power.util.Throwables.silently;
import static power.util.Util.str;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import trip.spi.Provided;
import trip.spi.ServiceProvider;
import trip.spi.Singleton;
import alexandria.manifest.InvalidInstructionException;
import alexandria.manifest.ManifestInstruction;

@Slf4j
@Singleton
public class ManifestRunner {

	@Provided
	RuntimeContext context;

	@Provided
	ServiceProvider provider;
	
	boolean readDefaultConfiguration = false;

	public void read( Iterable<ManifestInstruction> instructions ) {
		for ( ManifestInstruction instruction : instructions )
			tryToFindPluginFor( ContextualManifestInstruction.wrap( context.getProperties(), instruction ) );
	}

	void tryToFindPluginFor( ManifestInstruction instruction ) {
		val plugin = silently( ( ) -> provider.load( CommandFactory.class, instruction.getName() ) );
		if ( plugin != null ) {
			val runnablePlugin = plugin.read( instruction );
			context.addToRuntimeStack( CommandPrinterAndRunner.wrap( instruction, runnablePlugin ) );
		} else
			throw new InvalidInstructionException( instruction );
	}

	public void start() {
		showPackageDescription();
		try {
			for ( val plugin : context.getRunnablePlugins() )
				plugin.run();
			showFinishMessage( "SUCCESS" );
		// UNCHECKED: Should handle any thrown exception here
		} catch ( Throwable cause ) {
		// CHECKED
			handleErrorAndFinish(cause);
		}
	}

	void showPackageDescription(){
		PackageDescription desc = context.getPackageDescription();
		log.info( "----------------------------------------------------" );
		log.info( " " + desc.getMaintainer() );
		log.info( str(" %s - %s", desc.getName(), desc.getVersion() ) );
		log.info( "----------------------------------------------------" );
		log.info( "" );
	}
	
	void handleErrorAndFinish( Throwable cause ) {
		log.error( "Can't finish the build", cause);
		showFinishMessage( "ERROR" );
	}

	void showFinishMessage( String status ){
		log.info( "----------------------------------------------------" );
		log.info( " FINISHED: " + status );
		log.info( "----------------------------------------------------\n" );
	}
}
