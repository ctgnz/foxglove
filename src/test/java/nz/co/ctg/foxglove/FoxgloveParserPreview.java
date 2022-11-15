package nz.co.ctg.foxglove;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class FoxgloveParserPreview extends Application {

    public static void main(String[] args) {
        Application.launch(FoxgloveParserPreview.class, args);
    }

    private Stage mainStage;
    private ScrollPane scrollPane;
    private Path defaultFile;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.mainStage = primaryStage;
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));

        defaultFile = Paths.get(SvgGraphic.class.getResource("/test.svg").toURI());
        root.setTop(createToolbar());
        scrollPane = new ScrollPane(createGraphic(defaultFile));
        root.setCenter(scrollPane);

        Scene scene = new Scene(root);
        mainStage.setScene(scene);
        mainStage.setResizable(true);
        mainStage.setMaximized(true);
        mainStage.setTitle("Icon Previewer");
        mainStage.show();
    }

    private Node createToolbar() {
        ComboBox<Path> files = new ComboBox<>(findSvgFiles());
        files.getSelectionModel().select(defaultFile);
        files.setOnAction(evt -> {
            Path filePath = files.getSelectionModel().getSelectedItem();
            try {
                scrollPane.setContent(createGraphic(filePath));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return new ToolBar(files);
    }

    private ObservableList<Path> findSvgFiles() {
        try {
            return FXCollections.observableArrayList(Files.list(defaultFile.getParent()).filter(path -> path.toString().endsWith(".svg")).collect(Collectors.toList()));
        } catch (Exception e) {
            e.printStackTrace();
            return FXCollections.emptyObservableList();
        }
    }

    private Node createGraphic(Path filePath) throws Exception {
        FoxgloveParser parser = new FoxgloveParser();
        SvgGraphic svgElement = parser.parse(Files.newInputStream(filePath));
        Region graphic = svgElement.createGraphic();
        graphic.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.DOTTED, null, BorderStroke.THIN)));
        return graphic;
    }

}
