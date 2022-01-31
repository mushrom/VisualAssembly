// *****************************************************************************
// Major Programming Assignment (Part 4)
// 
// *****************************************************************************

import javafx.application.Application;
import javafx.geometry.Insets;

import javafx.scene.Node;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import javafx.stage.FileChooser;

import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import javafx.stage.Stage;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Stack;
import java.util.Arrays;

import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import javafx.scene.Parent;
import java.net.URL;

public class VisualAssembly extends Application {
	public static void main(String[] args) { launch(); }

	@Override
	public void start(Stage primaryStage) {
		// initialize top-level layout
		VirtualMachine vm = new VirtualMachine();
		InstructionPane instPane = new InstructionPane(vm);
		VBox definitions = new VBox();
		MenuBar menuBar = new MenuBar();

		BorderPane pane = new BorderPane();
		VMInterface vmPane = new VMInterface(vm);
		ScrollPane content = new ScrollPane();
		ScrollPane buttons = new ScrollPane();

		content.setContent(instPane);
		buttons.setContent(definitions);

		pane.setCenter(instPane);
		pane.setLeft(buttons);
		pane.setRight(vmPane);
		pane.setTop(menuBar);

		Scene scene = new Scene(pane, 1280, 720);
		primaryStage.setTitle("Visual Assembly - CMIS202");
		primaryStage.setScene(scene);
		primaryStage.show();

		// Initialize menubar
		Menu fileMenu = new Menu("File");
		Menu aboutMenu = new Menu("About");

		MenuItem newitem    = new MenuItem("New");
		MenuItem openitem   = new MenuItem("Open");
		MenuItem saveasitem = new MenuItem("Save As");
		MenuItem closeitem  = new MenuItem("Close");
		fileMenu.getItems().addAll(newitem, openitem, saveasitem, closeitem);

		MenuItem aboutManual = new MenuItem("Manual");
		aboutMenu.getItems().addAll(aboutManual);

		// create a new program by clearing all state and adding
		// a fresh 'start' procedure
		newitem.setOnAction(e -> {
			instPane.clearState();
			instPane.addDefinition(new Definition("start"));
			vm.reset();
		});

		aboutManual.setOnAction(e -> {
			// when the user clicks this, open a web view
			// to render the HTML documentation
			WebView view = new WebView();
			String htmlurl = getClass().getResource("Manual.html").toExternalForm();
			view.getEngine().load(htmlurl);

			Parent root = view;
			Stage webstage = new Stage();
			webstage.setTitle("VisualAssembly manual");
			webstage.setScene(new Scene(root, 1280, 720));
			webstage.show();
		});

		openitem.setOnAction(new OpenHandler<ActionEvent>(instPane, primaryStage));
		// TODO: save handler, keep track of whether the current state
		//       has already been saved, prompt for a name if not,
		//       save to the previous file name if so
		saveasitem.setOnAction(new SaveHandler<ActionEvent>(instPane, primaryStage));
		closeitem.setOnAction(new CloseHandler<ActionEvent>(instPane, primaryStage));

		menuBar.getMenus().addAll(fileMenu, aboutMenu);

		// Initialize left definition panel
		Text builtins = new Text("Built-in blocks");
		Text operandTitle = new Text("Operands");
		Text userdefined = new Text("User-defined blocks");
		definitions.setPadding(new Insets(10));
		definitions.setSpacing(8);

		// Add built-in instruction blocks
		String[] builtinNameArray = {
			// data movement
			"push",
			"pop",
			"move",

			// control flow
			"call",
			"jump",
			"jump-zero",
			"jump-not-zero",
			"jump-gt-zero",
			"jump-lt-zero",
			"return",
			"halt",

			// arithmetic
			"add",
			"subtract",
			"multiply",
			"divide",
			"shift-left",
			"shift-right",
			"bit-and",
			"bit-or",
			"bit-negate",
			"bit-xor",

			// misc
			"no-operation",

			// output instructions
			"print-char",
			"print-integer",

			// port manipulation operations
			"open-file-input",
			"open-file-output",
			"open-client-socket", // TODO:
			"read-port-byte",
			"write-port-byte",
			"port-has-available",
			"close-port",
		};

		MyArrayList<String> builtinNames = new MyArrayList<>(builtinNameArray);

		// TODO: more practical use of quicksort, sort user-added definitions
		QuickSort.quickSort(builtinNames);
		definitions.getChildren().add(builtins);

		// add built-in definition buttons to panel
		for (String name : builtinNames) {
			Button button = new Button(name);
			definitions.setMargin(button, new Insets(0, 0, 0, 16));
			definitions.getChildren().add(button);

			// TODO: possibly move to seperate EventHandler class
			button.setOnAction(e -> {
				System.out.println(name);
				Instruction ins = new Instruction(name);
				instPane.addInstruction(ins);
			});
		}

		Button addConstantOp = new Button("Constant");
		Button addRegisterOp = new Button("Register");
		Button addMemoryOp   = new Button("Memory");
		Button addEmptyOp    = new Button("Empty");

		// define button actions for operand adding buttons
		addEmptyOp.setOnAction(e    -> { instPane.addOperand(new EmptyOperand()); });
		addConstantOp.setOnAction(e -> { instPane.addOperand(new ConstantOperand()); });
		addRegisterOp.setOnAction(e -> { instPane.addOperand(new RegisterOperand()); });
		addMemoryOp.setOnAction(e   -> { instPane.addOperand(new MemoryOperand()); });

		// set spacing on operand buttons
		definitions.setMargin(addConstantOp, new Insets(0, 0, 0, 16));
		definitions.setMargin(addRegisterOp, new Insets(0, 0, 0, 16));
		definitions.setMargin(addMemoryOp, new Insets(0, 0, 0, 16));
		definitions.setMargin(addEmptyOp, new Insets(0, 0, 0, 16));

		// add operation buttons to the list
		definitions.getChildren().addAll(
			operandTitle,
			addConstantOp,
			addRegisterOp,
			addMemoryOp,
			addEmptyOp
		);

		// Intialize section for user-specified definition blocks
		Button definitionCreate = new Button("Create new definition");
		definitions.getChildren().add(new Separator());
		definitions.getChildren().add(userdefined);
		definitions.getChildren().add(definitionCreate);

		definitionCreate.setOnAction(
			new CreateDefinitionHandler<ActionEvent>(instPane, primaryStage, 
		                                             definitions));
	}
}

