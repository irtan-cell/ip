package sandrone.ui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/** A chat bubble used to show either a user command or Sandrone's response. */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private Label senderName;
    @FXML
    private ImageView displayPicture;

    /** Creates a dialog box containing the supplied text and profile image. */
    public DialogBox(String text, Image image) {
        try {
            FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the dialog box view", e);
        }
        dialog.setText(text);
        displayPicture.setImage(image);
        senderName.setVisible(false);
        senderName.setManaged(false);
    }

    /** Returns a dialog box styled as a user command. */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.dialog.getStyleClass().add("user-label");
        return dialogBox;
    }

    /** Returns a dialog box styled as Sandrone's response. */
    public static DialogBox getSandroneDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        dialogBox.senderName.setText("Sandrone");
        dialogBox.senderName.setVisible(true);
        dialogBox.senderName.setManaged(true);
        return dialogBox;
    }

    /** Returns a dialog box styled as a Sandrone error response. */
    public static DialogBox getErrorDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        dialogBox.senderName.setText("Command error");
        dialogBox.senderName.setVisible(true);
        dialogBox.senderName.setManaged(true);
        dialogBox.dialog.getStyleClass().remove("reply-label");
        dialogBox.dialog.getStyleClass().add("error-label");
        return dialogBox;
    }

    /** Reverses the dialog box so Sandrone's response is displayed on the left. */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }
}
