// *****************************************************************************
// Major Programming Assignment (Part 4)
// 
// *****************************************************************************

class PortMap {
	private AVLTree<Port> entries = new AVLTree<>();

	public PortMap() { }

	/** Add a port to the port map. */
	public void add(Port elem) {
		entries.add(elem);
	}

	/** Find an instance of a port in the map. */
	public Port find(int handle) {
		Port p = new Port(handle);
		Port temp = entries.findElem(p);
		return temp;
	}

	/** Remove all ports from the map */
	public void clear() {
		for (Port port : entries) {
			port.close();
		}

		entries = new AVLTree<>();
	}

	// TODO: Invalidate and remove a single port
}

