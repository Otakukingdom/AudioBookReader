/**
 * Created by mistlight on 11/19/2016.
 */

package com.otakukingdom.audiobook;

import com.otakukingdom.audiobook.controllers.MainController;
import com.otakukingdom.audiobook.services.AudioBookScanService;
import com.otakukingdom.audiobook.services.DatabaseService;
import com.otakukingdom.audiobook.services.DirectoryService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {


    @Override
    public void start(Stage stage) throws Exception {
        // init services
        DirectoryService directoryService = new DirectoryService();
        AudioBookScanService audioBookScanService = new AudioBookScanService(directoryService);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("MainWindow.fxml"));
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);

        MainController mainController = fxmlLoader.getController();
        System.out.println("MAIN CONTROLLER IS " + mainController.toString());
        mainController.setDirectoryService(directoryService);
        mainController.setAudioBookScanService(audioBookScanService);

        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        DatabaseService databaseService = DatabaseService.getInstance();
        databaseService.initDb();

        launch(args);
    }
}
