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
import javafx.scene.web.WebView;
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
    private WebView webView;

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
        webView = new WebView();
        webView.setMinSize(400, 800);
        root.setRight(webView);
        loadFile(defaultFile);

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
                loadFile(filePath);
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

    protected void loadFile(Path filePath) {
        try {
            scrollPane.setContent(createGraphic(filePath));
            webView.getEngine().load(filePath.toUri().toURL().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        svgElement.setBaseUri(filePath.toUri());
        // wired up so <a> activation is actually exercisable by clicking in this preview, rather than only visible
        // via a unit test - printing here is a stand-in for whatever a real embedding application would do
        RenderContext context = RenderContext.root(svgElement.getElementIndex(), 0, 0)
            .withBaseUri(svgElement.getBaseUri())
            .withAnchorActivationHandler(anchor -> System.out.println(
                "<a> activated: xlink:href=" + anchor.getXlinkHref() + " target=" + anchor.getTarget()));
        Group graphic = svgElement.createGraphic(context);
        Pane region = new Pane(graphic);
        region.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.DOTTED, null, BorderStroke.THIN)));
        return region;
    }

}
