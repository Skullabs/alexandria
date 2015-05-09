package alexandria.core;

import static alexandria.core.Logger.detail;
import static power.io.IO.file;
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

@Singleton( exposedAs=CommandFactory.class, name="COPY" )
public class CopyCommandFactory implements CommandFactory {
	
	@Provided
	RuntimeContext context;
	
	@Provided
	Resolver resolver;

	@Override
	public RunnableCommand read( ManifestInstruction instruction ) {
		ParamTokenizer params = instruction.iterator();
		File targetPath = context.newValidFile( file( params.nextAsNamedParam( "to" ) ) );
		CopyRunnableCommand command = new CopyRunnableCommand( targetPath );
		while( params.hasNext() )
			command.memorizeCopiableArtifact( params.next() );
		return command;
	}

	@RequiredArgsConstructor
	class CopyRunnableCommand implements RunnableCommand {

		final List<MavenArtifact> artifacts = list();
		final File targetPath;

		public void memorizeCopiableArtifact( String artifactAsString ){
			artifacts.add( MavenArtifact.from(artifactAsString) );
		}

		@Override
		public void run() throws Exception {
			detail( "Copying artifacts to %s", targetPath.getAbsolutePath() );
			List<Future<File>> resolvedArtifacts = resolver.resolve( artifacts );
			for ( Future<File> future : resolvedArtifacts )
				copyToDestinationFolder(future);
		}

		void copyToDestinationFolder(Future<File> future) throws Exception {
			File resolved = future.get();
			if ( !resolved.exists() )
				throw io( "Can't download the file %s", resolved.getName() );
			File newFile = targetPath.newFileHere( resolved.getName() );
			resolved.copyTo(newFile);
			detail( "Copied " + newFile.getName() );
		}
	}
}
