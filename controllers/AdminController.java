package controllers;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Optional;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import models.PhotoAlbum;
import models.utilities.User;
import models.utilities.UserList;
/**
 * Provides a controller class for the UI and logic of the administrator's
 * page. From here, the admin can add or remove users of the app.
 *
 * @author Michael Alweis
 * @author Subin Ahn
 *
 */
public class AdminController {
	/**
	 * A table which holds the users' name and password, 1 column for each
	 */
	@FXML // fx:id="table"
	TableView<User> table;
	/**
	 * Returns to the login page
	 */
	@FXML // fx:id="returnButton"
	Button returnButton;
	/**
	 * Button to add a user
	 */
	@FXML // fx:id="addUser"
	Button addUser;
	/**
	 * Button to remove a user from the table.
	 */
	@FXML // fx:id="removeUser"
	Button removeUser;
	/**
	 * Table column that holds the user name.
	 */
	@FXML // fx:id="userCol"
	TableColumn<User, String> userCol;
	/**
	 * Table column that holds the user's password
	 */
	@FXML // fx:id="pwCol"
	TableColumn<User, String> pwCol;

	/**
	 * Starts the controller.
	 * @param mainStage
	 * 		mainstage to start AdminController
	 */
	@FXML
	public void start(Stage mainStage) {
		mainStage.setTitle("MyPhotos - Administrator View");
		mainStage.setResizable(false);
		//set default text for empty table
		table.setPlaceholder(new Label("No current users"));
		userCol.setCellValueFactory(new Callback<CellDataFeatures<User, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<User, String> p) {
				return new ReadOnlyObjectWrapper<String>(p.getValue().getName());
			}
		});
		pwCol.setCellValueFactory(new Callback<CellDataFeatures<User, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<User, String> p) {
				return new ReadOnlyObjectWrapper<String>(p.getValue().getPassword());
			}
		});

		table.setItems(UserList.users);
		if (UserList.users.size() > 0) {
			table.getSelectionModel().select(0);
			removeUser.setDisable(false);
		}

		mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			/**
			 * Serializes the user list in case of a request to close the app.
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}
	/**
	 * A handler method for adding a user to the application, and to the user table.
	 * @param e
	 * 		ActionEvent e
	 */
	@FXML
	public void addUserHandler(ActionEvent e) {
		Dialog<User> d = new Dialog<User>();
		d.setTitle("Add a User");
		d.setHeaderText("Add a user to the MyPhoto application:\n" + "* - Required Field");

		Label user = new Label("*User Name: ");
		Label pw = new Label("*Password: ");

		TextField t1 = new TextField();
		TextField t2 = new TextField();

		GridPane grid = new GridPane();
		grid.add(user, 1, 1);
		grid.add(t1, 2, 1);
		grid.add(pw, 1, 2);
		grid.add(t2, 2, 2);

		d.getDialogPane().setContent(grid);
		ButtonType okButton = new ButtonType("Okay", ButtonData.OK_DONE);

		ButtonType cancelButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		d.getDialogPane().getButtonTypes().add(okButton);
		d.getDialogPane().getButtonTypes().add(cancelButton);
		d.setResultConverter(new Callback<ButtonType, User>() {
			@Override
			public User call(ButtonType bType) {
				if (bType == okButton) {
					if (t1.getText().length() > 0 && t2.getText().length() > 0) {
						//check for "admin"/"admin"
						if(t1.getText().equals("admin")&& t2.getText().equals("admin")){
							Alert alert = new Alert(AlertType.INFORMATION);
							alert.setTitle("Admin Error");
							alert.setHeaderText("Unavailable User Name");
							alert.setContentText("To add a user, they must have" + " a unique user name");
							alert.show();
							return null;
						}
						// check if user name already exists
						if (!UserList.userMap.containsKey(t1.getText())) {
							User u = new User(t1.getText(), t2.getText());
							UserList.addUser(u);
							removeUser.setDisable(false);
							return u;
						} else {
							Alert alert = new Alert(AlertType.INFORMATION);
							alert.setTitle("Error Adding User");
							alert.setHeaderText("Existing User Name");
							alert.setContentText("To add a user, they must have" + " a unique user name");
							alert.show();
						}
					}
				}

				return null;
			}
		});
		Optional<User> result = d.showAndWait();
		if (result.isPresent()) {
			User u = result.get();

			String uName = u.getName();
			String uPw = u.getPassword();

			if (uName.isEmpty() || uPw.isEmpty()) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Error Adding User");
				alert.setHeaderText("Missing Required Fields");
				alert.setContentText("To add a user, they must have" + " a user name" + " and password");
				alert.show();
			}
		}

	}
	/**
	 * A handler method for removing a user and their associated files from the app.
	 * @param e
	 * 		ActionEvent e
	 */
	@FXML
	public void removeUserHandler(ActionEvent e) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Delete Confirmation");
		alert.setHeaderText("You are about to remove this user.");
		alert.setContentText("Are you sure you want to delete this User? All their photos will be deleted.");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			alert.close();
			User u = table.getSelectionModel().getSelectedItem();
			UserList.users.remove(u);
			UserList.userMap.remove(u.getName());

			deleteDir(u.getDir());

		} else {
			alert.close();
		}
	}
	/**
	 * This method handles returning to the login window, exiting the admin view in the process.
	 * @param e
	 * 		ActionEvent e
	 * @throws IOException
	 * 		Exception for input and output
	 */
	@FXML
	public void returnButtonHandler(ActionEvent e) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/view/login.fxml"));
		Scene loginScene = new Scene((Parent) loader.load());

		LoginController loginController = loader.getController();
		loginController.start(PhotoAlbum.primaryStage);

		PhotoAlbum.primaryStage.setScene(loginScene);
		PhotoAlbum.primaryStage.show();
	}

	void deleteDir(File file) {
		File[] contents = file.listFiles();
		if (contents != null) {
			for (File f : contents) {
				deleteDir(f);
			}
		}
		file.delete();
	}

}
