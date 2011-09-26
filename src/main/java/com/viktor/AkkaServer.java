package com.viktor;

import akka.actor.TypedActor;

import static akka.actor.Actors.remote;

public class AkkaServer extends TypedActor implements IdService {

	private int tableId = 0;

	@Override
	public int getNextTableId() {
		return tableId++;
	}

	public static void main(String[] args) {
		remote().start("localhost", 2553);
		IdService typedActor = TypedActor.newInstance(IdService.class, AkkaServer.class, 2000);
		remote().registerTypedActor("table-id-service", typedActor);
	}
}
