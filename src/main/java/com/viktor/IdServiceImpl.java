package com.viktor;

public class IdServiceImpl implements IdService {

	private int id = 0;

	@Override
	public int getNextTableId() {
		System.out.println("Returning id " + id);
		return id++;
	}
}
