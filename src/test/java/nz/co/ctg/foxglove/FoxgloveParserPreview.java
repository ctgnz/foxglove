package nz.co.ctg.foxglove;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class FoxgloveParserPreview extends Application {

    public static void main(String[] args) {
        Application.launch(FoxgloveParserPreview.class, args);
    }

    private Stage mainStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.mainStage = primaryStage;
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));

        root.setCenter(new ScrollPane(createGraphic()));

        Scene scene = new Scene(root);
        mainStage.setScene(scene);
        mainStage.setResizable(true);
        mainStage.setMaximized(true);
        mainStage.setTitle("Icon Previewer");
        mainStage.show();
    }

    private Node createGraphic() throws Exception {
        FoxgloveParser parser = new FoxgloveParser();
        SvgGraphic svgElement = parser.parse(SvgGraphic.class.getResourceAsStream("/test.svg"));
        return svgElement.createGraphic();
    }

}
