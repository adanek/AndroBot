package at.uibk.informatik.androbot.app;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import at.uibk.informatik.androbot.contracts.Direction;
import at.uibk.informatik.androbot.contracts.IRobot;

public class MainActivity extends Activity {

	private TextView tv1;
	private BluetoothAdapter BA;
	private IRobot robot;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tv1 = (TextView) findViewById(R.id.textView1);
		BA = BluetoothAdapter.getDefaultAdapter();
	}

	public void on(View view) {
		if (!BA.isEnabled()) {
			Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(turnOn, 0);

			tv1.setText("BLUETOOTH activated");
		} else {

			tv1.setText("BLUETOOTH already running");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// move
	public void move(View v) {

		switch (v.getId()) {
		case R.id.forward:
			robot.move();
			break;
		case R.id.backward:
			robot.move();
			break;
		case R.id.left:
			robot.turn(Direction.LEFT);
			break;
		case R.id.right:
			robot.turn(Direction.RIGHT);
			break;
		}

	}
}