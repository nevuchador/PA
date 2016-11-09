package controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
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
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import models.PhotoAlbum;
import models.utilities.Album;
import models.utilities.Photo;
import models.utilities.User;
/**
 * Provides a controller for the UI and logic of the "album screen" of a user.
 * From here they can see photo albums, and they can add captions and tag
 * In addition, they can move a photo to other albums
 *
 * @author Michael Alweis
 * @author Subin Ahn
 *
 */
public class AlbumController {
	/**
	 * The current user.
	 */
	private static User user;
	/**
	 * The current user album.
	 */
	private static Album album;
	/**
	 * The current UI Stage.
	 */
	protected static Stage primaryStage;
	/**
	 * A list of the UI ImageView containers for the photos in the current
	 * album.
	 */
	protected ArrayList<ImageView> photoImages;
	/**
	 * A map from the ImageView containers to their corresponding Photo objects.
	 */
	protected HashMap<ImageView, Photo> photoImageMap;
	/**
	 * The currently selected Photo.
	 */
	protected Photo selectedPhoto;
	/**
	 * The UI container containing the selected ImageView.
	 */
	protected VBox selectedImageBox;
	/**
	 * The label containing the name of the selected Photo.
	 */
	protected Label selectedLabel;
	/**
	 * The selected ImageView container.
	 */
	protected ImageView selectedImage;
	/**
	 * The selected Image object, which corresponds directly to a photo file.
	 */
	protected Image selectedImageObject;
	/**
	 * A popup window.
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
	 * A button for adding a photo to the album.
	 */
	@FXML // fx:id = "addPhotoButton"
	Button addPhotoButton;
	/**
	 * A button for renaming the selected photo.
	 */
	@FXML // fx:id = "renameButton"
	Button renamePhotoButton;
	/**
	 * A button for deleting the selected photo.
	 */
	@FXML // fx:id = "deletePhotoButton"
	Button deletePhotoButton;
	/**
	 * A button for displaying a large view of the selected photo.
	 */
	@FXML // fx:id = "displayPhotoButton"
	Button displayPhotoButton;
	/**
	 * The container for the large version of the selected image in display
	 * mode.
	 */
	@FXML // fx:id = "largeDisplay"
	ImageView largeDisplay;
	/**
	 * The container for the popup display window.
	 */
	@FXML // fx:id = "largeAnchor"
	AnchorPane largeAnchor;
	/**
	 * A button to display the next photo in the album.
	 */
	@FXML // fx:id = "nextButton"
	Button nextButton;
	/**
	 * A button to display the previous photo in the album.
	 */
	@FXML // fx:id = "prevButton"
	Button prevButton;
	/**
	 * A label for the photo caption.
	 */
	@FXML // fx:id = "captionLabel"
	Label captionLabel;
	/**
	 * A field to hold the caption of the current photo.
	 */
	@FXML // fx:id = "captionTextField"
	TextField captionTextField;
	/**
	 * A button to change the caption of the selected photo.
	 */
	@FXML // fx:id = "updateCaption"
	Button updateCaption;
	/**
	 * A button to return to the user's homescreen.
	 */
	@FXML // fx:id = "returntoAlbums"
	Button returntoAlbums;
	/**
	 * A button to move the selected photo to a different album.
	 */
	@FXML // fx:id = "moveAlbumButton"
	Button moveAlbumButton;
	/**
	 * A menu with the names of the possible tag keys.
	 */
	@FXML // fx:id = "addTagMenu"
	MenuButton addTagMenu;
	/**
	 * A field to hold the user's input for a new tag value.
	 */
	@FXML // fx:id = "addTagField"
	TextField addTagField;
	/**
	 * UI container for displaying tags.
	 */
	@FXML // fx:id = "tagVbox"
	VBox tagVbox;
	/**
	 * Button to add tag to current photo.
	 */
	@FXML // fx:id = "addTagButton"
	Button addTagButton;
	/**
	 * UI container for the tag menu.
	 */
	@FXML // fx:id = "menuPane"
	Pane menuPane;
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
	 * Button to log of the current user's session.
	 */
	@FXML // fx:id = "logoutAlbumButton"
	Button logoutAlbumButton;

