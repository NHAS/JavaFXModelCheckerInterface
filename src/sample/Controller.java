package sample;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import java.util.ResourceBundle;
import java.net.URL;
import javax.swing.SwingUtilities;
import javax.swing.*;

public class Controller implements Initializable{

    @FXML private SwingNode modelDisplay;

    @FXML private AnchorPane mainWindowSize;


    @FXML
    private void handleQuit(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void handleCreateNew(ActionEvent event) {

    }

    @FXML
    private void handleOpenRecentAction(ActionEvent event) {


    }

    @FXML
    private void handleOpen(ActionEvent event) {

    }

    @FXML
    private void handleFileClose(ActionEvent event) {

    }

    @FXML
    private void handleSave(ActionEvent event) {

    }

    @FXML
    private void handleAddSelectedModel(ActionEvent event) {

    }

    @FXML
    private void handleAddallModels(ActionEvent event) {

    }

    @FXML
    private void handleClearCanvas(ActionEvent event) {

    }

    @FXML
    private void handlExportImage(ActionEvent event) {

    }

    @FXML
    private void handOptionsRequest(ActionEvent event) {

    }

    @FXML
    private void handleCompileRequest(ActionEvent event) {

    }

    @FXML
    private void handleSaveAs(ActionEvent event) {


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createAndSetSwingDrawingPanel(modelDisplay);


    }

    public void createAndSetSwingDrawingPanel(final SwingNode swingNode) {
        SwingUtilities.invokeLater(() -> {
            JButton jButton = new JButton("Click me!");
            jButton.setBounds(0,0,120,50);

            JPanel panel = new JPanel();
            panel.setLayout(null);
            panel.add(jButton);

            panel.setSize(120, 60);

            swingNode.setContent(panel);

        });
    }


}
