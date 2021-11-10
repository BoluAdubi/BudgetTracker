//package ch.makery.address;

import java.io.IOException;

import budgettracker.UserAccount;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

    private Stage primaryStage;
    private Parent rootLayout;
    private UserAccount account = UserAccount.getInstance();

    
    /** 
     * Initilizes the window of the application.
     * @param primaryStage : Stage Window for UI
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Budgeting");

        initRootLayout();
    }
    
    /**
     * Inilizes the "root" node, or the scene which contains all of the other JAVAFX objects.
     * It also grabs the FXML file and loads it into the application.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("home.fxml"));
            rootLayout = (Pane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            HomeController controller = loader.getController();
            controller.setStage(primaryStage);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** 
     * Starts the application.
     * @param args : String[]
     */
    public static void main(String[] args) {
        launch(args);
    }
}