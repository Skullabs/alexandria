package alexandria.core.resolver;

import static alexandria.core.Logger.detail;
import static power.io.IO.file;
import static power.io.IO.iterate;
import static power.util.Throwables.io;
import static power.util.Throwables.silently;
import static power.util.Util.list;
import static power.util.Util.str;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import power.io.ByteBuffer;
import power.io.File;
import power.io.InputStreamIterable;
import stalkr.http.Requests;
import trip.spi.Provided;
import trip.spi.Singleton;
import alexandria.core.RuntimeContext;

import com.ning.http.client.Response;

@Slf4j
@Singleton
public class Resolver {

	public static final String LOCAL_MAVEN_REPO = "local-maven-repository";
	
	@Provided
	Requests requests;
	
	@Provided
	RuntimeContext context;

	public List<Future<File>> resolve( List<MavenArtifact> artifacts ) throws IOException {
		List<Future<File>> resolvedArtifacts = list();
		for ( MavenArtifact artifact : artifacts )
			resolvedArtifacts.add( resolve( artifact ) );
		return resolvedArtifacts;
	}

	public Future<File> resolve( MavenArtifact artifact ) throws IOException {
		File localM2Repo = ensureLocalRepoDirExists();
		File resolvedArtifact = file( "%s/%s", localM2Repo.getAbsolutePath(), artifact.getRelativeMavenPath() );
		ResolvedArtifact resolved = new ResolvedArtifact(artifact, resolvedArtifact);
		downloadOrRetrieveFromLocalRepository( resolved );
		return resolved.getFuture();
	}

	File ensureLocalRepoDirExists() throws IOException {
		File dir = file( context.getProperty(LOCAL_MAVEN_REPO) );
		if ( !dir.mkdirs() && !dir.exists() )
			throw io( "Can't create: %s", dir.getAbsolutePath() );
		return dir;
	}

	void downloadOrRetrieveFromLocalRepository( ResolvedArtifact resolved ) throws IOException {
		if ( resolved.getResolvedArtifact().exists() )
			resolved.setResolved();
		else
			downloadFileAt( resolved );
	}

	void downloadFileAt( ResolvedArtifact resolved ) throws IOException {
		new Downloader(resolved).download();
	}

	@RequiredArgsConstructor
	class Downloader {

		final Iterator<String> urls = context.getRepositoryList().iterator();
		final ResolvedArtifact resolved;

		void download() throws IOException {
			if ( urls.hasNext() ) {
				downloadFromNextAvailableRepository();
			} else {
				log.warn( str( "Can't to download %s", resolved.getArtifact().toString() ) );
				resolved.setResolved();
			}
		}

		private void downloadFromNextAvailableRepository() throws IOException {
			String url = str( "%s/%s", urls.next(), resolved.getArtifact().getRelativeMavenPath() );
			URI uri = silently( ()-> new URI(url) );
			detail( "Downloading %s", uri );
			requests.get(uri.toString()).execute( this::handleDownloadResponse );
		}

		void handleDownloadResponse( final Requests requests, final Response response ) throws IOException {
			if ( response == null || response.getStatusCode() > 299 ) {
				download();
				return;
			}
			copyDownloadedFileToArtifactFile( response );
		}

		void copyDownloadedFileToArtifactFile( final Response response ) throws IOException {
			try {
				resolved.getResolvedArtifact().getParentFile().ensureExistsAsDirectory();
				copyDownloadedFileToArtifactFile( response.getResponseBodyAsStream() );
				resolved.setResolved();
				// UNCHECKED: It should catch all failures here and try download
				// again from another source
			} catch ( Exception cause ) {
				// CHECKED
				log.warn( "Download from the last source have failed. It will try to find another download source.", cause );
				download();
			}
		}

		void copyDownloadedFileToArtifactFile( InputStream inputStream  ) throws IOException {
			InputStreamIterable iterable = iterate(inputStream);
			FileOutputStream output = resolved.getResolvedArtifact().openForWrite();
			for ( ByteBuffer buffer: iterable )
				output.write( buffer.buffer(), 0, buffer.length() );
			iterable.close();
			output.close();
		}
	}
}
