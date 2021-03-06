package protocol;

import java.io.*;
import java.net.*;
import java.util.Stack;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import javax.swing.*;

/**
 * GUI session to display network communication
 */
public class Browser extends Application {
    public static final Logger log = Logger.getLogger("Browser");
    public static Stage stage;
    public static Scene scene;
    public static VBox history = new VBox();
    public static ScrollPane conversation = new ScrollPane(history);
    public static HBox console = new HBox();
    public static BorderPane layout = new BorderPane();
    public Client client = Client.session;
    public static Stack requests;

    public Browser(){
        log.info("interface instantiated");
        client.browser = this;
    }

    private SVGPath drawSVG(String track){
        SVGPath path = new SVGPath();
        path.setContent(track);
        path.setStyle("-fill:#000000;-hover-fill:#d3d3d3");
        return path;
    }

    private void drawUI(){
        TextField field = new TextField();
        field.setMinHeight(40.0);
        field.setPromptText("write here");
        field.setStyle("-fx-focus-color:transparent;-fx-background-color:#ffffff;");
        Button button = new Button();
        Double radius = 20.0;
        Group svg = new Group(drawSVG("M50 -8 L-7 -3 L5 7 Z"),drawSVG("M50 -8 L33 9 L6 8 Z"));
        svg.setScaleX(Math.min(20/svg.getBoundsInParent().getWidth(), 20/svg.getBoundsInParent().getHeight()));
        button.setShape(new Circle(radius));
        button.setGraphic(svg);
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        button.setMaxSize(radius*2,radius*2);
        button.setMinSize(radius*2,radius*2);
        button.setOnAction(event -> {
            if(field.getText() == ""){
                event.consume();
            };
            log.info("sending request...");
            bubble(field.getText(),Pos.CENTER_RIGHT,"#00bcd4","M169.6,80H108H80C75.6,80,72,83.6,72,88V132.7C72,137.1,75.6,140.7,80,140.7H169.6C174,140.7,177.6,137.1,177.6,132.7V88C177.6,83.6,174,80,169.6,80Z");
            String request = field.getText();
            try{requests.push(request);}catch(Exception e){e.printStackTrace();};
            log.info("recieving response...");
            String response = client.contact(request);
            bubble(response,Pos.CENTER_LEFT,"D3D3D3","M169.6,80H108H80C75.6,80,72,83.6,72,88V132.7C72,137.1,75.6,140.7,80,140.7H169.6C174,140.7,177.6,137.1,177.6,132.7V88C177.6,83.6,174,80,169.6,80Z");
            field.setText("");
            field.requestFocus();
        });
        bubble(client.contact(null),Pos.CENTER_LEFT,"#D3D3D3","M169.6,80H108H80C75.6,80,72,83.6,72,88V132.7C72,137.1,75.6,140.7,80,140.7H169.6C174,140.7,177.6,137.1,177.6,132.7V88C177.6,83.6,174,80,169.6,80Z");
        conversation.setFitToHeight(true);
        history.setStyle("-fx-background-color:#ffffff");
        conversation.setStyle("-fx-focus-color:transparent;-fx-background-color:#ffffff;");
        conversation.setMaxHeight(500.0);
        conversation.setFitToHeight(true);
        conversation.setFitToWidth(true);
        conversation.addEventFilter(ScrollEvent.SCROLL,event->{
                if (event.getDeltaX() != 0){
                    event.consume();
            }
        });
        console.addEventHandler(KeyEvent.KEY_PRESSED,event -> {
            if(event.getCode() == KeyCode.ENTER) {
                button.fire();
                event.consume();
            }
        });
        console.setAlignment(Pos.BOTTOM_CENTER);
        HBox.setHgrow(field, Priority.ALWAYS);
        console.getChildren().addAll(field, button);
        layout.setCenter(conversation);
        layout.setBottom(console);
        layout.setStyle("-fx-background-color:#ffffff");
    }

    @Override
    public void start(Stage browser) throws Exception {
        stage = browser;
        stage.setTitle("Protocol");
        stage.setOnCloseRequest(event -> {
            System.exit(1);
        });
        drawUI();
        scene = new Scene(layout);
        stage.setScene(scene);
        stage.show();
    }

    public void bubble(String message, Pos position, String color,String shape){
        Client.log.info("inserting message...");
        VBox record = new VBox();
        Label label = new Label(message);
        label.setStyle("    -fx-shape: \""+shape+"\";-fx-background-color: transparent,"+color+";-fx-background-insets: 0,1;-fx-padding: 5;");
        label.setWrapText(true);
        label.setFont(new Font("Courier",10));
        record.setMaxWidth(Double.MAX_VALUE);
        record.setAlignment(position);
        record.getChildren().add(label);
        history.getChildren().add(record);
    }
}

