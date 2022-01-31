import java.net.*;
import java.io.*;

public class NetworkPort extends Port {
	private Socket socket = null;
	private InputStream input = null;
	private OutputStream output = null;

	/** Create a new port, attempting to connect to the specified host.
	 *  The host string is expected to be in the format "hostname:port".
	 */
	public NetworkPort(int handle, String host) {
		super(handle);

		try {
			String[] args = host.split(":");
			System.out.printf("Opening connection to %s, port %s\r\n",
							  args[0], args[1]);
			socket = new Socket(args[0], Integer.parseInt(args[1]));
			input  = socket.getInputStream();
			output = socket.getOutputStream();

		} catch (IOException ex) {
			ex.printStackTrace();
			socket = null;
			input = null;
			output = null;
		}
	}

	@Override
	public int readByte() {
		try {
			if (hasAvailable()) {
				return input.read();
			}

		} catch (IOException ex) {
			System.err.println(ex.getMessage());
			return -1;
		}

		return -1;
	}

	@Override
	public boolean hasAvailable() {
		try {
			return input != null && input.available() > 0;

		} catch (IOException ex) {
			return false;
		}
	}

	@Override
	public void writeByte(int b) {
		try {
			if (output != null) {
				output.write(b);
			}

		} catch (IOException ex) {
			System.err.println(ex.getMessage());
			return;
		}
	}

	@Override
	public void close() {
		try {
			if (input  != null) input.close();
			if (output != null) output.close();
			if (socket != null) socket.close();

		} catch (IOException ex) {
			System.err.println(ex.getMessage());
		}
	}
}
