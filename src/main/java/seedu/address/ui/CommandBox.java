package seedu.address.ui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.history.History;
import seedu.address.logic.history.Historyable;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {

    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";

    private final CommandExecutor commandExecutor;
    private final Historyable<Command> commandHistory;

    @FXML
    private TextField commandTextField;

    /**
     * Creates a {@code CommandBox} with the given {@code CommandExecutor}.
     */
    public CommandBox(CommandExecutor commandExecutor) {
        super(FXML);
        this.commandExecutor = commandExecutor;
        this.commandHistory = new History<>();
        commandHistory.push(new Command(""));
        // calls #setStyleToDefault() whenever there is a change to the text of the command box.
        commandTextField.textProperty().addListener((unused1, unused2, unused3) -> setStyleToDefault());
    }

    /**
     * Handles any key button pressed event.
     *
     * @param event The key pressed event.
     */
    @FXML
    private void handleKeyPressed(KeyEvent event) {
        KeyCode keyCode = event.getCode();
        switch (keyCode) {
        case ENTER:
            handleCommandEntered();
            break;
        case UP:
        case DOWN:
            handleNavigateHistory(keyCode);
            break;
        default:
        }
    }

    /**
     * Handles the Enter button pressed event.
     */
    private void handleCommandEntered() {
        String commandText = commandTextField.getText();
        if (commandText.equals("")) {
            return;
        }

        commandHistory.pop();
        commandHistory.restore();
        commandHistory.push(new Command(commandText));
        commandHistory.push(new Command(""));
        commandTextField.setText("");

        try {
            commandExecutor.execute(commandText);
        } catch (CommandException | ParseException e) {
            setStyleToIndicateCommandFailure();
        }
    }

    /**
     * Handles the Up button pressed event.
     */
    private void handleNavigateHistory(KeyCode keyCode) {
        switch (keyCode) {
        case UP:
            Command previousCommand = commandHistory.back();
            commandTextField.setText(previousCommand.getCommand());
            commandTextField.end();
            break;
        case DOWN:
            Command nextCommand = commandHistory.forward();
            commandTextField.setText(nextCommand.getCommand());
            commandTextField.end();
            break;
        default:
        }
    }

    /**
     * Handles a key typed event.
     */
    @FXML
    private void handleKeyTyped(KeyEvent event) {
        Command editedCommand = new Command(commandTextField.getText());
        commandHistory.setCurrentState(editedCommand);
    }

    /**
     * Sets the command box style to use the default style.
     */
    private void setStyleToDefault() {
        commandTextField.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    /**
     * Sets the command box style to indicate a failed command.
     */
    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = commandTextField.getStyleClass();

        if (styleClass.contains(ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(ERROR_STYLE_CLASS);
    }

    /**
     * Represents a function that can execute commands.
     */
    @FunctionalInterface
    public interface CommandExecutor {
        /**
         * Executes the command and returns the result.
         *
         * @see seedu.address.logic.Logic#execute(String)
         */
        CommandResult execute(String commandText) throws CommandException, ParseException;
    }
}
