package com.example.testapp2;

import android.os.Handler;


public class BackgroundWorker {
	
	BackgroundworkerThread workerThread;
	private Handler handler;
	
	public BackgroundWorker(){
		this.workerThread = new BackgroundworkerThread("Backgroundworker");
		this.workerThread.start();
		
		this.handler = new Handler(this.workerThread.getLooper());
	}
	
	public Handler getHandler(){
		return this.handler;
	}
	
	public void stop(){
		this.workerThread.cancel();
		this.handler = null;
	}
}
