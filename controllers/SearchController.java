package controllers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import models.PhotoAlbum;
import models.utilities.Album;
import models.utilities.Photo;
import models.utilities.User;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;

/**
 * Provides a controller class for the search results page of the application.
 * Handles the logic for the search itself as well as displaying the photos.
 *
 * @author Michael Alweis
 * @author Subin Ahn
 *
 */
public class SearchController {
	/**
	 * The current Java FX stage.
	 */
	protected static Stage primaryStage;
	/**
	 * The current User
	 */
	private User user;
	/**
	 * A list of photos matching the searh criteria.
	 */
	protected ArrayList<Photo> searchResults;
	/**
	 * The filter tags, part of the search criteria, mapped in key-value pairs.
	 */
	protected HashMap<String, ArrayList<String>> searchTags;
	/**
	 * The "from" date search filter.
	 */
	protected Calendar fromCal;
	/**
	 * The "to" date search filter.
	 */
	protected Calendar toCal;
	/**
	 * The UI ImageViews of the search results.
	 */
	protected ArrayList<ImageView> photoImages;
	/**
	 * A map of the ImageViews to their respective Photo objects.
	 */
	protected HashMap<ImageView, Photo> photoImageMap;
	/**
	 * The currently seleected Photo.
	 */
	protected Photo selectedPhoto;
	/**
	 * UI container for the current ImageView.
	 */
	protected VBox selectedImageBox;
	/**
	 * The UI label for the current Photo's name.
	 */
	protected Label selectedLabel;
	/**
	 * The UI container for the current Photo.
	 */
	protected ImageView selectedImage;
	/**
	 * The Image object that refers to the current Photo.
	 */
	protected Image selectedImageObject;
	/**
	 * Popup window for large display of the search results.
	 */
	protected Popup p;
	/**
	 * The container for holding all of the thumbnail images.
	 */
	@FXML // fx:id = "flows"
	FlowPane flows;
	/**
	 * The UI scroll container surrounding the thumbnail image flowPane.
	 */
	@FXML // fx:id = "scroll"
	ScrollPane scroll;
	/**
	 * A button for displaying the photos.
	 */
	@FXML // fx:id = "displayPhotoButton"
	Button displayPhotoButton;
	/**
	 * The UI container for the large image display.
	 */
	@FXML // fx:id = "largeDisplay"
	ImageView largeDisplay;
	/**
	 * The UI anchor for the large ImageView.
	 */
	@FXML // fx:id = "largeAnchor"
	AnchorPane largeAnchor;
	/**
	 * A button to move to the next photo.
	 */
	@FXML // fx:id = "nextButton"
	Button nextButton;
	/**
	 * A button to move to the previous photo.
	 */
	@FXML // fx:id = "prevButton"
	Button prevButton;
	/**
	 * The name label for the photo's caption.
	 */
	@FXML // fx:id = "captionLabel"
	Label captionLabel;
	/**
	 * The UI textfield that contains the caption for the selected photo.
	 */
	@FXML // fx:id = "captionTextField"
	TextField captionTextField;
	/**
	 * Button to return to the user's homescreen.
	 */
	@FXML // fx:id = "returntoAlbums"
	Button returntoAlbums;
	/**
	 * Button to make a new album from the search results.
	 */
	@FXML // fx:id = "makeAlbumButton"
	Button makeAlbumButton;
	/**
	 * UI container for displaying tags.
	 */
	@FXML // fx:id = "tagVbox"
	VBox tagVbox;
	/**
	 * UI container for the tag display.
	 */
	@FXML // fx:id = "tagPane"
	Pane tagPane;
	/**
	 * Label for the tag display.
	 */
	@FXML // fx:id = "tagHeader"
	Label tagHeader;
	/**
	 * UI container for the photo date information.
	 */
	@FXML // fx:id = "dateBox"
	HBox dateBox;

	/**
	 * Button to logout of the current user session.
	 */
	@FXML // fx:id ="logoutButton"
	Button logoutButton;

