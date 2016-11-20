/**
 * Created by mistlight on 11/19/2016.
 */

package com.otakukingdom.audiobook;

import com.otakukingdom.audiobook.services.DatabaseService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.SQLException;

public class Main extends Application {


    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("MainWindow.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        DatabaseService databaseService = DatabaseService.getInstance();
        try {
            databaseService.initDb();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        launch(args);
    }
}
