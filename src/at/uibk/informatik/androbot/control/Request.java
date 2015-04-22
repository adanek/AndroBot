package at.uibk.informatik.androbot.control.requests;

import java.util.LinkedList;
import java.util.List;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import at.uibk.informatik.androbot.contracts.IConnection;
import at.uibk.informatik.androbot.contracts.IRequest;

/**
 * Encapsulates one command which will sent to the remote device in an own thread.
 */
public class Request implements IRequest {

	private static final int SUFFIX_LENGTH = 2;
	private static final int COMMAND_LENGTH = 1;

	// ********************************************** Locals **********************************************************

	private static final String LOG_TAG = "Request";

	private IConnection conn;
	private Handler handler;
	private long runtime;
	private List<Byte> params;
	private char command;
	private boolean confirm;

	// ******************************************** Constructors ******************************************************

	public Request(IConnection conn, Handler handler, boolean confirm) {
		super();
		this.conn = conn;
		this.handler = handler;
		this.setRuntime(100);
		this.params = new LinkedList<Byte>();
		this.setCommand((char) 0);
		this.confirm = confirm;
	}

	public Request(IConnection conn, Handler handler) {
		this(conn, handler, true);
	}

	// ********************************************* Properties *******************************************************

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

		if (confirm) {
			Log.d(LOG_TAG, String.format("Request done %s", getCommand()));
			Message doneMsg = handler.obtainMessage(REQUEST_EVENT, REQUEST_SENT, -1);
			handler.sendMessageDelayed(doneMsg, getRuntime());
		}
	};

	// Writes the request to the connection
	private void send() {
		logRequest();
		this.conn.write(getData());
	}

	// Logs the request
	private void logRequest() {
		StringBuilder sb = new StringBuilder();

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