	/**
	 * The method to start the search controller and perform the search.
	 *
	 * @param mainStage
	 * 		mainstage to start SearchController
	 */
	@FXML
	public void start(Stage mainStage) {
		mainStage.setTitle("Search");
		mainStage.setResizable(false);

		photoImages = new ArrayList<ImageView>();
		photoImageMap = new HashMap<ImageView, Photo>();
		search();
		flows.setVgap(20);
		flows.setHgap(20);
		scroll.setBorder(new Border(
				new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		refreshPhotoGallery();

		mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				// write user to serialization file;
				try {
					FileOutputStream f_out = new FileOutputStream("data/dummy.txt");
					ObjectOutputStream obj_out = new ObjectOutputStream(f_out);

					for (User u : PhotoAlbum.userList.users) {
						obj_out.writeObject(u);
					}

					obj_out.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}

	/**
	 * A mouse handler to provide the functionality of selecting a photo via
	 * mouse click.
	 */
	EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent t) {

			if (displayPhotoButton.isDisable() == true) {
				displayPhotoButton.setDisable(false);
			}

			ImageView imageView = (ImageView) t.getSource();
			selectedImageObject = imageView.getImage();
			selectedImage = imageView;
			VBox v = (VBox) imageView.getParent();
			// if it is a new selection, get rid of the border on the old
			// selection
			if (!(selectedImageBox == null) && !selectedImageBox.equals(v)) {
				selectedImageBox.setStyle("-fx-padding: 0;" + "-fx-border-style: none;");
			}
			v.setStyle("-fx-padding: 1;" + "-fx-border-style: solid inside;" + "-fx-border-width: 1;"
					+ "-fx-border-insets: 1;" + "-fx-border-radius: 1;" + "-fx-border-color: blue;");
			selectedImageBox = v;
			selectedPhoto = photoImageMap.get(imageView);
			// show the photo caption
			captionLabel.setVisible(true);
			captionTextField.setVisible(true);
			dateBox.setVisible(true);
			TextField date = (TextField) dateBox.getChildren().get(1);
			String result = null;
			SimpleDateFormat dateForm = new SimpleDateFormat("MM/dd/yyyy");
			result = dateForm.format(selectedPhoto.date.getTime());
			date.setText(result);
			captionTextField.setText(selectedPhoto.caption);

			if (selectedPhoto.tags.size() > 0) {
				tagPane.setVisible(true);
				tagHeader.setVisible(true);
				refreshTags();
			} else {
				tagPane.setVisible(false);
				tagHeader.setVisible(false);
				tagVbox.getChildren().clear();
			}
		}
	};

	/**
	 * A method to handle returning to the user's homescreen. Loads the
	 * HomeController FXML and controller.
	 *
	 * @param e
	 * 		ActionEvent e
	 * @throws IOException
	 * 		Exception for input and output
	 */
	@FXML
	public void returnToAlbumsHandler(ActionEvent e) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/view/home.fxml"));
		Scene homeScene = new Scene((Parent) loader.load());

		HomeController homeController = loader.getController();
		homeController.setUser(user);
		// set the user for the homeController
		homeController.start(PhotoAlbum.primaryStage);

		PhotoAlbum.primaryStage.setScene(homeScene);
		PhotoAlbum.primaryStage.show();
	}

	/**
	 * Resets the tag display to the current state.
	 */
	public void refreshTags() {
		tagVbox.getChildren().clear();
		for (String key : selectedPhoto.tags.keySet()) {
			for (String value : selectedPhoto.tags.get(key)) {
				addTag(key, value);
			}
		}
	}

	/**
	 * Creates a new UI tag display for a new key-value pair.
	 *
	 * @param key
	 * 		key for tag
	 * @param value
	 * 		value for tag
	 */
	public void addTag(String key, String value) {
		HBox h = new HBox();
		h.setAlignment(Pos.CENTER);
		Label keyLbl = new Label();
		keyLbl.setText(key + ": ");
		keyLbl.setFont(Font.font(13));
		TextField valueFld = new TextField();
		valueFld.setEditable(false);
		valueFld.setText(value);
		valueFld.setPrefWidth(100);

		h.getChildren().addAll(keyLbl, valueFld);
		tagVbox.getChildren().add(h);
		// make sure tag pane is visible
		tagPane.setVisible(true);

	}

