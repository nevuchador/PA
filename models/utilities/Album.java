package models.utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Provides a model for an album of photographs, implemented mainly as an
 * ArrayList of Pictures
 *
 * @author Michael Alweis
 * @author Subin Ahn
 *
 */
public class Album implements java.io.Serializable {
	/**
	 * The UID for the object.
	 */
	private static final long serialVersionUID = 2969370883501154554L;
	/**
	 * The name of the album.
	 */
	private String name;
	/**
	 * A list of the photos contained within this album.
	 */
	private ArrayList<Photo> photos;

	/**
	 * Constructor for the album given a name.
	 *
	 * @param name
	 * 		construct the album
	 */
	public Album(String name) {
		this.setName(name);
		this.setPhotos(new ArrayList<Photo>());
	}

	/**
	 * Getter method for the album name
	 *
	 * @return the album's name.
	 * 		get the album name
	 */
	public String getName() {
		return name;
	}

	/**
	 * returns the number of photos in the album.
	 *
	 * @return
	 * 		return the size of photo
	 */
	public int getSize() {
		return this.photos.size();
	}

	/**
	 * Setter method for the album's name.
	 *
	 * @param name
	 * 		set the album name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Getter method for the list of the album's photos.
	 *
	 * @return photos
	 * 		return the arrayList containing the photos.
	 */
	public ArrayList<Photo> getPhotos() {
		return photos;
	}

	/**
	 * Setter method for the album's photo list.
	 *
	 * @param photos
	 * 		set the album's photo list
	 */
	public void setPhotos(ArrayList<Photo> photos) {
		this.photos = photos;
	}

	/**
	 * Deletes a photo from the album's list, as well as the file associated
	 * with the Photo object.
	 *
	 * @param i
	 * 		index of photo
	 * @param name
	 * 		name of photo
	 * @param user
	 * 		user name
	 */
	public void delPhoto(int i, String name, User user) {
		this.getPhotos().remove(i);
		String photopath = "data/users/" + user.getName() + "/" + this.getName() + "/" + name;
		File PhotoFile = new File(photopath);
		PhotoFile.delete();
	}

	/**
	 * Finds the index of a photo with a given name.
	 *
	 * @param name
	 * 		the photo name
	 * @return
	 * 		return index of the photo
	 */
	public int Photoindexer(String name) {

		for (int i = 0; i < this.photos.size(); i++) {
			if (photos.get(i) != null && photos.get(i).getName().equals(name)) {

				return i;
			}
		}
		return -1;
	}

	/**
	 * Allows for renaming a photo
	 *
	 * @param p
	 * 		the photo to be rename
	 * @param name
	 * 		the photo name
	 * @return
	 * 		if the name exist then return false. otherwise return true
	 */
	public boolean renamePhoto(Photo p, String name) {

		for (Photo curr : this.getPhotos()) {
			if (curr.getName().equals(name)) {
				return false;
			}
		}
		String newName = name+".jpg";
		File dir = new File(p.path);
		String newPath = dir.getParent() +"/"+ newName;
		File newDir = new File(newPath);

		if(newDir.exists()){return false;}
		boolean b =  dir.renameTo(newDir);
		if(b){
			p.setName(newName);
			p.path = newPath;
			return true;
		}
		return false;
	}

	/**
	 * Adds a photo to the current album and creates a new file for the image in
	 * the appropriate directory within the project space.
	 *
	 * @param path
	 * 		path of photo
	 * @param name
	 * 		the photo name
	 * @param user
	 * 		user
	 * @param orig
	 * 		the original file path
	 * @return 
	 * 		a boolean flag for whether the addition took place.
	 */
	public boolean addPhoto(String path, String name, User user, File orig) {
		String newFilePath = "data/users/" + user.getName() + "/" + this.getName() + "/" + name;
		Photo p = new Photo(newFilePath, name);

		try {
			Files.copy(Paths.get(path), Paths.get(newFilePath));
			Path path1 = Paths.get(newFilePath);
			File f = path1.toFile();
			p.path = newFilePath;
		} catch (IOException e) {
			return false;
		}

		this.getPhotos().add(p);
		// add the date to the photo
		long noFormatDate = orig.lastModified();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(noFormatDate);
		cal.set(Calendar.MILLISECOND, 0);
		p.date = cal;
		user.getPhotos().add(p);
		return true;
	}

	/**
	 * Add photo method used when moving an existing photo into a different
	 * album.
	 *
	 * @param photo
	 * 		the photo to be add
	 * @param user
	 * 		the user
	 * @return  
	 *		a boolean flag for whether the addition took place.
	 */
	public boolean addPhoto(Photo photo, User user) {
		String origPath = photo.path;
		String newPath = "data/users/" + user.getName() + "/" + this.getName() + "/" + photo.getName();

		try {
			Files.copy(Paths.get(origPath), Paths.get(newPath));
			Path path1 = Paths.get(newPath);
			File f = path1.toFile();
			photo.path = newPath;
		} catch (IOException e) {
			return false;
		}
		this.getPhotos().add(photo);
		user.getPhotos().add(photo);
		return true;
	}
}
