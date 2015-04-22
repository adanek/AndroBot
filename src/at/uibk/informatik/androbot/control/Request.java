package at.uibk.informatik.androbot.control;

import java.util.LinkedList;
import java.util.List;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Encapsulates one command which will sent to the remote device in an own thread.
 */
public class Request implements Runnable {

	private static final int SUFFIX_LENGTH = 2;
	private static final int COMMAND_LENGTH = 1;
	private static int nextId = 0;

	// ********************************************** Locals **********************************************************

	private static final String LOG_TAG = "Request";

	private int id;
	private IConnection conn;
	private Handler handler;
	private long runtime;
	private List<Byte> params;
	private char command;
	private boolean responseWhenDone;

	// ******************************************** Constructors ******************************************************

	public Request(IConnection conn, Handler handler, boolean responseWhenDone) {
		super();
		this.id = nextId++;
		this.conn = conn;
		this.handler = handler;
		this.setRuntime(100);
		this.params = new LinkedList<Byte>();
		this.setCommand((char) 0);
		this.responseWhenDone = responseWhenDone;
	}

	public Request(IConnection conn, Handler handler) {
		this(conn, handler, true);
	}

	// ********************************************* Properties *******************************************************

	public int getId(){
		return this.id;
	}
	
	/**
	 * @return the command
	 */
	public char getCommand() {
		return command;
	}

	/**
	 * @param command
	 *            the command to set
	 */
	public void setCommand(char command) {
		this.command = command;
	}

	/**
	 * @return the runtime
	 */
	public long getRuntime() {
		return runtime;
	}

	/**
	 * @param runtime
	 *            the runtime to set
	 */
	public void setRuntime(long runtime) {
		this.runtime = runtime;
	}

	// *********************************************** Methods ********************************************************

	/**
	 * Adds an parameter to the request
	 * 
	 * @param param
	 *            The parameter to add.
	 */
	public void addParameter(Byte param) {
		this.params.add(param);
	}

	@Override
	public void run() {

		send();

		if (responseWhenDone) {
			sentResponse();
		}
	}

	private void sentResponse() {
		Log.d(LOG_TAG, String.format("Request done %s", getCommand()));
		Message doneMsg = handler.obtainMessage(MessageTypes.REQUEST_EVENT, MessageTypes.REQUEST_DONE, id);
		handler.sendMessageDelayed(doneMsg, getRuntime());
	};

	// Writes the request to the connection
	private void send() {
		logRequest();
		this.conn.write(getData());
	}

	// Logs the request
	private void logRequest() {
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("Request %d: ", id));
		sb.append("Sending: ");
		sb.append(command);

		for (Byte b : params) {
			sb.append(String.format(" 0x%02X", b));
		}

		sb.append("\r\n");

		Log.d(LOG_TAG, sb.toString());
	}

	// Returns a byte array representing the request
	private byte[] getData() {
		int len = COMMAND_LENGTH + this.params.size() + SUFFIX_LENGTH;
		byte[] data = new byte[len];

		data[0] = (byte) command;

		for (int i = 1; i <= params.size(); i++) {
			data[i] = params.get(i - 1);
		}

		data[len - 2] = '\r';
		data[len - 1] = '\n';

		return data;
	}
}