	/**
	 * Handles the popup large display of the photos and allows for manual
	 * slideshow.
	 *
	 * @param e
	 * 		ActionEvent e
	 */
	@FXML
	public void displayPhotoHandler(ActionEvent e) {

		ImageView i = new ImageView();
		i.setFitHeight(500);
		i.setFitWidth(700);
		i.setImage(selectedImageObject);
		int index = photoImages.indexOf(selectedImage);
		final Stage dialog = new Stage();
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.initOwner(primaryStage);
		dialog.setResizable(false);
		HBox outer = new HBox();
		VBox imageBox = new VBox();
		VBox tagBox = new VBox();
		outer.setAlignment(Pos.CENTER);
		outer.getChildren().addAll(imageBox, tagBox);
		imageBox.getChildren().add(i);
		imageBox.setAlignment(Pos.CENTER);

		// tagBox elements
		Label tagHead = new Label("Tags");
		tagHead.setFont(Font.font(20));
		tagBox.getChildren().add(tagHead);
		for (String key : selectedPhoto.tags.keySet()) {
			for (String value : selectedPhoto.tags.get(key)) {
				HBox h = new HBox();
				h.setAlignment(Pos.CENTER);
				Label keyLbl = new Label(key + ": ");
				Label valLbl = new Label(value);
				h.getChildren().addAll(keyLbl, valLbl);
				tagBox.getChildren().add(h);
			}
		}
		HBox dateBox = new HBox();
		dateBox.setAlignment(Pos.CENTER);
		Label dateLbl = new Label("Date of Photo:");
		TextField dateFld = new TextField();
		dateFld.setPrefWidth(100);
		dateFld.setEditable(false);
		String result = null;
		SimpleDateFormat dateForm = new SimpleDateFormat("MM/dd/yyyy");
		result = dateForm.format(selectedPhoto.date.getTime());
		dateFld.setText(result);
		dateBox.getChildren().addAll(dateLbl, dateFld);
		tagBox.getChildren().add(dateBox);
		Scene dialogScene = new Scene(outer, 900, 550);
		dialog.setTitle(selectedPhoto.name);
		HBox b = new HBox();
		nextButton = new Button();
		nextButton.setAlignment(Pos.BOTTOM_CENTER);
		nextButton.setText("Next");
		prevButton = new Button();
		prevButton.setAlignment(Pos.BOTTOM_CENTER);
		prevButton.setText("Prev");
		HBox captBox = new HBox();
		captBox.setAlignment(Pos.CENTER);
		Label captLbl = new Label("Caption: ");
		TextField captFld = new TextField();
		captFld.setEditable(false);
		captFld.setPrefWidth(150);
		captFld.setText(selectedPhoto.caption);
		captBox.getChildren().addAll(captLbl, captFld);

		if (index == 0) {
			prevButton.setDisable(true);
		}

		if (index == photoImages.size() - 1) {
			nextButton.setDisable(true);
		}

		nextButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				prevButton.setDisable(false);
				int currIndex = photoImages.indexOf(selectedImage);
				if (currIndex < photoImages.size() - 1) {

					i.setImage(photoImages.get(currIndex + 1).getImage());
					Photo curr = photoImageMap.get(photoImages.get(currIndex + 1));
					captFld.setText(curr.caption);
					tagBox.getChildren().clear();
					if (curr.tags.size() != 0) {
						Label tagHead = new Label("Tags");
						tagHead.setFont(Font.font(20));
						tagBox.getChildren().add(tagHead);
						for (String key : curr.tags.keySet()) {
							for (String value : curr.tags.get(key)) {
								HBox h = new HBox();
								h.setAlignment(Pos.CENTER);
								Label keyLbl = new Label(key + ": ");
								Label valLbl = new Label(value);
								h.getChildren().addAll(keyLbl, valLbl);
								tagBox.getChildren().add(h);
							}
						}
					}
					HBox dateBox = new HBox();
					dateBox.setAlignment(Pos.CENTER);
					Label dateLbl = new Label("Date of Photo:");
					TextField dateFld = new TextField();
					dateFld.setPrefWidth(100);
					dateFld.setEditable(false);
					String result = null;
					SimpleDateFormat dateForm = new SimpleDateFormat("MM/dd/yyyy");
					result = dateForm.format(curr.date.getTime());
					dateFld.setText(result);
					dateBox.getChildren().addAll(dateLbl, dateFld);
					tagBox.getChildren().add(dateBox);

					selectedImage = photoImages.get(currIndex + 1);
				} else {
					nextButton.setDisable(true);
				}
			}

		});
		prevButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				nextButton.setDisable(false);
				int currIndex = photoImages.indexOf(selectedImage);
				if (currIndex > 0) {
					i.setImage(photoImages.get(currIndex - 1).getImage());

					Photo curr = photoImageMap.get(photoImages.get(currIndex - 1));
					captFld.setText(curr.caption);
					tagBox.getChildren().clear();
					if (curr.tags.size() != 0) {
						Label tagHead = new Label("Tags");
						tagHead.setFont(Font.font(20));
						tagBox.getChildren().add(tagHead);
						for (String key : curr.tags.keySet()) {
							for (String value : curr.tags.get(key)) {
								HBox h = new HBox();
								h.setAlignment(Pos.CENTER);
								Label keyLbl = new Label(key + ": ");
								Label valLbl = new Label(value);
								h.getChildren().addAll(keyLbl, valLbl);
								tagBox.getChildren().add(h);
							}
						}
					}
					HBox dateBox = new HBox();
					dateBox.setAlignment(Pos.CENTER);
					Label dateLbl = new Label("Date of Photo:");
					TextField dateFld = new TextField();
					dateFld.setPrefWidth(100);
					dateFld.setEditable(false);
					String result = null;
					SimpleDateFormat dateForm = new SimpleDateFormat("MM/dd/yyyy");
					result = dateForm.format(curr.date.getTime());
					dateFld.setText(result);
					dateBox.getChildren().addAll(dateLbl, dateFld);
					tagBox.getChildren().add(dateBox);
					selectedImage = photoImages.get(currIndex - 1);
				} else {
					prevButton.setDisable(true);
				}
			}
		});

		b.getChildren().addAll(prevButton, nextButton);
		b.setAlignment(Pos.CENTER);
		imageBox.getChildren().addAll(b, captBox);
		dialog.setScene(dialogScene);
		dialog.show();
		refreshPhotoGallery();
	}

	/**
	 * This method contains the logic to perform a search of all of the users
	 * photos based on the dates and tag filters provided via the
	 * HomeController.
	 */
	private void search() {
		searchResults = new ArrayList<Photo>();

		// if we are searching tags, this covers this case as well as tags and
		// dates
		if (searchTags.size() != 0) {
			for (Photo p : user.getPhotos()) {
				for (String key : searchTags.keySet()) {
					for (String value : searchTags.get(key)) {
						// check if current photo has same tag
						if (p.tags != null && p.tags.get(key) != null && p.tags.get(key).contains(value)) {
							// check if we are looking for specific dates
							if (fromCal != null) {
								// confirm that photo date is b/w fromCal and
								// toCal
								if (p.date.compareTo(fromCal) >= 0 && p.date.compareTo(toCal) <= 0) {
									searchResults.add(p);
								}
							} else {
								searchResults.add(p);
							}
						}
					}
				}
			}
			refreshPhotoGallery();
			if (searchResults.size() == 0) {
				try {
					noResultsHandler();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return;
		}
		// last case: only searching dates
		else {
			for (Photo p : user.getPhotos()) {
				if (p.date.compareTo(fromCal) >= 0 && p.date.compareTo(toCal) <= 0) {
					searchResults.add(p);
				}
			}
		}
		refreshPhotoGallery();
		if (searchResults.size() == 0) {
			try {
				noResultsHandler();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return;
	}

	/**
	 * Resets the photo display based on the current state of the photo list.
	 */
	public void refreshPhotoGallery() {
		// clear all of the FlowPane nodes
		flows.getChildren().clear();
		if (searchResults == null) {
			return;
		}
		photoImages.clear();
		photoImageMap.clear();
		for (Photo photo : searchResults) {
			VBox v = new VBox();
			ImageView i = new ImageView();
			i.setOnMouseClicked(mouseHandler);
			Label name = new Label();
			name.setText(photo.getName().substring(0, photo.getName().length() - 4));
			name.setTextAlignment(TextAlignment.CENTER);
			photoImages.add(i);
			photoImageMap.put(i, photo);
			i.setImage(new Image("file:" + photo.path));
			i.setFitHeight(75);
			i.setFitWidth(75);
			v.getChildren().add(i);
			v.getChildren().add(name);
			flows.getChildren().add(v);
		}

	}

	/**
	 * If the search returns no results, this method triggers an alert which
	 * informs the user and returns them to their homescreen.
	 *
	 * @throws IOException
	 * 		Exception for input and output
	 */
	public void noResultsHandler() throws IOException {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Empty Search");
		alert.setHeaderText("No Matches Found");
		alert.setContentText("No photos were found with given search criteria. Please try again");
		alert.show();
		alert.setOnCloseRequest(new EventHandler<DialogEvent>() {
			public void handle(DialogEvent d) {
				try {
					FXMLLoader loader = new FXMLLoader();
					loader.setLocation(getClass().getResource("/view/home.fxml"));
					Scene homeScene = new Scene((Parent) loader.load());

					HomeController homeController = loader.getController();
					homeController.setUser(user);
					// set the user for the homeController
					homeController.start(PhotoAlbum.primaryStage);

					PhotoAlbum.primaryStage.setScene(homeScene);
					PhotoAlbum.primaryStage.show();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * Getter method for the current user.
	 *
	 * @return
	 * 		return the current user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Setter method for the current user.
	 *
	 * @param user
	 * 		set the current user
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Creates a new album using the photos returned as search results. Does not
	 * modify in any way the photos from the original location.
	 *
	 * @param e
	 * 		ActionEvent e
	 * @throws IOException
	 * 		Exception for input and output
	 */
	@FXML
	public void makeAlbumHandler(ActionEvent e) throws IOException {
		TextInputDialog dialog = new TextInputDialog("Name Goes Here");
		dialog.setTitle("New Album");
		dialog.setHeaderText("Create and open a new album from your search results");
		dialog.setContentText("New album name:");

		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			String name = result.get();
			if (user.getAlbum(name) != null) {
				Alert a = new Alert(AlertType.ERROR);
				a.setTitle("Album name already exists");
				a.setContentText("Please choose a unique name for your new album");
				a.show();
				return;
			}
			user.addAlbum(name);
			Album a = user.getAlbum(name);
			for (Photo p : searchResults) {
				Photo newP = new Photo(p.path, p.name);
				if(p.tags!=null){
					for(String key: p.tags.keySet()){
						ArrayList<String> list = p.tags.get(key);
						newP.tags.put(key, new ArrayList<String>());
						for(String value:list){
							newP.tags.get(key).add(new String(value));
						}
					}
				}
				newP.date = p.date;
				if(p.caption!=null){
					newP.caption = new String(p.caption);
				}
				a.addPhoto(newP, user);
			}

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/view/album.fxml"));
			Scene homeScene = new Scene((Parent) loader.load());

			AlbumController albumController = loader.getController();
			AlbumController.setUser(user);
			AlbumController.setAlbum(a);
			albumController.start(PhotoAlbum.primaryStage);

			PhotoAlbum.primaryStage.setScene(homeScene);
			PhotoAlbum.primaryStage.show();
		}
	}

	/**
	 * Handles the request to logout of the current user session.
	 *
	 * @param e
	 * 		ActionEvent e
	 * @throws IOException
	 * 		Exception for input and output
	 */
	@FXML
	public void logoutHandler(ActionEvent e) throws IOException {

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
