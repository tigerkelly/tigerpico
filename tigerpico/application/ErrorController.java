package application;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class ErrorController {
    @FXML
    private TextArea errorMessage ;

    public void setErrorText(String text) {
        errorMessage.setText(text);
        errorMessage.appendText("\nRestarting GUI...\n");
    }

    @FXML
    private void close() {
        errorMessage.getScene().getWindow().hide();
        System.exit(1);
    }
}