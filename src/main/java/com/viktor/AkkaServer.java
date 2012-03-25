package com.viktor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.TypedActor;
import akka.actor.TypedProps;
import akka.kernel.Bootable;
import com.typesafe.config.ConfigFactory;

public class AkkaServer implements Bootable {
	private ActorSystem system;

	public AkkaServer() {
		system = ActorSystem.create("IdApplication", ConfigFactory.load().getConfig("id-generator"));
		ActorRef actor = system.actorOf(new Props(IdGeneratorActor.class), "idService");

		IdService idService = TypedActor.get(system).typedActorOf(new TypedProps<IdServiceImpl>(IdService.class, IdServiceImpl.class), "typedIdService");
		System.out.println("Server running..");
	}

	public static void main(String[] args) {
		new AkkaServer();

	}

	@Override
	public void startup() {

	}

	@Override
	public void shutdown() {
		system.shutdown();
	}
}
