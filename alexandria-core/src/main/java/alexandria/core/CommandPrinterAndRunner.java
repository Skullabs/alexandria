package alexandria.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import alexandria.manifest.ManifestInstruction;

@Slf4j
@RequiredArgsConstructor( staticName = "wrap" )
public class CommandPrinterAndRunner implements RunnableCommand {

	final ManifestInstruction instruction;
	final RunnableCommand command;

	@Override
	public void run() throws Exception {
		log.info( "$ " + instruction.toString() );
		if ( command != null )
			command.run();
		log.info( "" );
	}
}
