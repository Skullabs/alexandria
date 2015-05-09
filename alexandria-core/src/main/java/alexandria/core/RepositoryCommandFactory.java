package alexandria.core;

import trip.spi.Provided;
import trip.spi.Singleton;
import alexandria.manifest.ManifestInstruction;

@Singleton(exposedAs=CommandFactory.class, name="REPOS")
public class RepositoryCommandFactory implements CommandFactory {
	
	@Provided
	RuntimeContext context;

	@Override
	public RunnableCommand read( ManifestInstruction instruction ) {
		for ( String repo : instruction )
			context.addRepository( repo.replaceFirst("\\/+$", "") );
		return null;
	}
}
