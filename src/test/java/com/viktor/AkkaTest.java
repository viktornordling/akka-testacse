package com.viktor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.TypedActor;
import akka.actor.TypedProps;
import com.typesafe.config.ConfigFactory;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.Assert.assertEquals;

public class AkkaTest {

	private final AtomicInteger counter = new AtomicInteger();

	private final int ITERATIONS = 10000;

	private final int THREADS = 100;

	private ActorSystem system;
	private ActorRef remoteActor;


	@Test
	public void testAkka() {
		ExecutorService executorService = Executors.newFixedThreadPool(THREADS);

		system = ActorSystem.create("LookupApplication", ConfigFactory.load().getConfig("remotelookup"));
		remoteActor = system.actorFor("akka://IdApplication@127.0.0.1:2552/user/typedIdService");

		IdService idService = TypedActor.get(system).typedActorOf(new TypedProps<IdServiceImpl>(IdService.class, IdServiceImpl.class), remoteActor);

		for (int i = 0; i < ITERATIONS; i++) {
			executorService.submit(tableIdLookup(idService, i));
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

	private Runnable tableIdLookup(final IdService actor, final int lookupId) {
		return new Runnable() {

			@Override
			public void run() {
				lookupTableId(actor, lookupId);
			}
		};
	}

	private void lookupTableId(IdService actor, int lookupId) {
		int nextTableId = actor.getNextTableId();
		System.out.println("Lookup: " + lookupId + ". Got: " + nextTableId);
		counter.incrementAndGet();
	}
}
