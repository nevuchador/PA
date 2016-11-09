package controllers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import javafx.stage.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Callback;
import models.PhotoAlbum;
import models.utilities.Album;
import models.utilities.Photo;
import models.utilities.User;
import models.utilities.UserList;
/**
 * Provides a controller for the UI and logic of the "home screen" of a user.
 * From here they can see their album folders and open,rename,or delete them.
 * In addition, they can perform a search from here.
 *
 * @author Michael Alweis
 * @author Subin Ahn
 *
 */
public class HomeController {
	/**
	 * The current Stage.
	 */
	protected static Stage primaryStage;
	/**
	 * The owner whose albums are to be displayed.
	 */
	private User user;
	/**
	 * A list of the ImageViews for the user's albums.
	 */
	protected ArrayList<ImageView> albumImages;
	/**
	 * A map from the ImageView of an album folder to the user's associated Album instance.
	 */
	protected HashMap<ImageView, Album> albumImageMap;
	/**
	 * The currently selected Album.
	 */
	protected Album selectedAlbum;
	/**
	 * The currently selected UI element that holds the image of the album folder.
	 */
	protected VBox selectedImage;
	/**
	 * The name label for the currently selected album.
	 */
	protected Label selectedLabel;
	/**
	 * The key-value pairs that the user provides for searching by tags.
	 */
	protected HashMap<String, ArrayList<String>> searchTags;
	/**
	 * The UI container that holds all of the album images.
	 */
	@FXML // fx:id = "flows"
	FlowPane flows;
	/**
	 * The button to add an album.
	 */
	@FXML // fx:id = "addButton"
	Button addButton;
	/**
	 * The button to rename an album.
	 */
	@FXML // fx:id = "renameButton"
	Button renameButton;
	/**
	 * The button to open an album.
	 */
	@FXML // fx:id = "openAlbumButton"
	Button openAlbumButton;
	/**
	 * The button to delete the selected album.
	 */
	@FXML // fx:id = "deleteAlbumButton"
	Button deleteAlbumButton;

	/**
	 * The UI container holding information about the selected album.
	 */
	@FXML // fx:id = "contentPane"
	Pane contentPane;
	/**
	 * The UI container holding the date information for the selected album.
	 */
	@FXML // fx:id = "datePane"
	Pane datePane;
	/**
	 * Text field for the name of the selected album.
	 */
	@FXML // fx:id = "nameField"
	TextField nameField;
	/**
	 * Text field for the number of photos in the selected album.
	 */
	@FXML // fx:id = "numField"
	TextField numField;
	/**
	 * Text field for the earliest date in the album.
	 */
	@FXML // fx:id = "earliestField"
	TextField earliestField;
	/**
	 * Text field for the latest date in the album.
	 */
	@FXML // fx:id = "latestField"
	TextField latestField;
	/**
	 * UI element to choose minimum date filter for search.
	 */
	@FXML // fx:id = "fromDatePicker"
	DatePicker fromDatePicker;
	/**
	 * UI element to choose maximum date filter for search.
	 */
	@FXML // fx:id = "toDatePicker"
	DatePicker toDatePicker;
	/**
	 * Container for the search tags UI elements.
	 */
	@FXML // fx:id = "tagsVbox"
	VBox tagsVbox;
	/**
	 * Button to perform search.
	 */
	@FXML // fx:id = "searchButton"
	Button searchButton;
	/**
	 * Menu button to choose tag key.
	 */
	@FXML // fx:id = "tagMenu"
	MenuButton tagMenu;
	/**
	 * Field to provide tag value.
	 */
	@FXML // fx:id = "tagField"
	TextField tagField;
	/**
	 * Button to add tag filter for search purposes.
	 */
	@FXML // fx:id = "addTagButton"
	Button addTagButton;

