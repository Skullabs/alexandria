package alexandria.core;

import static power.io.IO.file;
import static power.util.Util.list;
import static power.util.Util.map;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import power.io.File;
import trip.spi.Singleton;

@Setter
@Getter
@Singleton
public class RuntimeContext {

	final List<RunnableCommand> runnablePlugins = list();
	final List<String> repositoryList = list();
	final Map<String,String> properties = map();
	final PackageDescription packageDescription = new PackageDescription();

	public void addToRuntimeStack( RunnableCommand plugin ) {
		runnablePlugins.add( plugin );
	}

	public void addRepository( String url ) {
		repositoryList.add(url);
	}

	public void setProperty( String key, String value ) {
		properties.put(key, value);
	}

	public String getProperty( String key ) {
		return properties.get(key);
	}

	public File newValidFile( File file ) {
		if ( file.getPath().startsWith( "/" ) )
			return file;
		final File buildDir = file( getProperty( ConfCommandFactory.BUILD_DIRECTORY ) );
		return buildDir.newFileHere( file.getPath() );
	}
}
