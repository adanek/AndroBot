package com.example.testapp2;

import android.os.HandlerThread;
import android.util.Log;

public class BackgroundworkerThread extends HandlerThread {

	private volatile boolean running;
	public BackgroundworkerThread(String name) {
		super(name);
		this.running = true;
	}
	
	@Override
	public void run() {
		super.run();
		
		while(running){
			
			try {
				this.wait();
			} catch (InterruptedException e) {
				
			}
		}
		
		Log.d("BGWThread", "finished");
	}
	
	public synchronized void cancel(){
		this.running = false;
	}

}
