// *****************************************************************************
// Major Programming Assignment (Part 4)
// 
// *****************************************************************************

import java.util.Stack;

import javafx.scene.paint.Color;

class VirtualMachine {
	public static enum State {
		// currently running, can transition to any other state
		RUNNING,
		// paused, can resume execution
		PAUSED,
		// successfully reached end of execution, needs reset
		HALTED,
		// encountered a fatal error during execution, needs reset
		ERROR,
	};

	// registers 0-7 (inclusive): general purpose
	// register  8: instruction pointer
	// register  9: stack pointer
	// register 10: stack base pointer
	// register 11: machine state flags
	//              bit 0: integer overflow
	//              bit 1: division by zero
	//              bit 2: non-fatal error
	//              bit 3: fatal error (emulation stopped)
	//              TODO: more bits
	public int[] registers = new int[12];
	public int[] memory    = new int[16384];

	private static final int IP = 8;
	private static final int SP = 9;
	private static final int BP = 10;
	private static final int EP = 11;

	private State currentState = State.PAUSED;
	private Instruction lastInstruction = null;

	// map string hash codes to procedures
	// NOTE: using hash codes rather than strings themselves so that references
	//       to procedures can be passed around as values in the assembly code
	private MyMap<Integer, Definition> procedures = new MyHashMapLinear<>();
	private PortMap ports = new PortMap();
	private Port consolePort = null;

	private Stack<Definition> procedureStack = new Stack<>();
	private Stack<Integer> instructionPointerStack = new Stack<>();
	private Definition currentProcedure = null;

	public VirtualMachine() {

	}

	public Definition getCurrentProcedure() {
		return currentProcedure;
	}

	public void setConsolePort(Port p) {
		consolePort = p;
		ports.add(p);
	}

	public State getState() {
		return currentState;
	}

	public void setState(State state) {
		currentState = state;
	}

	public void reloadDefinitions(MyList<Definition> definitions) throws Exception {
		System.out.println("New definitions");
		procedures = new MyHashMapLinear<>();

		for (Definition def : definitions) {
			procedures.put(def.getName().hashCode(), def);
			System.out.printf("Added definition: %s -> %d\n", def.getName(), def.getName().hashCode());
		}

		if (!procedures.containsKey("start".hashCode())) {
			throw new Exception("No start procedure");
		}

		currentProcedure = procedures.get("start".hashCode());
		if (currentProcedure == null){
			throw new Exception("No start procedure(?)");
		}
	}

	public void reset() {
		for (int i = 0; i < registers.length; i++) {
			registers[i] = 0;
		}

		ports.clear();
		procedureStack.clear();
		instructionPointerStack.clear();
		currentState = State.PAUSED;
		currentProcedure = procedures.get("start".hashCode());

		if (consolePort != null) {
			ports.add(consolePort);
		}
	}