	/**
	 * Button to logout of the current user session.
	 */
	@FXML // fx:id = "logoutAlbumButton"
	Button logoutAlbumButton;

/**
 * The method that is called to start the controller.
 * @param mainStage
 * 		mainstage to start HomeController
 */
	@FXML
	public void start(Stage mainStage) {
		mainStage.setTitle("Home");
		albumImages = new ArrayList<ImageView>();
		albumImageMap = new HashMap<ImageView, Album>();
		initializeMenu();
		flows.setVgap(10);
		flows.setHgap(10);
		flows.setBorder(new Border(
				new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		refreshAlbumGallery();

		mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			/**
			 * Handles serialization of user data in case of close request.
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
	 * Getter method for the current user.
	 * @return
	 * 		return the current user
	 */
	public User getUser() {
		return user;
	}
	/**
	 * Setter method for the current user.
	 * @param user
	 * 		set the current user
	 */
	public void setUser(User user) {
		this.user = user;
	}
	/**
	 * Method that clears all the current images in the gallery and resets them with
	 * the current user data.
	 */
	public void refreshAlbumGallery() {
		// clear all of the FlowPane nodes
		flows.getChildren().clear();
		if (user.getAlbums() == null) {
			return;
		}
		for (Album a : user.getAlbums()) {
			VBox v = new VBox();
			ImageView i = new ImageView();
			i.setOnMouseClicked(mouseHandler);
			Label name = new Label();
			name.setText(a.getName());
			name.setTextAlignment(TextAlignment.CENTER);
			albumImages.add(i);
			albumImageMap.put(i, a);
			i.setImage(new Image("folderIcon.jpg"));
			i.setFitHeight(50);
			i.setFitWidth(50);
			v.getChildren().add(i);
			v.getChildren().add(name);
			flows.getChildren().add(v);
			openAlbumButton.setDisable(true);
		}
	}
	/**
	 * Handler that either opens the clicked album if double-clicked, or otherwise
	 * places a border around the selected image.
	 */
	EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent t) {
			openAlbumButton.setDisable(false);
			// if double click, open the album
			if (t.getClickCount() == 2) {
				try {
					openAlbum();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (renameButton.isDisable() == true) {
				renameButton.setDisable(false);
			}
			if (deleteAlbumButton.isDisable() == true) {
				deleteAlbumButton.setDisable(false);
			}
			ImageView imageView = (ImageView) t.getSource();
			VBox v = (VBox) imageView.getParent();
			// if it is a new selection, get rid of the border on the old
			// selection
			if (!(selectedImage == null) && !selectedImage.equals(v)) {
				selectedImage.setStyle("-fx-padding: 0;" + "-fx-border-style: none;");
			}
			v.setStyle("-fx-padding: 1;" + "-fx-border-style: solid inside;" + "-fx-border-width: 1;"
					+ "-fx-border-insets: 1;" + "-fx-border-radius: 1;" + "-fx-border-color: blue;");
			selectedImage = v;
			selectedAlbum = albumImageMap.get(imageView);
			contentPane.setVisible(true);
			nameField.setText(selectedAlbum.getName());
			numField.setText(Integer.toString(selectedAlbum.getPhotos().size()));
			if (selectedAlbum.getPhotos() != null && selectedAlbum.getPhotos().size() > 0) {
				datePane.setVisible(true);
				earliestField.setText(findEarliest());
				latestField.setText(findLatest());
			} else {
				datePane.setVisible(false);
			}
		}
	};
	/**
	 * Handler method for the add button - asks for a name and creates a new album folder.
	 * @param e
	 * 		ActionEvent e
	 */
	@FXML
	public void handleAdd(ActionEvent e) {
		Dialog<User> d = new Dialog<User>();
		d.setTitle("Add an Album");
		d.setHeaderText("Add an album to your MyPhoto profile:\n");

		Label albumName = new Label("Album Name ");
		TextField t1 = new TextField();

		GridPane grid = new GridPane();
		grid.add(albumName, 1, 1);
		grid.add(t1, 2, 1);

		d.getDialogPane().setContent(grid);
		ButtonType okButton = new ButtonType("Okay", ButtonData.OK_DONE);

		ButtonType cancelButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		d.getDialogPane().getButtonTypes().add(okButton);
		d.getDialogPane().getButtonTypes().add(cancelButton);

		d.setResultConverter(new Callback<ButtonType, User>() {
			@Override
			public User call(ButtonType bType) {
				if (bType == okButton) {
					if (t1.getText().length() > 0) {
						String name = t1.getText();
						boolean b = user.addAlbum(name);
						if (!b) {
							Alert alert = new Alert(AlertType.INFORMATION);
							alert.setTitle("Error Adding Album");
							alert.setHeaderText("Existing album name");
							alert.setContentText("To add an album, it must have" + " a unique name");
							alert.show();
						}
					}
				}

				return null;
			}
		});
		Optional<User> result = d.showAndWait();
		refreshAlbumGallery();
		contentPane.setVisible(false);
		datePane.setVisible(false);
		renameButton.setDisable(true);
		deleteAlbumButton.setDisable(true);
		openAlbumButton.setDisable(true);
	}
	/**
	 * Method to delete the current album and its associated photos.
	 * @param e
	 * 		ActionEvent e
	 */
	@FXML
	public void deleteAlbumHandler(ActionEvent e) {

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Delete Confirmation");
		alert.setHeaderText("You are about to remove this album.");
		alert.setContentText("Are you sure you want to delete this album? All their photos will be deleted.");

		Optional<ButtonType> result = alert.showAndWait();
		if ((result.get() == ButtonType.OK)) {
			alert.close();
			String name = selectedAlbum.getName();
			int i = user.Albumindexer(name);
			user.delAlbum(i, name);


		} else {
			alert.close();
		}

		refreshAlbumGallery();
		contentPane.setVisible(false);
		datePane.setVisible(false);
		renameButton.setDisable(true);
		deleteAlbumButton.setDisable(true);
		openAlbumButton.setDisable(true);
	}
	/**
	 * Method that handles renaming the selected album.
	 * @param e
	 * 		ActionEvent e
	 */
	@FXML
	public void renameHandler(ActionEvent e) {
		Dialog<User> d = new Dialog<User>();
		d.setTitle("Rename Album");
		d.setHeaderText("Rename your album called: " + selectedAlbum.getName() + ":\n");

		Label albumName = new Label("New Name ");
		TextField t1 = new TextField();

		GridPane grid = new GridPane();
		grid.add(albumName, 1, 1);
		grid.add(t1, 2, 1);

		d.getDialogPane().setContent(grid);
		ButtonType okButton = new ButtonType("Okay", ButtonData.OK_DONE);

		ButtonType cancelButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		d.getDialogPane().getButtonTypes().add(okButton);
		d.getDialogPane().getButtonTypes().add(cancelButton);

		d.setResultConverter(new Callback<ButtonType, User>() {
			@Override
			public User call(ButtonType bType) {
				if (bType == okButton) {
					if (t1.getText().length() > 0) {
						String name = t1.getText();
						boolean b = user.renameAlbum(selectedAlbum, name);
						if (!b) {
							Alert alert = new Alert(AlertType.INFORMATION);
							alert.setTitle("Error Renaming Album");
							alert.setHeaderText("Existing album name");
							alert.setContentText("To rename an album, it must have" + " a unique name");
							alert.show();
						}

					}
				}

				return null;
			}
		});
		Optional<User> result = d.showAndWait();
		refreshAlbumGallery();
	}
	/**
	 * Method to handle opening the album and loading the Album controller.
	 * @param e
	 * 		ActionEvent e
	 * @throws IOException
	 * 		Exception for input and output
	 */
	@FXML
	public void openAlbumHandler(ActionEvent e) throws IOException {
		openAlbum();
	}

	public void openAlbum() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/view/album.fxml"));
		Scene homeScene = new Scene((Parent) loader.load());

		AlbumController albumController = loader.getController();
		AlbumController.setAlbum(selectedAlbum);
		// set the user for the albumController
		AlbumController.setUser(user);
		AlbumController.primaryStage = primaryStage;

		albumController.start(PhotoAlbum.primaryStage);

		PhotoAlbum.primaryStage.setScene(homeScene);
		PhotoAlbum.primaryStage.show();
	}
	/**
	 * Finds the earliest photo date in the selected Album.
	 * @return String result
	 * 		return the earliest date of phot
	 */
	public String findEarliest() {
		// get the first photo's date
		Calendar earliest = selectedAlbum.getPhotos().get(0).date;
		for (Photo p : selectedAlbum.getPhotos()) {
			if (p.date.compareTo(earliest) < 0) {
				earliest = p.date;
			}
		}
		String result = null;
		SimpleDateFormat date = new SimpleDateFormat("MM/dd/yyyy");
		result = date.format(earliest.getTime());
		return result;
	}
	/**
	 * Finds the latest photo date in the selected album.
	 * @return String result
	 * 		return the lastst date of photo
	 */
	public String findLatest() {
		// get the first photo's date
		Calendar latest = selectedAlbum.getPhotos().get(0).date;
		for (Photo p : selectedAlbum.getPhotos()) {
			if (p.date.compareTo(latest) > 0) {
				latest = p.date;
			}
		}
		String result = null;
		SimpleDateFormat date = new SimpleDateFormat("MM/dd/yyyy");
		result = date.format(latest.getTime());
		return result;
	}
	/**
	 * Method to handle the search button. Determines the search paramaters provided
	 * by the user and loads the search controller to find and display the results of
	 * the search.
	 * @param e
	 * 		ActionEvent e
	 * @throws IOException
	 * 		Exception for input and output
	 */
	@FXML
	public void searchHandler(ActionEvent e) throws IOException {
		boolean searchingDate;
		boolean searchingTags;
		Calendar fromCal = null;
		Calendar toCal = null;
		LocalDate fromLocalDate = fromDatePicker.getValue();
		LocalDate toLocalDate = toDatePicker.getValue();
		if (fromLocalDate == null || toLocalDate == null) {
			searchingDate = false;
		} else {
			searchingDate = true;
			// convert each to Date
			Instant instant = Instant.from(fromLocalDate.atStartOfDay(ZoneId.systemDefault()));
			Date fromDate = Date.from(instant);
			instant = Instant.from(toLocalDate.atStartOfDay(ZoneId.systemDefault()));
			Date toDate = Date.from(instant);

			// convert each to a Calendar instance
			fromCal = Calendar.getInstance();
			toCal = Calendar.getInstance();
			fromCal.setTime(fromDate);
			toCal.setTime(toDate);
			// set milliseconds to 0
			fromCal.set(Calendar.MILLISECOND, 0);
			toCal.set(Calendar.MILLISECOND, 0);
		}
		if (searchTags.size() == 0) {
			searchingTags = false;
		} else {
			searchingTags = true;
		}

		if (searchingDate == false && searchingTags == false) {
			return;
		}
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/view/search.fxml"));
		Scene searchScene = new Scene((Parent) loader.load());

		SearchController searchController = loader.getController();
		searchController.setUser(user);

		searchController.fromCal = fromCal;
		searchController.toCal = toCal;
		searchController.searchTags = searchTags;
		searchController.start(PhotoAlbum.primaryStage);

		PhotoAlbum.primaryStage.setScene(searchScene);
		PhotoAlbum.primaryStage.show();
	}
	/**
	 * Method to add a UI display of a key-value pair to the tag box container.
	 * @param e
	 * 		ActionEvent e
	 */
	@FXML
	public void addTagBoxHandler(ActionEvent e) {

		String key = tagMenu.getText();
		String value = tagField.getText();
		if (tagField.getText().isEmpty() || value == "Tag") {
			return;
		}

		Label keyLbl = new Label(key + ": ");
		Label valLbl = new Label(value);

		if(searchTags.get(key) == null){
			searchTags.put(key, new ArrayList<String>());
			searchTags.get(key).add(value);
		}
		else{
			searchTags.get(key).add(value);
		}

		HBox h = new HBox();
		h.setAlignment(Pos.CENTER);
		h.getChildren().addAll(keyLbl, valLbl);
		tagsVbox.getChildren().add(tagsVbox.getChildren().size() - 1, h);

		// clear the menu and textField
		tagField.clear();
		tagMenu.setText("Tag");
	}
	/**
	 * A method to fill the Tag menu with all of the possible keys as defined in the Photo class.
	 */
	public void initializeMenu() {
		searchTags = new HashMap<String, ArrayList<String>>();
		tagMenu.getItems().clear();
		for (String s : Photo.possibleTags) {
			MenuItem m = new MenuItem(s);
			m.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent t) {
					tagMenu.setText(s);
					addTagButton.setDisable(false);
				}
			});
			tagMenu.getItems().addAll(m);
		}
	}
	/**
	 * Method to handle the request to logout of the current user session.
	 * @param e
	 * 		ActionEvent e
	 * @throws Exception
	 * 		Exception
	 */
	@FXML
	public void logoutAlbumHandler(ActionEvent e) throws Exception{

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/view/Login.fxml"));
		AnchorPane root = (AnchorPane) loader.load();

		LoginController loginController = loader.getController();
		loginController.start(PhotoAlbum.primaryStage);

		loginController.primaryStage = PhotoAlbum.primaryStage;
		Scene loginScene = new Scene(root, 286, 238);

		// Scene adminScene = new Scene(root);
		PhotoAlbum.primaryStage.setScene(loginScene);
		PhotoAlbum.primaryStage.show();
	}
}
