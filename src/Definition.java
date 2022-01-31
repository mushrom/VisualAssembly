// *****************************************************************************
// Major Programming Assignment (Part 4)
// 
// *****************************************************************************

import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

public class Definition extends Group {
	private String name;
	private Group instructions;

	static final int HEIGHT = 30;

	public Definition(String newname) {
		name = newname;
		instructions = new Group();

		Rectangle recta = new Rectangle(200, HEIGHT);
		Rectangle rectb = new Rectangle(18, 18);
		Text text = new Text("Definition: " + newname);

		recta.setFill(Color.LIGHTBLUE);
		rectb.setFill(Color.LIGHTBLUE);
		rectb.setTranslateY(HEIGHT);

		text.setTranslateX(HEIGHT);
		text.setTranslateY(16);

		getChildren().addAll(recta, rectb);
		getChildren().addAll(text);
		getChildren().addAll(instructions);
	}

	public String getName() {
		return name;
	}

	public void addInstruction(Instruction in) {
		in.setTranslateX(0);
		in.setTranslateY(numInstructions()*Instruction.HEIGHT + HEIGHT);
		in.setOnMouseClicked(e -> {
			instructions.getChildren().remove(in);

			// update positions of remaining instructions after deletion 
			for (int i = 0; i < numInstructions(); i++) {
				Instruction temp = getInstruction(i);
				temp.setTranslateY(i * Instruction.HEIGHT + HEIGHT);
			}
		});

		in.setOnMouseDragged(e -> {});

		instructions.getChildren().add(in);
	}

	public Group getInstructions() {
		return instructions;
	}

	public int numInstructions() {
		return instructions.getChildren().size();
	}

	public Instruction getInstruction(int n) {
		return (Instruction)instructions.getChildren().get(n);
	}
}
