import Graph.Graph;
import Hibernate.FlightHelper;
import Hibernate.HibernateUtil;
import Hibernate.PlaceHelper;
import Hibernate.entity.Flight;
import Hibernate.entity.Place;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main extends Application{
    static Label labelOne = new Label();
    static Label labelTwo = new Label("Нет такого пути");
    private static Pane pane = new Pane();
    private static List<Flight> flights = new ArrayList<>();
    public static void main(String[] args) {
        launch(args);

    }
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Drawing Operations Test");
        Button btn = new Button("Пуск ");
        Canvas canvas = new Canvas(1920, 1080);
        canvas.setScaleX(1.0);
        canvas.setScaleY(1.0);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        TextField text = new TextField();
        TextField text1 = new TextField();
        Label label = new Label();
        Label label1 = new Label();
        Label label2 = new Label();
        label.setText("Откуда: ");
        label.setFont(Font.font("Verdana", FontWeight.MEDIUM, 20));
        label1.setFont(Font.font("Verdana", FontWeight.MEDIUM, 20));
        label1.setText("Куда: ");
        label2.setText("Полёт");
        label2.setFont(Font.font("Verdana", FontWeight.MEDIUM, 20));
        label2.setLayoutX(300);
        label2.setLayoutY(430);
        label.setLayoutX(300);
        label.setLayoutY(500);
        label1.setLayoutX(300);
        label1.setLayoutY(600);
        text.setLayoutX(300);
        text.setLayoutY(550);
        text1.setLayoutX(300);
        text1.setLayoutY(650);
        btn.setLayoutX(300.0);
        btn.setMinSize(50, 20);
        btn.setLayoutY(800.0);
        btn.setLineSpacing(10);
        go(gc);
        btn.setOnAction(event -> {
                showDeikstr(text.getText(),text1.getText(),gc);
        });
        pane.getChildren().addAll(canvas, btn, text, label1, label, text1,label2);
        primaryStage.setScene(new Scene(pane));
        primaryStage.show();
    }
    public static void go(GraphicsContext gc){
        Place fromPlace = null,toPlace = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Place> places = new PlaceHelper(session).getPlaceList();
        flights = new FlightHelper(session).getFlightList();
        for (Flight flight : flights){
            for (Place place : places){
                if(Objects.equals(flight.getFrom(), place.getName())){
                    fromPlace = place;
                }
                if(Objects.equals(flight.getTo(), place.getName())){
                    toPlace = place;
                }
            }
            Graph.generateGraph(fromPlace,toPlace,flight);
        }
        Graph.printGraph();
        Graph.drawGraphWrap(Graph.graph,gc);
        session.close();
    }
    public static void showDeikstr(String from, String to,GraphicsContext gc){
        pane.getChildren().remove(labelOne);
        pane.getChildren().remove(labelTwo);
        gc.clearRect(650, 200, 1100, 1100);
        Graph.drawGraphWrap(Graph.graph,gc);
        ArrayList<String> order = new ArrayList<>();
        try {
             order =  Graph.Deikstra(from,to);
        }
        catch (NullPointerException exception){
            labelOne.setFont(Font.font("Verdana", FontWeight.MEDIUM, 20));
            labelOne.setText("Нет пути");
            labelOne.setLayoutX(300);
            labelOne.setLayoutY(700);
            pane.getChildren().add(labelOne);
            return;
        }
        gc.setStroke(Paint.valueOf("#ff0000"));
        for (int i = 0; i <order.size()-1 ; i++) {
            gc.strokeLine(Graph.graph.get(order.get(i)).getX(),Graph.graph.get(order.get(i)).getY(),Graph.graph.get(order.get(i+1)).getX(),Graph.graph.get(order.get(i+1)).getY());
        }

        //System.out.println(order);
        int sum = 0;
        for (int i = 0; i < order.size() - 1 ; i++) {
            for (Flight flight : flights) {
                sum += flight.returnPrice(order.get(i), order.get(i + 1));
            }
        }
        labelTwo.setFont(Font.font("Verdana", FontWeight.MEDIUM, 20));
        labelTwo.setText("Вся сумма: " + sum);
        labelTwo.setLayoutX(300);
        labelTwo.setLayoutY(700);
        pane.getChildren().add(labelTwo);
    }


    }
