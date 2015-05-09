package alexandria.core;

import static alexandria.core.Logger.detail;
import static power.io.IO.file;
import static power.io.IO.unzip;
import static power.util.Throwables.io;
import static power.util.Util.list;

import java.util.List;
import java.util.concurrent.Future;

import lombok.RequiredArgsConstructor;
import power.io.File;
import trip.spi.Provided;
import trip.spi.Singleton;
import alexandria.core.resolver.MavenArtifact;
import alexandria.core.resolver.Resolver;
import alexandria.manifest.ManifestInstruction;
import alexandria.manifest.ParamTokenizer;

@Singleton( exposedAs = CommandFactory.class, name = "EXTRACT" )
public class ExtractCommandFactory implements CommandFactory {

	@Provided
	RuntimeContext context;

	@Provided
	Resolver resolver;

	@Override
	public RunnableCommand read( ManifestInstruction instruction ) {
		ParamTokenizer params = instruction.iterator();
		File targetPath = context.newValidFile( file( params.nextAsNamedParam( "to" ) ) );
		ExtractRunnableCommand command = new ExtractRunnableCommand( targetPath );
		while( params.hasNext() )
			command.memorizeCopiableArtifact( params.next() );
		return command;
	}

	@RequiredArgsConstructor
	class ExtractRunnableCommand implements RunnableCommand {

		final List<MavenArtifact> artifacts = list();
		final File targetPath;

		public void memorizeCopiableArtifact( String artifactAsString ){
			artifacts.add( MavenArtifact.from(artifactAsString) );
		}

		@Override
		public void run() throws Exception {
			detail( "Extract artifacts to %s", targetPath.getAbsolutePath() );
			List<Future<File>> resolvedArtifacts = resolver.resolve( artifacts );
			for ( Future<File> future : resolvedArtifacts )
				extractToDestinationFolder( future );
		}

		void extractToDestinationFolder( Future<File> future ) throws Exception {
			File resolved = future.get();
			if ( !resolved.exists() )
				throw io( "Can't download the file %s", resolved.getName() );
			unzip( resolved )
				.notifier( ( s ) ->
						detail( " expanding %s", s ) )
				.into( targetPath );
			detail( "Extracted " + resolved.getName() );
		}
	}
}
