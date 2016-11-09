package controllers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import models.PhotoAlbum;
import models.utilities.User;
import models.utilities.UserList;
/**
 * Provides a controller class for all of the UI elements and logic
 * of a basic login screen with User and Password functionality.
 *
 * @author Michael Alweis
 * @author Subin Ahn
 *
 */
public class LoginController {
	/**
	 * The FX Stage for the login screen
	 */
	protected Stage primaryStage;
	/**
	 * The UserName Field
	 */
	@FXML // fx:id="userName"
	TextField userName;

	/**
	 * The Password field
	 */
	@FXML // fx:id="password"
	PasswordField password;

	/**
	 * Starts the controller.
	 * @param mainStage
	 * 		mainstage to start LoginController
	 */
	@FXML
	public void start(Stage mainStage) {
		mainStage.setTitle("MyPhotos - Login");
		mainStage.setResizable(false);

		mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			/**
			 * When the user clicks the "x" to close the window, this method
			 * serializes all of the current user objects for persistence
			 */
			public void handle(WindowEvent we) {
				// write user to serialization file;
				try {
					FileOutputStream f_out = new FileOutputStream("data/dummy.txt");
					ObjectOutputStream obj_out = new ObjectOutputStream(f_out);

					for (User u : UserList.users) {
						obj_out.writeObject(u);
					}

					obj_out.flush();
					obj_out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		});
	}

	/**
	 * handles the logic for logging in upon clicking the 'login' button
	 *
	 * @param e
	 * 		ActionEvent e
	 * @throws IOException
	 * 		Exception for input and output
	 */
	@FXML
	public void loginHandler(ActionEvent e) throws IOException {
		String user = userName.getText();
		String pw = password.getText();

		if(user.equals("") || pw.equals("")){
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Login Confirmation");
			alert.setHeaderText("Missing Required Fields");
			alert.setContentText("You must enter username and password to use photoalbum.");
			alert.show();
		}
		// check if admin is logging in
		else if (user.equals("admin") && pw.equals("admin")){
			//Load the admin controller and screen
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/view/admin.fxml"));
			loader.setController(new AdminController());
			Scene adminScene = new Scene((Parent) loader.load());
			AdminController adminController =
			         loader.getController();
			      adminController.start(PhotoAlbum.primaryStage);

			PhotoAlbum.primaryStage.setScene(adminScene);
			PhotoAlbum.primaryStage.show();
		}

		// check if user is logging in
		else {
			if(UserList.userMap.get(user)!=null&&UserList.userMap.get(user).getPassword().equals(pw)){
				User currUser = UserList.userMap.get(user);
				//Load the user's album home screen
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(getClass().getResource("/view/home.fxml"));
				Scene homeScene = new Scene((Parent) loader.load());

				HomeController homeController =
				         loader.getController();
				homeController.setUser(currUser);
				//set the user for the homeController
				      homeController.start(PhotoAlbum.primaryStage);

				PhotoAlbum.primaryStage.setScene(homeScene);
				PhotoAlbum.primaryStage.show();
			}
			else{
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Login Confirmation");
				alert.setHeaderText("Contact the admin");
				alert.setContentText("The username does not exist in the database, or the password is wrong");
				alert.show();
			}
		}
	}
	/**
	 * Handler method to close the program
	 * @param e
	 * 		ActionEvent e
	 * @throws IOException
	 * 		Exception for input and output
	 */
	@FXML
	public void exitHandler(ActionEvent e) throws IOException{
		Platform.exit();
	}
	/**
	 * Getter method for primary stage
	 * @return primaryStage
	 * 		return primarystage
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	/**
	 * Setter method for primary stage
	 * @param primaryStage
	 * 		set the primarystage
	 */
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
}