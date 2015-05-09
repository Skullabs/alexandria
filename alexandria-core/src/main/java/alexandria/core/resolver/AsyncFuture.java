package alexandria.core.resolver;

import static power.util.Throwables.silently;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AsyncFuture<T> implements Future<T> {

	final BlockingQueue<T> reference = new ArrayBlockingQueue<>(1);
	boolean cancelled = false;
	boolean done = false;

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	@Override
	public T get() throws InterruptedException {
		return reference.take();
	}

	@Override
	public T get(long timeout, TimeUnit unit) {
		throw new UnsupportedOperationException();
	}

	public void set( T object ) {
		silently(()->{
			reference.put( object );
			done = true;
		});
	}
}