class OpenHandler<T extends Event> implements EventHandler<T> {
	private InstructionPane instPane;
	private Stage stage;

	public OpenHandler(InstructionPane instructionPane, Stage targetStage) {
		instPane = instructionPane;
		stage    = targetStage;
	}

	public void handle(T ev) {
		FileChooser chooser = new FileChooser();
		File file = chooser.showOpenDialog(stage);

		if (file != null) {
			try {
				instPane.loadState(file);
				System.out.println("Opened file " + file.toPath());

			} catch (java.io.FileNotFoundException exception) {
				System.out.println("Couldn't open " + file.toPath());
			}

		} else {
			System.out.println("Cancelled open");
		}
	}
}

class SaveHandler<T extends Event> implements EventHandler<T> {
	private InstructionPane instPane;
	private Stage stage;

	public SaveHandler(InstructionPane instructionPane, Stage targetStage) {
		instPane = instructionPane;
		stage    = targetStage;
	}

	public void handle(T ev) {
		FileChooser chooser = new FileChooser();
		File file = chooser.showSaveDialog(stage);

		if (file != null) {
			try {
				instPane.saveState(file);
				System.out.println("Saved file " + file.toPath());

			} catch (java.io.FileNotFoundException exception) {
				System.out.println("Couldn't save " + file.toPath());
			}

		} else {
			System.out.println("Cancelled save");
		}
	}
}

class CloseHandler<T extends Event> implements EventHandler<T> {
	private InstructionPane instPane;
	private Stage stage;

	public CloseHandler(InstructionPane instructionPane, Stage targetStage) {
		instPane = instructionPane;
		stage    = targetStage;
	}

	public void handle(T ev) {
		ButtonType yesButtonType = new ButtonType("Save", ButtonData.YES);
		ButtonType noButtonType = new ButtonType("Don't save", ButtonData.NO);
		ButtonType cancelButtonType = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

		Dialog<ButtonType> dialog = new Dialog<>();

		dialog.getDialogPane().getButtonTypes().addAll(yesButtonType, noButtonType, cancelButtonType);
		dialog.getDialogPane().lookupButton(yesButtonType).setDisable(false);
		dialog.getDialogPane().lookupButton(noButtonType).setDisable(false);
		dialog.getDialogPane().lookupButton(cancelButtonType).setDisable(false);
		dialog.setContentText("Do you want to save your changes before closing?");

		dialog.showAndWait().ifPresent(response -> {
			if (response == yesButtonType) {
				System.out.println("Saving before closing...");
			}

			else if (response == noButtonType) {
				System.out.println("Not saving before closing...");
			}

			else {
				System.out.println("Cancelled closing");
			}
		});
	}
}

class CreateDefinitionHandler<T extends Event> implements EventHandler<T> {
	private InstructionPane instPane;
	private Stage stage;
	private VBox definitions;

	public CreateDefinitionHandler(InstructionPane instructionPane,
	                               Stage targetStage,
	                               VBox definitionVBox)
	{
		instPane    = instructionPane;
		stage       = targetStage;
		definitions = definitionVBox;
	}

	public void handle(T ev) {
		TextInputDialog input = new TextInputDialog("Testing this");
		input.setContentText("Name of new block:");

		input.showAndWait().ifPresent(response -> {
			Button btn = new Button(response);

			definitions.setMargin(btn, new Insets(0, 0, 0, 16));
			System.out.printf("Response: %s\n", response);
			definitions.getChildren().add(btn);

			btn.setOnAction(f -> {
				// TODO: call instructions should have a line/arrow pointing
				//       from the instruction to the referenced block
				//
				//       this could be done by linking position properties,
				//       this would give a cool visual of the program flow

				// Create a new instruction with an operand containing a reference
				// to the clicked procedure name
				Instruction ins = new Instruction("call");
				Operand callHash = new ConstantOperand();
				callHash.setTarget("#" + response);
				ins.setOperand(0, callHash);
				instPane.addInstruction(ins);
			});

			instPane.addDefinition(new Definition(response));
		});
	}
}