	/**
	 * Method to start the controller.
	 * @param mainStage
	 * 		mainstage to start AlbumController
	 */
	@FXML
	public void start(Stage mainStage) {
		mainStage.setTitle("MyPhotos - Album View");
		mainStage.setResizable(false);

		photoImages = new ArrayList<ImageView>();
		photoImageMap = new HashMap<ImageView, Photo>();

		flows.setVgap(20);
		flows.setHgap(20);
		scroll.setBorder(new Border(
				new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		refreshPhotoGallery();
		menuInitializer();
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
	 * Getter method for the current album.
	 *
	 * @return
	 * 		return the current album
	 */
	public static Album getAlbum() {
		return album;
	}

	/**
	 * Setter method for the current album.
	 *
	 * @param album
	 * 		set the current album
	 */
	public static void setAlbum(Album album) {
		AlbumController.album = album;
	}

	/**
	 * Getter method for the current user.
	 *
	 * @return
	 * 		return the current user
	 */
	public static User getUser() {
		return user;
	}

	/**
	 * Setter method for the current user.
	 *
	 * @param user
	 * 		set the current user
	 */
	public static void setUser(User user) {
		AlbumController.user = user;
	}

	/**
	 * Handler method to add a new photo to the current album.
	 *
	 * @param e
	 * 		ActionEvent e
	 */
	public void addPhotoHandler(ActionEvent e) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose Photo File");
		File photoFile = fileChooser.showOpenDialog(primaryStage);
		if (photoFile == null) {
			return;
		}
		String path = photoFile.getPath();
		String name = photoFile.getName();
		album.addPhoto(path, name, user, photoFile);
		refreshPhotoGallery();
		tagHeader.setVisible(false);
		tagPane.setVisible(false);
		menuPane.setVisible(false);
		captionLabel.setVisible(false);
		captionTextField.setVisible(false);
		dateBox.setVisible(false);
	}

	/**
	 * Handler method for renaming the currently selected photo and its
	 * associated system file.
	 *
	 * @param e
	 * 		ActionEvent e
	 */
	public void renamePhotoHandler(ActionEvent e) {

		Dialog<User> d = new Dialog<User>();
		d.setTitle("Rename Photo");
		d.setHeaderText("Only put the name, not the file type/extension\n");

		Label photoName = new Label("New Name ");
		TextField t1 = new TextField();

		GridPane grid = new GridPane();
		grid.add(photoName, 1, 1);
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
						boolean b = album.renamePhoto(selectedPhoto, name);
						if (!b) {
							Alert alert = new Alert(AlertType.INFORMATION);
							alert.setTitle("Error Renaming Photo");
							alert.setHeaderText("Existing photo name, or added extension at end of name");
							alert.setContentText("To rename a photo, it must have a unique name");
							alert.show();
						}
					}
				}

				return null;
			}
		});
		Optional<User> result = d.showAndWait();
		refreshPhotoGallery();
		tagHeader.setVisible(false);
		tagPane.setVisible(false);
		menuPane.setVisible(false);
		captionLabel.setVisible(false);
		captionTextField.setVisible(false);
		dateBox.setVisible(false);
	}

	/**
	 * Method for deleting a photo. Also handles deleting the file associated
	 * with it.
	 *
	 * @param e
	 * 		ActionEvent e
	 */
	public void deletePhotoHandler(ActionEvent e) {

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Delete Confirmation");
		alert.setHeaderText("You are about to remove this photo.");
		alert.setContentText("Are you sure you want to delete this photo?");

		Optional<ButtonType> result = alert.showAndWait();
		if ((result.get() == ButtonType.OK)) {
			alert.close();
			String name = selectedPhoto.getName();
			int i = album.Photoindexer(name);
			album.delPhoto(i, name, user);
			user.delPhoto(i);

		} else {
			alert.close();
		}

		refreshPhotoGallery();
		renamePhotoButton.setDisable(true);
		deletePhotoButton.setDisable(true);
		displayPhotoButton.setDisable(true);
		moveAlbumButton.setDisable(true);
		tagHeader.setVisible(false);
		tagPane.setVisible(false);
		menuPane.setVisible(false);
		captionLabel.setVisible(false);
		captionTextField.setVisible(false);
		dateBox.setVisible(false);
	}

	/**
	 * Method to re-populate the gallery of images. This method is called after
	 * performing an action that affects the list of photos, such as deleting a
	 * photo.
	 */
	public void refreshPhotoGallery() {
		// clear all of the FlowPane nodes
		flows.getChildren().clear();
		if (album.getPhotos() == null) {
			return;
		}
		photoImages.clear();
		photoImageMap.clear();
		for (Photo photo : album.getPhotos()) {
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
	 * Handles selection via mouseclick of a photo.
	 */
	EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent t) {

			if (renamePhotoButton.isDisable() == true) {
				renamePhotoButton.setDisable(false);
			}
			if (displayPhotoButton.isDisable() == true) {
				displayPhotoButton.setDisable(false);
			}
			if (deletePhotoButton.isDisable() == true) {
				deletePhotoButton.setDisable(false);
			}
			if (moveAlbumButton.isDisable() == true && user.getAlbums().size() > 1) {
				moveAlbumButton.setDisable(false);
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
			updateCaption.setVisible(true);
			captionTextField.setText(selectedPhoto.caption);
			menuPane.setVisible(true);
			dateBox.setVisible(true);
			TextField dateFld = (TextField) dateBox.getChildren().get(1);
			String result = null;
			SimpleDateFormat dateForm = new SimpleDateFormat("MM/dd/yyyy");
			result = dateForm.format(selectedPhoto.date.getTime());
			dateFld.setText(result);

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
	 * This method handles the request to display a photo. It opens a new
	 * window, and display a large view of the selected photo along with its
	 * details. Furthermore, the window allows for a manual slideshow of all of
	 * the photos in the album.
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
	 * Saves the current value of the caption field.
	 *
	 * @param e
	 * 		ActionEvent e
	 */
	@FXML
	public void updateCaptionHandler(ActionEvent e) {
		selectedPhoto.caption = captionTextField.getText();
	}

	/**
	 * This method returns the user to their homescreen and loads the relevant
	 * controller.
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
	 * A method to handle moving the selected photo to a different album, while
	 * also deleting it from the current album.
	 *
	 * @param e
	 * 		ActionEvent e
	 */
	@FXML
	public void moveToAnotherAlbumHandler(ActionEvent e) {
		Dialog<User> d = new Dialog<User>();
		d.setTitle("Move Photo");
		d.setHeaderText("Move " + selectedPhoto.getName() + " to another album");
		int row = 2;
		final ToggleGroup group = new ToggleGroup();
		RadioButton rb;
		GridPane grid = new GridPane();
		Label label = new Label("Album Name");
		grid.add(label, 1, 1);
		for (Album a : user.getAlbums()) {
			if (!a.getName().equals(album.getName())) {
				rb = new RadioButton(a.getName());
				rb.setUserData(a.getName());
				rb.setToggleGroup(group);
				rb.setSelected(true);
				grid.add(rb, 1, row);
				row++;
			}

		}

		d.getDialogPane().setContent(grid);

		ButtonType okButton = new ButtonType("Move", ButtonData.OK_DONE);

		ButtonType cancelButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		d.getDialogPane().getButtonTypes().add(okButton);
		d.getDialogPane().getButtonTypes().add(cancelButton);

		d.setResultConverter(new Callback<ButtonType, User>() {
			@Override
			public User call(ButtonType bType) {
				if (bType == okButton) {
					// get name of the selected album
					String name = group.getSelectedToggle().getUserData().toString();

					// get album with that name
					Album target = null;
					for (Album a : user.getAlbums()) {
						if (a.getName().equals(name)) {
							target = a;
						}
					}
					if (target != null) {

						// access the file
						Path path1 = Paths.get(selectedPhoto.path);
						File orig = path1.toFile();
						// move the photo file to the correct folder
						Photo p = selectedPhoto;
						Photo newP = new Photo(p.path, p.name);
						if (p.tags != null) {
							for (String key : p.tags.keySet()) {
								ArrayList<String> list = p.tags.get(key);
								newP.tags.put(key, new ArrayList<String>());
								for (String value : list) {
									newP.tags.get(key).add(new String(value));
								}
							}
						}
						newP.date = p.date;
						if (p.caption != null) {
							newP.caption = new String(p.caption);
						}
						boolean b = target.addPhoto(newP, user);
						// delete the Photo from this album
						if (b) {
							int i = album.Photoindexer(selectedPhoto.getName());
							album.delPhoto(i, selectedPhoto.getName(), user);
							user.delPhoto(i);
							captionTextField.setVisible(false);
						} else {
							Alert alert = new Alert(AlertType.INFORMATION);
							alert.setTitle("Error Moving Photo");
							alert.setHeaderText("Same photo name exists in the album");
							alert.setContentText("Change photo name, or move the photo to another album");
							alert.show();
							return null;
						}
					}
				}
				return null;
			}
		});
		Optional<User> result = d.showAndWait();
		refreshPhotoGallery();
		if (album.getSize() == 0) {
			renamePhotoButton.setDisable(true);
			deletePhotoButton.setDisable(true);
			displayPhotoButton.setDisable(true);
			moveAlbumButton.setDisable(true);
		}
		tagHeader.setVisible(false);
		tagPane.setVisible(false);
		menuPane.setVisible(false);
		captionLabel.setVisible(false);
		captionTextField.setVisible(false);
		dateBox.setVisible(false);
	}

	/**
	 * A method to set up the menu of possible tag key values.
	 */
	public void menuInitializer() {
		addTagMenu.getItems().clear();
		for (String s : Photo.possibleTags) {
			MenuItem m = new MenuItem(s);
			m.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent t) {
					addTagMenu.setText(s);
					addTagButton.setDisable(false);
				}
			});
			addTagMenu.getItems().addAll(m);
		}

	}

	/**
	 * Handler method for the 'add tag' button. Adds a key-value pair to the
	 * tags HashMap
	 *
	 * @param e
	 * 		ActionEvent e
	 */
	public void addTagHandler(ActionEvent e) {
		if (addTagField.getText().isEmpty()) {
			return;
		} else {
			// create a new key-value pair in the photo's tags

			String key = addTagMenu.getText();
			String value = addTagField.getText();
			if (selectedPhoto.tags.get(key) == null) {
				tagHeader.setVisible(true);
				selectedPhoto.tags.put(key, new ArrayList<String>());
				selectedPhoto.tags.get(key).add(value);
				addTag(key, value);
			} else if (!selectedPhoto.tags.get(key).contains(value)) {
				tagHeader.setVisible(true);
				selectedPhoto.tags.get(key).add(value);
				addTag(key, value);
			}

		}
	}

	/**
	 * This method adds the necessary UI elements to add a tag to the display.
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
		valueFld.setText(value);
		valueFld.setPrefWidth(100);
		MenuButton updateTag = new MenuButton("Edit/Delete");

		MenuItem delete = new MenuItem("Delete");
		delete.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				if (selectedPhoto.tags.get(key).size() == 0) {
					selectedPhoto.tags.remove(key);
				} else {
					selectedPhoto.tags.get(key).remove(value);
				}
				refreshTags();
			}
		});
		MenuItem update = new MenuItem("Update");
		update.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				if (valueFld.getText().isEmpty()) {
					return;
				} else {
					if (selectedPhoto.tags.get(key).size() == 0) {
						selectedPhoto.tags.remove(key);
						selectedPhoto.tags.put(key, new ArrayList<String>());
						selectedPhoto.tags.get(key).add(valueFld.getText());
					} else {
						selectedPhoto.tags.get(key).remove(value);
						selectedPhoto.tags.get(key).add(valueFld.getText());
					}
				}
				refreshTags();
			}
		});

		updateTag.showingProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue) {
					if (value.equals(valueFld.getText())) {
						update.setDisable(true);
						delete.setDisable(false);
					} else {
						update.setDisable(false);
						delete.setDisable(true);
					}

				}

			}

		});
		updateTag.getItems().addAll(update, delete);

		h.getChildren().addAll(keyLbl, valueFld, updateTag);
		tagVbox.getChildren().add(h);
		// make sure tag pane is visible
		tagPane.setVisible(true);
		// empty tag text
		addTagField.clear();
		addTagMenu.setText("Tag");
	}

	/**
	 * This method resets all of the tags in the display to their current values
	 * based on the current state of the tags HashMap.
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
	 * Handles the request to logout of the current user session.
	 *
	 * @param e
	 * 		ActionEvent e
	 * @throws IOException
	 * 		Exception for input and output
	 */
	@FXML
	public void logoutAlbumHandler(ActionEvent e) throws IOException {

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
