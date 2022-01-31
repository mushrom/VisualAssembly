// *****************************************************************************
// Major Programming Assignment (Part 4)
// 
// *****************************************************************************

import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.geometry.Point2D;

public class Instruction extends Group {
	private String operation = "nop";
	private Operand[] operands = new Operand[2];

	// XXX: stand-in until operand blocks are implemented
	public TextField op1;
	public TextField op2;

	static final int WIDTH  = 270;
	static final int HEIGHT = 36;

	// TODO: better names/array
	private Rectangle recta;
	private Rectangle rectb;
	private Rectangle rectc;

	public Instruction(String op) {
		operation = op;
		recta = new Rectangle(WIDTH - HEIGHT/2, HEIGHT/2);
		rectb = new Rectangle(WIDTH,            HEIGHT/2);
		rectc = new Rectangle(HEIGHT/2,         HEIGHT/2);

		recta.setTranslateX(HEIGHT/2);
		rectb.setTranslateY(HEIGHT/2);
		rectc.setTranslateY(HEIGHT);

		Text text = new Text(operation);
		text.setTranslateX(30);
		text.setTranslateY(16);

		text.setFont(Font.font("monospace", 11));

		op1 = new TextField();
		op2 = new TextField();

		op1.setTranslateX(130);
		op2.setTranslateX(200);
		op1.setTranslateY(3);
		op2.setTranslateY(3);
		op1.setPrefColumnCount(3);
		op2.setPrefColumnCount(3);

		operands[0] = new EmptyOperand();
		operands[1] = new EmptyOperand();

		operands[0].getShape().setTranslateX(130);
		operands[1].getShape().setTranslateX(200);
		operands[0].getShape().setTranslateY(3);
		operands[1].getShape().setTranslateY(3);

		setColor(Color.ORANGE);
		getChildren().addAll(recta, rectb, rectc);
		getChildren().addAll(text);
		getChildren().addAll(operands[0].getShape(), operands[1].getShape());
	}

	public boolean addOperand(Operand op) {
		double x = op.getShape().getTranslateX();
		double y = op.getShape().getTranslateY();

		Point2D opp  = op.getShape().localToScreen(0, 0);
		double op1x = localToScreen(130, 3).getX();
		double op2x = localToScreen(200, 3).getX();
		double opy  = localToScreen(100, 3).getY();

		double d1x = (opp.getX() - op1x);
		double d2x = (opp.getX() - op2x);
		double dy  = (opp.getY() - opy);

		if (Math.sqrt(d1x*d1x + dy*dy) < 30) {
			getChildren().remove(operands[0]);
			getChildren().add(op.getShape());

			op.getShape().setTranslateX(130);
			op.getShape().setTranslateY(3);
			operands[0] = op;

			return true;
		}

		else if (Math.sqrt(d2x*d2x + dy*dy) < 30) {
			getChildren().remove(operands[1]);
			getChildren().add(op.getShape());

			op.getShape().setTranslateX(200);
			op.getShape().setTranslateY(3);
			operands[1] = op;

			return true;
		}

		return false;
	}

	public void setColor(Color color) {
		recta.setFill(color);
		rectb.setFill(color);
		rectc.setFill(color);
	}

	public void setOperand(int idx, Operand op) {
		getChildren().remove(operands[idx]);
		getChildren().add(op.getShape());
		operands[idx] = op;

		op.getShape().setTranslateX(130 + 70*idx);
		op.getShape().setTranslateY(3);
	}

	public String getOperation() {
		return operation;
	}

	public Operand getOperand(int n) {
		return operands[n];
	}

	public Operand[] getOperands() {
		return operands;
	}
}

