package com.viktor;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static akka.actor.Actors.remote;
import static junit.framework.Assert.assertEquals;

public class AkkaTest {

	private final AtomicInteger counter = new AtomicInteger();

	private final int ITERATIONS = 1000;

	private final int THREADS = 100;

	@Test
	public void testAkka() {
		ExecutorService executorService = Executors.newFixedThreadPool(THREADS);
		for (int i = 0; i < ITERATIONS; i++) {
			final IdService service = remote().typedActorFor(IdService.class, "table-id-service", "localhost", 2553);
			executorService.submit(tableIdLookup(service, i));
		}
		try {
			System.out.println("Shutting down");
			executorService.shutdown();
			System.out.println("Awaiting termination.");
			executorService.awaitTermination(10, TimeUnit.SECONDS);
			System.out.println("Terminated.");
			System.out.println("Counter: " + counter.get());
			assertEquals(ITERATIONS, counter.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private Runnable tableIdLookup(final IdService service, final int lookupId) {
		return new Runnable() {

			@Override
			public void run() {
				lookupTableId(service, lookupId);
			}
		};
	}

	private void lookupTableId(IdService service, int lookupId) {
		int result = service.getNextTableId();
		System.out.println("Result of lookup " + lookupId + " = " + result);
		counter.incrementAndGet();
	}
}
