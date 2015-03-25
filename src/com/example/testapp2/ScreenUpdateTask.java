package com.example.testapp2;

import android.os.Handler;

public class ScreenUpdateTask implements Runnable {

	private Handler handler;
	private Handler worker;

	public ScreenUpdateTask(Handler uiHandler, Handler worker) {
		this.handler = uiHandler;
		this.worker = worker;
	}

	@Override
	public void run() {
		System.out.println(Thread.currentThread().getId());
		this.handler.obtainMessage(1, "Hello Handler!").sendToTarget();
		
		this.worker.postDelayed(this, 1000);
	}

}
