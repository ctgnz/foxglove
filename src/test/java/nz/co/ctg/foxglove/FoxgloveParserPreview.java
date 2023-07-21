package nz.co.ctg.foxglove;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class FoxgloveParserPreview extends Application {

    public static void main(String[] args) {
        Application.launch(FoxgloveParserPreview.class, args);
    }

    private Stage mainStage;
    private ScrollPane scrollPane;
    private Path defaultFile;
    private Path fileFolder;
    private FoxgloveParser parser;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.parser = new FoxgloveParser();
        this.mainStage = primaryStage;
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));

        defaultFile = Paths.get(SvgGraphic.class.getResource("/test.svg").toURI());
        fileFolder = defaultFile.getParent();
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
            if (filePath != null) {
                try {
                    scrollPane.setContent(createGraphic(filePath));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Button browse = new Button("Browse...");
        browse.setOnAction(evt -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setInitialDirectory(fileFolder.toFile());
            File selectedDir = chooser.showDialog(mainStage);
            if (selectedDir != null) {
                fileFolder = selectedDir.toPath();
                files.setItems(findSvgFiles());
            }
        });
        return new ToolBar(browse, files);
    }

    private ObservableList<Path> findSvgFiles() {
        try {
            return FXCollections.observableArrayList(Files.list(fileFolder).filter(path -> path.toString().endsWith(".svg")).collect(Collectors.toList()));
        } catch (Exception e) {
            e.printStackTrace();
            return FXCollections.emptyObservableList();
        }
    }

    private Node createGraphic(Path filePath) throws Exception {
        SvgGraphic svgElement = parser.parse(Files.newInputStream(filePath));
        Group graphic = svgElement.createGroup();
        Pane region = new Pane(graphic);
        region.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.DOTTED, null, BorderStroke.THIN)));
        return region;
    }

}
