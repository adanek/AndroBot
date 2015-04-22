/*
 * Original source for this code is
 * https://developer.android.com/samples/BluetoothChat
 */

package at.uibk.informatik.androbot.control;

/**
 * Defines several constants used between {@link BluetoothConnection} and the caller thread.
 */
public interface MessageTypes {

	public static final int CONNECTION_EVENT = 100;
	public static final int REQUEST_EVENT = 200;

	public static final int CONNECTION_STATE_CHANGED = 110;
	public static final int CONNECTION_STATE_CONNECTING = 111;
	public static final int CONNECTION_STATE_CONNECTED = 112;
	public static final int CONNECTION_STATE_DISCONNECTED = 113;
	public static final int CONNECTION_STATE_FAILED = 114;	
	public static final int CONNECTION_STATE_LOST = 115;

	public static final int CONNECTION_MESSAGE_EVENT = 130;
	public static final int CONNECTION_MESSAGE_SENT = 131;
	public static final int CONNECTION_MESSAGE_RECEIVED = 132;
	
	
	public static final int REQUEST_DONE = 201;

}