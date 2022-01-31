// *****************************************************************************
// Major Programming Assignment (Part 4)
// 
// *****************************************************************************

import javafx.scene.layout.Pane;
import javafx.scene.control.ScrollPane;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.geometry.Point2D;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

class InstructionPane extends ScrollPane {
	// free-floating instructions unbound to a definition
	private Group instructions = new Group();
	// free-floating operations unbound to an instruction
	private Group operands = new Group();
	// lists of instructions bound to a definition
	private Group definitions = new Group();
	private VirtualMachine vm;

	public InstructionPane(VirtualMachine newvm) {
		Group toplevel = new Group();
		toplevel.getChildren().add(instructions);
		toplevel.getChildren().add(definitions);
		toplevel.getChildren().add(operands);
		setContent(toplevel);

		vm = newvm;

		// add entrypoint for the program
		addDefinition(new Definition("start"));
		reloadDefinitions();
	}

	private void reloadDefinitions() {
		MyArrayList<Definition> defs = new MyArrayList<>();

		for (Node defnode : definitions.getChildren()) {
			// all children of definitions should be instances of
			// Definition, so this cast should never fail
			defs.add((Definition)defnode);
		}

		try {
			vm.reloadDefinitions(defs);
		} catch (Exception e){
			System.out.println("Couldn't reload definitions! " + e);
		}
	}

	public void addInstruction(Instruction instruction) {
		instructions.getChildren().add(instruction);

		// TODO: find a reasonable place to put the new instruction
		//       so that it doesn't overlap with others, keep
		//       track of free space on screen
		instruction.setTranslateX(100);
		instruction.setTranslateY(100);

		// Set handler for dragging mouse,
		// moves instruction with mouse
		instruction.setOnMouseDragged(e -> {
			instruction.setTranslateX(instruction.getTranslateX() + e.getX() - 100);
			instruction.setTranslateY(instruction.getTranslateY() + e.getY() - 30);
		});

		// when the mouse is released after dragging an instruction,
		// check to see if it's near the bottom of a definition, and if so,
		// add the instruction to that definition
		//
		// TODO: Possibly move into a seperate event handler
		instruction.setOnMouseReleased(e -> {
			for (Node node : definitions.getChildren()) {
				Definition  def  = (Definition)node;
				Instruction inst = instruction;

				Point2D instPos = inst.localToScreen(0,0);
				Point2D defPos  = def.localToScreen(0,0);

				// euclidean distance calculation
				double x = instPos.getX() - defPos.getX();
				double y = instPos.getY()
				           - (defPos.getY()
				                 + def.numInstructions()*Instruction.HEIGHT
				                 + Definition.HEIGHT);

				double dist = Math.sqrt(x*x + y*y);

				// if the instruction is within a certain radius of the bottom
				// left corner of this definition, add it to the definition.
				if (dist < 30) {
					def.addInstruction(inst);
					instructions.getChildren().remove(inst);
				}
			}
		});
	}

	public void addOperand(Operand operand) {
		operands.getChildren().add(operand.getShape());

		// TODO: find a reasonable place to put the new instruction
		//       so that it doesn't overlap with others, keep
		//       track of free space on screen
		operand.getShape().setTranslateX(100);
		operand.getShape().setTranslateY(100);

		// Set handler for dragging mouse,
		// moves instruction with mouse
		operand.getShape().setOnMouseDragged(e -> {
			double x = operand.getShape().getTranslateX();
			double y = operand.getShape().getTranslateY();

			operand.getShape().setTranslateX(x + e.getX() - 20);
			operand.getShape().setTranslateY(y + e.getY() - 20);
		});

		// when the mouse is released after dragging an instruction,
		// check to see if it's near the bottom of a definition, and if so,
		// add the instruction to that definition
		//
		// TODO: Possibly move into a seperate event handler
		operand.getShape().setOnMouseReleased(e -> {
			outerloop:
			for (Node node : definitions.getChildren()) {
				Definition  def  = (Definition)node;
				
				for (Node instnode : def.getInstructions().getChildren()) {
					Instruction inst = (Instruction)instnode;

					if (inst.addOperand(operand)) {
						System.out.println("Got here");
						operands.getChildren().remove(operand.getShape());
						operand.getShape().setOnMouseDragged(ex -> {});
						operand.getShape().setOnMouseReleased(ex -> {});

						// break out of all loops, operand has been placed
						break outerloop;
					}
				}
			}

			return;
		});
	}

	/*
	// TODO: Implement operand blocks
	public void addOperand(Operand operand) {

	}
	*/

	public void addDefinition(Definition def) {
		definitions.getChildren().add(def);

		// TODO: find a reasonable place to put the new definition
		//       so that it doesn't overlap with others, keep
		//       track of free space on screen
		def.setTranslateX(100);
		def.setTranslateY(100);

		// When the mouse is dragged, move the definition along with the mouse
		def.setOnMouseDragged(e -> {
			def.setTranslateX(def.getTranslateX() + e.getX() - 100);
			def.setTranslateY(def.getTranslateY() + e.getY() - 30);
		});

		reloadDefinitions();
	}

	// write current definitions into the given file
	// NOTE: this only saves definitions, instructions not attached
	//       to a definition won't be saved.
	public void saveState(File outfile)
		throws java.io.FileNotFoundException
	{
		PrintWriter writer = new PrintWriter(outfile);

		for (Node defnode : definitions.getChildren()) {
			Definition def = (Definition)defnode;

			writer.printf("define %s %g,%g\n",
			              def.getName(),
			              def.getTranslateX(),
			              def.getTranslateY());
			
			for (Node instnode : def.getInstructions().getChildren()) {
				Instruction inst = (Instruction)instnode;

				writer.printf("\t%s %s %s\n",
				              inst.getOperation(),
				              inst.getOperand(0).toString(),
				              inst.getOperand(1).toString());
			}
		}

		writer.close();
	}

	public void clearState() {
		definitions.getChildren().clear();
		instructions.getChildren().clear();
		operands.getChildren().clear();
		vm.setState(VirtualMachine.State.PAUSED);
	}

	// clear current state and load new state from assembly in the given file.
	public void loadState(File infile)
		throws java.io.FileNotFoundException
	{
		Scanner scan = new Scanner(infile);
		Definition currentDefinition = null;

		clearState();

		while (scan.hasNextLine()) {
			String line = scan.nextLine().trim();
			String[] args = line.split(" ");

			if (args[0].equals("define")) {
				currentDefinition = new Definition(args[1]); 
				addDefinition(currentDefinition);

				// Parse window coordinates, if present.
				//
				// Optional as the assembly may be written by hand, and it
				// would be a pain to need to specify window coordinates
				// manually.
				//
				// TODO: if window coordinates aren't present, place the definition
				//       somewhere reasonable where it doesn't overlap with other
				//       definitions
				if (args.length >= 3) {
					String[] coords = args[2].split(",");

					if (coords.length == 2) {
						currentDefinition.setTranslateX(Double.parseDouble(coords[0]));
						currentDefinition.setTranslateY(Double.parseDouble(coords[1]));
					}
				}

			} else if (currentDefinition != null) {
				Instruction inst = new Instruction(args[0]);

				// TODO: parse into Operand instances
				//if (args.length > 1) inst.op1.setText(args[1]);
				//if (args.length > 2) inst.op2.setText(args[2]);

				Operand[] operands = inst.getOperands();
				if (args.length > 1) inst.setOperand(0, OperandFactory.operandFromString(args[1]));
				if (args.length > 2) inst.setOperand(1, OperandFactory.operandFromString(args[2]));

				currentDefinition.addInstruction(inst);
			}
		}
	}
}
