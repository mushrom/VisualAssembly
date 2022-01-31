// *****************************************************************************
// Major Programming Assignment (Part 4)
// 
// *****************************************************************************

import javafx.scene.Node;
import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.control.TextField;

public interface Operand {
	static final int WIDTH  = 60;
	static final int HEIGHT = 30;

	/** Return the node for this operand, which contains all of
	 *  the widgets and shapes for this operand to be drawn on screen.
	 */
	public Node getShape();

	/** Retrieves the value referenced by the operand. */
	public int getValue(VirtualMachine vm);

	/** Sets the value referenced by the operand to the specified value.
	 *
	 *  This isn't supported for all operand types,
	 *  EmptyOperand() and ConstantOperand() can't be set.
	 */
	public void setValue(VirtualMachine vm, int val);

	/** Sets the value reference for this operand.
	 *
	 *  Meant primarily for loading with OperandFactory,
	 *  you probably won't want to use this.
	 */
	public void setTarget(String text);
};

/** Constructs an Operand from the text respresentation
 *  of the operand written to an assembly file.
 */
class OperandFactory {
	public static Operand operandFromString(String txt) {
		if (txt.length() == 0) {
			return new EmptyOperand();
		}
		else if (txt.charAt(0) == '$') {
			ConstantOperand op = new ConstantOperand();
			op.setTarget(txt.substring(1));
			return op;
		}
		else if (txt.charAt(0) == '#') {
			ConstantOperand op = new ConstantOperand();
			op.setTarget(txt);
			return op;
		}
		else if (txt.charAt(0) == 'r') {
			RegisterOperand op = new RegisterOperand();
			op.setTarget(txt);
			return op;
		}
		else if (txt.charAt(0) == '@') {
			MemoryOperand op = new MemoryOperand();
			op.setTarget(txt.substring(1));
			return op;
		}
		else {
			// TODO: show user an error/warning
			return new EmptyOperand();
		}
	}
}

class EmptyOperand implements Operand {
	private Group group = new Group();

	public EmptyOperand() {
		Rectangle rect = new Rectangle(WIDTH, HEIGHT);
		rect.setFill(Color.DARKORANGE);
		group.getChildren().addAll(rect);
	}

	@Override
	public Node getShape() {
		return group;
	}

	@Override
	public int getValue(VirtualMachine vm) {
		return 0;
	}

	@Override
	public void setValue(VirtualMachine vm, int val) {}

	@Override
	public void setTarget(String text) {}

	@Override
	public String toString() {
		// return nothing for empty operands
		return "";
	}
}

class ConstantOperand implements Operand {
	private Group group = new Group();
	private TextField field = new TextField();

	public ConstantOperand() {
		init("");
	}

	public ConstantOperand(String text) {
		init(text);
	}

	// Internal helper function for constructing
	private void init(String data) {
		Rectangle rect = new Rectangle(WIDTH, HEIGHT);
		rect.setFill(Color.RED);
		field.setPrefColumnCount(2);
		field.setTranslateX(16);
		group.getChildren().addAll(rect, field);

		field.setText(data);
	}

	@Override
	public Node getShape() {
		return group;
	}

	@Override
	public int getValue(VirtualMachine vm) {
		String str = field.getText();

		if (str.length() == 0) {
			// no data, return 0
			// (not an error for constants)
			return 0;

		} else if (str.charAt(0) == '#') {
			// hash constant
			return str.substring(1).hashCode();

		} else {
			// otherwise return a plain old constant
			return Integer.parseInt(str);
		}
	}

	@Override
	public void setValue(VirtualMachine vm, int val) {
		// can't write to a constant
		// TODO: should this be an error?
	}

	@Override
	public void setTarget(String text) {
		field.setText(text);
	}

	@Override
	public String toString() {
		String str = field.getText();

		if (str.length() == 0) {
			return "$0";

		} else {
			return "$" + str;
		}
	}
}

class RegisterOperand implements Operand {
	private Group group = new Group();
	private TextField field = new TextField();

	public RegisterOperand() {
		init("");
	}

	public RegisterOperand(String data) {
		init(data);
	}

