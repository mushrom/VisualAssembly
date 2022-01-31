// *****************************************************************************
// Major Programming Assignment (Part 4)
// 
// *****************************************************************************

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

// Binds an integer handle to a set of byte read/write methods,
// implements a compareTo so ports can be placed in an AVL tree.
public class Port implements Comparable<Port> {
	private int portHandle = -1;

	public Port(int handle) {
		portHandle = handle;
	}

	/** Reads a single byte from the port. */
	public int readByte() {
		return 0;
	}

	/** Writes a single byte to the port. */
	public void writeByte(int b) { }

	/** Returns true if there is data available to be read from the port. */
	public boolean hasAvailable() { return false; }

	/** Close the port, flushing output and invalidating further uses of it. */
	public void close() { }

	/** Compares the handle of this port to the handle of another. */
	public int compareTo(Port other) {
		return this.portHandle - other.portHandle;
	}

	/** Returns a string with information about the type of port this is. */
	public String toString() {
		return "Port:" + portHandle;
	}
}

/** Wraps java InputStream as a Port for the virtual machine. */
class InputStreamPort extends Port {
	private InputStream stream = null;

	public InputStreamPort(int handle) {
		super(handle);
	}

	/** Sets the input stream for this port. */
	public void setStream(InputStream stream) {
		this.stream = stream;
	}

	@Override
	public int readByte() {
		if (stream == null) {
			return -1;

		} else {
			try {
				return stream.read();

			} catch (IOException ex) {
				return -1;
			}
		}
	}

	@Override
	public void writeByte(int b) { }

	@Override
	public boolean hasAvailable() {
		try {
			return stream.available() > 0;

		} catch (IOException ex) {
			return false;
		}
	}

	@Override
	public void close() {
		try {
			stream.close();

		} catch (IOException ex) {
			System.err.println(ex.getMessage());
		}
	}
}

/** Wraps java OutputStream as a Port for the virtual machine. */
class OutputStreamPort extends Port {
	private OutputStream stream = null;

	public OutputStreamPort(int handle) {
		super(handle);
	}

	/** Sets the output stream for this port. */
	public void setStream(OutputStream stream) {
		this.stream = stream;
	}

	@Override
	public int readByte() {
		return -1;
	}

	@Override
	public void writeByte(int b) {
		if (stream != null) {
			try {
				stream.write(b);

			} catch (IOException ex) { }
		}
	}

	@Override
	public boolean hasAvailable() {
		return true;
	}

	@Override
	public void close() {
		try {
			stream.close();

		} catch (IOException ex) {
			System.err.println(ex.getMessage());
		}
	}
}

/** Building off the InputStreamPort class, this wraps java FileInputStreams
 *  as ports for the virtual machine.
 */
class FileInputPort extends InputStreamPort {
	public FileInputPort(int handle, String filename) {
		super(handle);

		try {
			System.out.printf("PORT (file): opening input: %s\r\n", filename);
			InputStream stream = new FileInputStream(new File(filename));
			setStream(stream);

		} catch (FileNotFoundException ex) {
			// TODO: show error to the user somehow
		}
	}
}

/** Building off the OutputStreamPort class, this wraps java FileOutputStream
 *  as ports for the virtual machine.
 */
class FileOutputPort extends OutputStreamPort {
	public FileOutputPort(int handle, String filename) {
		super(handle);

		try {
			System.out.printf("PORT (file): opening output: %s\r\n", filename);
			OutputStream stream = new FileOutputStream(new File(filename));
			setStream(stream);

		} catch (FileNotFoundException ex) {
			// TODO: show error to the user somehow
		}
	}
}