	public void step() {
		if (currentState == State.ERROR) {
			// machine is in unrecoverable error state, can't continue
			return;
		}

		if (registers[IP] < 0 || registers[IP] >= currentProcedure.numInstructions()) {
			// invalid instruction index, fatal error
			currentState = State.ERROR;
			return;
		}

		Instruction inst = currentProcedure.getInstruction(registers[IP]);
		inst.setColor(Color.DARKORANGE);

		if (lastInstruction != null && lastInstruction != inst) {
			// reset the color of the previous instruction node
			lastInstruction.setColor(Color.ORANGE);
		}
		lastInstruction = inst;

		// Scratch registers for instructions
		// (can't redefine variables inside of the switch block,
		//  so need to reuse these variables)
		int a;
		int b;

		switch (inst.getOperation()) {
			case "halt":
				currentState = State.HALTED;
				// don't increase instruction pointer, stays on 'halt' instruction
				break;

			case "call":
				int code = inst.getOperand(0).getValue(this);
				Definition proc = procedures.get(code);

				if (!procedures.containsKey(code)) {
					System.out.println("Undefined procedure in call: " + code);
					// call to undefined procedure, fatal error
					currentState = State.ERROR;

				} else {
					instructionPointerStack.push(registers[IP]);
					procedureStack.push(currentProcedure);
					currentProcedure = proc;
					registers[IP] = 0;
				}

				break;

			case "return":
				if (instructionPointerStack.isEmpty() || procedureStack.isEmpty()) {
					// TODO:
				}

				currentProcedure = procedureStack.pop();
				// +1 as we need to return to the instruction following the 'call' instruction
				registers[IP] = instructionPointerStack.pop() + 1;
				break;

			case "move":
				a = inst.getOperand(0).getValue(this);
				inst.getOperand(1).setValue(this, a);
				registers[IP] += 1;
				break;

			case "add":
				a = inst.getOperand(0).getValue(this);
				b = inst.getOperand(1).getValue(this);

				// using register 0 as accumulator,
				// place result in r0
				registers[0] = a + b;
				registers[IP] += 1;
				break;

			case "subtract":
				a = inst.getOperand(0).getValue(this);
				b = inst.getOperand(1).getValue(this);

				// place result in r0
				registers[0] = a - b;
				registers[IP] += 1;
				break;

			case "multiply":
				a = inst.getOperand(0).getValue(this);
				b = inst.getOperand(1).getValue(this);

				// place result in r0
				registers[0] = a * b;
				registers[IP] += 1;
				break;

			case "divide":
				a = inst.getOperand(0).getValue(this);
				b = inst.getOperand(1).getValue(this);

				if (b == 0) {
					// division by zero, fatal error
					currentState = State.ERROR;

				} else {
					// place result in r0
					registers[0] = a / b;
					registers[IP] += 1;
				}
				break;

			case "shift-left":
				a = inst.getOperand(0).getValue(this);
				b = inst.getOperand(1).getValue(this);

				// place result in r0
				registers[0] = a << b;
				registers[IP] += 1;
				break;

			case "shift-right":
				a = inst.getOperand(0).getValue(this);
				b = inst.getOperand(1).getValue(this);

				// place result in r0
				registers[0] = a >> b;
				registers[IP] += 1;
				break;

			case "bit-and":
				a = inst.getOperand(0).getValue(this);
				b = inst.getOperand(1).getValue(this);

				// place result in r0
				registers[0] = a & b;
				registers[IP] += 1;
				break;

			case "bit-or":
				a = inst.getOperand(0).getValue(this);
				b = inst.getOperand(1).getValue(this);

				// place result in r0
				registers[0] = a | b;
				registers[IP] += 1;
				break;

			case "bit-xor":
				a = inst.getOperand(0).getValue(this);
				b = inst.getOperand(1).getValue(this);

				// place result in r0
				registers[0] = a ^ b;
				registers[IP] += 1;
				break;

			case "bit-negate":
				a = inst.getOperand(0).getValue(this);

				// place result in r0
				registers[0] = ~a;
				registers[IP] += 1;
				break;

			case "jump":
				a = inst.getOperand(0).getValue(this);
				registers[IP] += a;
				break;

			case "jump-zero":
				a = inst.getOperand(0).getValue(this);
				b = inst.getOperand(1).getValue(this);

				if (b == 0) {
					registers[IP] += a;
				} else {
					registers[IP] += 1;
				}
				break;

			case "jump-not-zero":
				a = inst.getOperand(0).getValue(this);
				b = inst.getOperand(1).getValue(this);

				if (b != 0) {
					registers[IP] += a;
				} else {
					registers[IP] += 1;
				}
				break;

			case "jump-gt-zero":
				a = inst.getOperand(0).getValue(this);
				b = inst.getOperand(1).getValue(this);

				if (b > 0) {
					registers[IP] += a;
				} else {
					registers[IP] += 1;
				}
				break;

			case "jump-lt-zero":
				a = inst.getOperand(0).getValue(this);
				b = inst.getOperand(1).getValue(this);

				if (b < 0) {
					registers[IP] += a;
				} else {
					registers[IP] += 1;
				}
				break;

			case "push":
				a = inst.getOperand(0).getValue(this);
				memory[registers[SP]] = a;
				registers[SP] += 1;
				registers[IP] += 1;
				break;

			case "pop":
				registers[SP] -= 1;
				registers[IP] += 1;
				a = memory[registers[SP]];
				inst.getOperand(0).setValue(this, a);
				//setData(inst.op1.getText(), a);
				break;

			case "no-operation":
				registers[IP] += 1;
				break;

			case "print-char":
				{
					Port p = ports.find("console".hashCode());
					a = inst.getOperand(0).getValue(this);
					p.writeByte(a);
					//System.out.print((char)a);

					registers[IP] += 1;
				}
				break;

			case "print-integer":
				{
					Port p = ports.find("console".hashCode());
					String num = "";

					a = inst.getOperand(0).getValue(this);

					do {
						num = (char)((a % 10) + '0') + num;
						a /= 10;
					} while (a != 0);


					for (int i = 0; i < num.length(); i++) {
						p.writeByte(num.charAt(i));
					}

					registers[IP] += 1;
				}
				break;

			case "open-file-output":
				{
					a = inst.getOperand(0).getValue(this);
					b = inst.getOperand(1).getValue(this);
					String foo = "";

					System.out.printf("Have filename pointer: 0x%x\r\n", b);
					for (int i = b; i >= 0 && i < memory.length && memory[i] != 0; i++) {
						System.out.printf("Have filename char: %c\r\n", memory[i]);

						foo += (char)memory[i];
					}

					if (!foo.isEmpty()) {
						ports.add(new FileOutputPort(a, foo));
					} else {
						System.out.printf("NOTE: empty filename...");
					}

					registers[IP] += 1;
					break;
				}

			case "open-client-socket":
				{
					a = inst.getOperand(0).getValue(this);
					b = inst.getOperand(1).getValue(this);
					String foo = "";

					System.out.printf("Have socket pointer: 0x%x\r\n", b);
					for (int i = b; i >= 0 && i < memory.length && memory[i] != 0; i++) {
						System.out.printf("Have socket host char: %c\r\n", memory[i]);

						foo += (char)memory[i];
					}

					if (!foo.isEmpty()) {
						ports.add(new NetworkPort(a, foo));
					} else {
						System.out.printf("NOTE: empty filename...");
					}

					registers[IP] += 1;
					break;
				}

			case "write-port-byte":
				{
					a = inst.getOperand(0).getValue(this);
					b = inst.getOperand(1).getValue(this);
					Port p = ports.find(a);

					if (p != null) {
						p.writeByte(b);

					} else {
						System.out.printf("NOTE: port reference is not valid!\r\n");
					}

					registers[IP] += 1;
					break;
				}

			case "read-port-byte":
				{
					a = inst.getOperand(0).getValue(this);
					Port p = ports.find(a);

					if (p != null) {
						// place result in r0
						registers[0] = p.readByte();

					} else {
						System.out.printf("NOTE: port reference is not valid!\r\n");
					}

					registers[IP] += 1;
					break;
				}

			case "port-has-available":
				{
					a = inst.getOperand(0).getValue(this);
					Port p = ports.find(a);

					if (p != null) {
						// place result in r0
						registers[0] = p.hasAvailable()? 1 : 0;

					} else {
						System.out.printf("NOTE: port reference is not valid!\r\n");
					}

					registers[IP] += 1;
					break;
				}

			case "close-port":
				{
					a = inst.getOperand(0).getValue(this);
					Port p = ports.find(a);

					if (p != null) {
						// no changes to register/memory state
						p.close();

					} else {
						System.out.printf("NOTE: port reference is not valid!\r\n");
					}

					registers[IP] += 1;
					break;
				}

			default:
				System.out.println("NOTE: unimplemented instruction " + inst.getOperation());
				registers[IP] += 1;
				break;
		}
	}
}
