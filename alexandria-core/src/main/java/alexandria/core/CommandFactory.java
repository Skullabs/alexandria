package alexandria.core;

import alexandria.manifest.ManifestInstruction;

/**
 * @author miere.teixeira
 */
public interface CommandFactory {
	RunnableCommand read( ManifestInstruction instruction );
}