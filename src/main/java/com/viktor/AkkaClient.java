package com.viktor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static akka.actor.Actors.remote;

public class AkkaClient {

	private final AtomicInteger counter = new AtomicInteger();

	public AkkaClient() {
		ExecutorService executorService = Executors.newFixedThreadPool(100);
		for (int i = 0; i < 10000; i++) {
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
			System.exit(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new AkkaClient();
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
