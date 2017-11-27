package sample;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import java.util.ResourceBundle;
import java.net.URL;

public class Controller implements Initializable{
    @FXML private Canvas modelDrawWindow;

    @FXML private AnchorPane mainWindowSize;

    private GraphicsContext gc;


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
        gc = modelDrawWindow.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(50, 50, 100, 100);

        modelDrawWindow.setOnMouseDragged(event -> gc.fillRect(event.getX(), event.getY(), 5, 5));

        mainWindowSize.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                modelDrawWindow.setWidth(newValue.doubleValue());
            }
        });

        mainWindowSize.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                modelDrawWindow.setHeight(newValue.doubleValue());
            }
        });

    }

}
