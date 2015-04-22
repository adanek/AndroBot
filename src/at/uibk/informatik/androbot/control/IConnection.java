package at.uibk.informatik.androbot.control;

import android.os.Handler;

public interface IConnection {

	/**
	 * Returns the current state of the connection
	 * 
	 * @return An Integer representing the current state of the connection
	 */
	public abstract int getState();

	/**
	 * Start the ConnectThread to initiate a connection to a remote device.
	 *
	 * @param device
	 *            The BluetoothDevice to connect
	 * @param secure
	 *            Socket Security type - Secure (true) , Insecure (false)
	 */
	public abstract void connect();

	/**
	 * Stop all threads
	 */
	public abstract void stop();

	/**
	 * Write to the ConnectedThread in an unsynchronized manner
	 *
	 * @param out The bytes to write
	 * @see ConnectedThread#write(byte[])
	 */
	public abstract void write(byte[] out);
	
	/**
	 * Sets the Handler which handles the ReadEvents for the Connection 
	 * The handler can only be set in state STATE_NONE
	 * 
	 * @param readHandler The handler which should receive the ReadEvents
	 */
	public abstract void setReadHandler(Handler readHandler);

}