	private void init(String text) {
		Rectangle rect = new Rectangle(WIDTH, HEIGHT);
		rect.setFill(Color.LIGHTGREEN);
		field.setPrefColumnCount(2);
		field.setTranslateX(16);
		group.getChildren().addAll(rect, field);

		field.setText(text);
	}

	@Override
	public Node getShape() {
		return group;
	}

	@Override
	public int getValue(VirtualMachine vm) {
		String str = field.getText();

		if (str.length() == 0) {
			// no data, error, return 0
			vm.setState(VirtualMachine.State.ERROR);
			return 0;

		} else {
			switch (str) {
				case "r0":  return vm.registers[0];
				case "r1":  return vm.registers[1];
				case "r2":  return vm.registers[2];
				case "r3":  return vm.registers[3];
				case "r4":  return vm.registers[4];
				case "r5":  return vm.registers[5];
				case "r6":  return vm.registers[6];
				case "r7":  return vm.registers[7];
				case "rip": return vm.registers[8];
				case "rsp": return vm.registers[9];
				case "rbp": return vm.registers[10];
				case "ref": return vm.registers[11];

				default:
					// invalid register name (shouldn't happen with combo boxes...)
					vm.setState(VirtualMachine.State.ERROR);
					return 0;
			}
		}
	}

	@Override
	public void setValue(VirtualMachine vm, int val) {
		String str = field.getText();

		if (str.length() == 0) {
			// no data
			vm.setState(VirtualMachine.State.ERROR);

		} else {
			switch (str) {
				case "r0":  vm.registers[0]  = val; break;
				case "r1":  vm.registers[1]  = val; break;
				case "r2":  vm.registers[2]  = val; break;
				case "r3":  vm.registers[3]  = val; break;
				case "r4":  vm.registers[4]  = val; break;
				case "r5":  vm.registers[5]  = val; break;
				case "r6":  vm.registers[6]  = val; break;
				case "r7":  vm.registers[7]  = val; break;
				case "rip": vm.registers[8]  = val; break;
				case "rsp": vm.registers[9]  = val; break;
				case "rbp": vm.registers[10] = val; break;
				case "ref": vm.registers[11] = val; break;

				default:
					// invalid register name (shouldn't happen with combo boxes...)
					vm.setState(VirtualMachine.State.ERROR);
			}
		}
	}

	@Override
	public void setTarget(String text) {
		field.setText(text);
	}

	@Override
	public String toString() {
		return field.getText();
	}
}

class MemoryOperand implements Operand {
	private Group group = new Group();
	private TextField field = new TextField();

	public MemoryOperand() {
		init("");
	}

	public MemoryOperand(String data) {
		init(data);
	}

	private void init(String data) {
		Rectangle rect = new Rectangle(WIDTH, HEIGHT);
		rect.setFill(Color.PURPLE);
		field.setPrefColumnCount(2);
		field.setTranslateX(16);
		group.getChildren().addAll(rect, field);
		field.setText(data);
	}

	@Override
	public Node getShape() {
		return group;
	}

	@Override
	public int getValue(VirtualMachine vm) {
		String str = field.getText();

		if (str.length() == 0) {
			// no data, return 0
			vm.setState(VirtualMachine.State.ERROR);
			return 0;

		} else {
			// TODO: hex addresses (x prefix maybe)
			int addr = Integer.parseInt(str);
			if (addr > vm.memory.length || addr < 0) {
				// invalid address
				vm.setState(VirtualMachine.State.ERROR);
				return 0;
			}

			return vm.memory[addr];
		}
	}

	@Override
	public void setValue(VirtualMachine vm, int val) {
		String str = field.getText();

		if (str.length() == 0) {
			vm.setState(VirtualMachine.State.ERROR);
			// no data

		} else {
			// TODO: hex addresses (x prefix maybe)
			int addr = Integer.parseInt(str);

			if (addr > vm.memory.length || addr < 0) {
				// invalid address
				vm.setState(VirtualMachine.State.ERROR);
				return;
			}

			vm.memory[addr] = val;
		}
	}

	@Override
	public void setTarget(String text) {
		field.setText(text);
	}

	@Override
	public String toString() {
		return "@" + field.getText();
	}
}
