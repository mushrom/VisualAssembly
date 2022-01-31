// *****************************************************************************
// Major Programming Assignment (Part 4)
// 
// *****************************************************************************

import javafx.scene.layout.Pane;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.animation.Timeline;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollBar;
import javafx.beans.property.DoubleProperty;
import javafx.util.Duration;

class VMInterface extends Pane {
	private VirtualMachine vm;
	private Timeline animation;

	// for console input/output, accessible from the
	// VMConsolePort inner class defined here
	private TextArea vmOutput = new TextArea();
	private TextField vmInput = new TextField();
	private int vmInputAvailable = 0;

	public VMInterface(VirtualMachine newvm) {
		vm = newvm;
		VBox vert = new VBox();
		HBox controls = new HBox();

		// initialize emulation play/pause/reset buttons
		Button pause = new Button("Pause");
		Button play  = new Button("Continue");
		Button reset = new Button("Reset");

		play.setOnAction(e  -> vm.setState(VirtualMachine.State.RUNNING));
		pause.setOnAction(e -> vm.setState(VirtualMachine.State.PAUSED));
		reset.setOnAction(e -> vm.reset());

		controls.getChildren().addAll(pause, play, reset);
		vert.getChildren().addAll(controls);
		getChildren().addAll(vert);

		// initialize status labels
		Label status = new Label("<not set>");
		Label curProc = new Label("<not set>");
		Label[] registerLabels = new Label[vm.registers.length];
		vert.getChildren().addAll(status, curProc);

		// initialize register labels
		for (int i = 0; i < registerLabels.length; i++) {
			registerLabels[i] = new Label("Testing");
			vert.getChildren().add(registerLabels[i]);
		}

		// initialize animation
		animation = new Timeline(new KeyFrame(Duration.millis(200), e -> {
			if (vm.getState() == VirtualMachine.State.RUNNING) {
				vm.step();
			}

			status.setText(vm.getState().toString());
			if (vm.getCurrentProcedure() != null) {
				curProc.setText("Current procedure: " + vm.getCurrentProcedure().getName());
			}

			for (int i = 0; i < vm.registers.length; i++) {
				Integer value = vm.registers[i];
				registerLabels[i].setText("register " + i + ": " + value.toString());
			}
		}));

		animation.setCycleCount(Timeline.INDEFINITE);
		animation.play();

		// initialize emulation speed control scrollbar
		ScrollBar sbSpeed = new ScrollBar();
		sbSpeed.setMax(10);
		sbSpeed.setValue(1);
		rateProperty().bind(sbSpeed.valueProperty());
		vert.getChildren().add(sbSpeed);

		// initialize VM output TextArea
		vmOutput.setText("");
		vmOutput.setEditable(false);
		vmOutput.setPromptText("<VM console output>");
		vmOutput.setPrefColumnCount(20);
		vert.getChildren().add(vmOutput);

		vmInput.setText("");
		vmInput.setPromptText("<VM console input>");
		vmInput.setPrefColumnCount(20);
		vmInput.setOnAction(e -> { vmInputAvailable = vmInput.getText().length(); });
		vert.getChildren().add(vmInput);

		newvm.setConsolePort(new VMConsolePort());
	}

	private class VMConsolePort extends Port {
		public VMConsolePort() {
			super("console".hashCode());
		}

		/** Reads a single byte from the port. */
		public int readByte() {
			int ret = -1;

			if (hasAvailable()) {
				String text = vmInput.getText();

				ret = text.charAt(0);
				vmInput.setText(text.substring(1));
				vmInputAvailable--;
			}

			return ret;
		}

		/** Writes a single byte to the port. */
		public void writeByte(int b) {
			vmOutput.setText(vmOutput.getText() + (char)b);
			// scroll to the bottom
			vmOutput.setScrollTop(Double.MAX_VALUE);
		}

		/** Returns true if there is data available to be read from the port. */
		public boolean hasAvailable() {
			return vmInputAvailable > 0;
		}

		/** Close the port. For the virtual console, this means clearing the
		 *  output and input.
		 */
		public void close() {
			vmOutput.setText("");
			vmInput.setText("");
			vmInputAvailable = 0;
		}
	}

	public void play() {
		animation.play();
	}

	public void pause() {
		animation.pause();
	}

	public void increaseSpeed() {
		animation.setRate(animation.getRate() + 0.1);
	}

	public void decreaseSpeed() {
		animation.setRate((animation.getRate() > 0)? animation.getRate() - 0.1 : 0);
	}

	public DoubleProperty rateProperty() {
		return animation.rateProperty();
	}
}
