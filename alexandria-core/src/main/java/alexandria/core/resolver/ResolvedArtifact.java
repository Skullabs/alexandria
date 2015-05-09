package alexandria.core.resolver;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import power.io.File;

@Getter
@RequiredArgsConstructor
public class ResolvedArtifact {

	final AsyncFuture<File> future = new AsyncFuture<>();
	final MavenArtifact artifact;
	final File resolvedArtifact;

	public void setResolved(){
		future.set(resolvedArtifact);
	}
}
