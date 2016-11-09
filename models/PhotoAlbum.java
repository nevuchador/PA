package models;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import models.utilities.User;
import models.utilities.UserList;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import controllers.LoginController;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
/**
 * Provides the main method and driver for the Photo Album application. Loads and
 * starts the Login Controller.
 *
 * @author Michael Alweis
 * @author Subin Ahn
 *
 */
public class PhotoAlbum extends Application {
	/**
	 * The primary UI stage for the display of the application.
	 */
	public static Stage primaryStage;
	/**
	 * The list of users associated with the application.
	 */
	public static UserList userList;

	/**
	 * Starts the controller.
	 * @param primaryStage
	 * 		mainstage to start controllers
	 * @throws Exception
	 * 		Exception for error
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		PhotoAlbum.primaryStage = primaryStage;
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/view/Login.fxml"));
		AnchorPane root = (AnchorPane) loader.load();

		LoginController loginController = loader.getController();
		loginController.start(primaryStage);

		loginController.setPrimaryStage(primaryStage);
		Scene loginScene = new Scene(root, 286, 238);

		// Scene adminScene = new Scene(root);
		primaryStage.setScene(loginScene);
		primaryStage.show();
	}
	/**
	 * Sets the application in motion by reading in all of the existing user data saved
	 * from previous sessions and launching the application.
	 *
	 * @param args
	 * 		start PhotoAlbum
	 * @throws IOException
	 * 		Exception for input and output
	 * @throws ClassNotFoundException
	 * 		Exception for ClassNotFound
	 * 		
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		userList = new UserList();
		try{

		FileInputStream f_in = new FileInputStream("data/dummy.txt");

		// Read object using ObjectInputStream
		ObjectInputStream obj_in = new ObjectInputStream(f_in);
		while(true){
		// Read an object
		User u =(User)obj_in.readObject();
		UserList.addUser(u);
		}
		}catch(EOFException e){}
		launch(args);
	}
}
