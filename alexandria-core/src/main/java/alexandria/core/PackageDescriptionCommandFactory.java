package alexandria.core;

import java.util.Iterator;

import trip.spi.Provided;
import trip.spi.Singleton;
import alexandria.manifest.ManifestInstruction;

@Singleton( exposedAs=CommandFactory.class, name="PACKAGE" )
public class PackageDescriptionCommandFactory implements CommandFactory {
	
	static final String MAINTENER = "maintener";
	static final String NAME = "name";
	static final String VERSION = "version";
	
	@Provided
	RuntimeContext context;

	public RunnableCommand read( ManifestInstruction instruction ) {
		Iterator<String> params = instruction.iterator();
		while( params.hasNext() ) {
			String param = params.next();
			setPackageParameter(params, param);
		}
		return null;
	}

	void setPackageParameter(Iterator<String> params, String param) {
		if ( MAINTENER.equals(param) )
			context.getPackageDescription().setMaintainer( params.next() );
		else if ( NAME.equals(param) )
			context.getPackageDescription().setName( params.next() );
		else if ( VERSION.equals(param) )
			context.getPackageDescription().setVersion( params.next() );
		else
			throw new IllegalArgumentException( "Invalid PACKAGE parameter: " + param );
	}
}
