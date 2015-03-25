package com.example.testapp2;

import java.util.concurrent.Callable;

public class StringRequest implements Callable<String> {

	@Override
	public String call() throws Exception {
		Thread.sleep(2000);
		return "Hello Callable";
	}

}
