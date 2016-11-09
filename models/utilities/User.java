package models.utilities;

import java.io.*;
import java.util.ArrayList;
/**
 * Provides a model for a user of the application, including data such as the user's
 * login information, as well as all of their photos and albums.
 *
 * @author Michael Alweis
 * @author Subin Ahn
 *
 */
public class User implements java.io.Serializable {

	/**
	 * The user's login name.
	 */
	private String userName;
	/**
	 * The user's login password.
	 */
	private String password;
	/**
	 * The user's directory in the project space where all albums and photo files are stored.
	 */
	private File dir;
	/**
	 * A list of all albums associated with the user.
	 */
	private ArrayList<Album> albums;
	/**
	 * A list of all photos associated with the user.
	 */
	private ArrayList<Photo> photos;
	public static String workDir = System.getProperty("user.dir");
	/**
	 * Constructor method, taking the username and password as arguments.
	 * @param user
	 * 		username
	 * @param pw
	 * 		password
	 */
	public User(String user, String pw) {

		this.userName = user;
		this.setPassword(pw);
		String fileName = workDir + "/data/users/" + user;
		dir = new File(fileName);
		boolean b = dir.mkdir();
		if (!b) {
		}

		setAlbums(new ArrayList<Album>());
		setPhotos(new ArrayList<Photo>());

	}
	/**
	 * Getter method for the user's name.
	 * @return
	 * 		return the user name
	 */
	public String getName() {
		return userName;
	}
	/**
	 * Setter method for the username.
	 * @param name
	 * 		set the user name
	 */
	public void setName(String name) {
		this.userName = name;
	}
	/**
	 * Getter method for the list of albums.
	 * @return
	 * 		return the list of albums
	 */
	public ArrayList<Album> getAlbums() {
		return albums;
	}
	/**
	 * Setter method for the list of albums.
	 * @param albums
	 * 		set the list of the albums
	 */
	public void setAlbums(ArrayList<Album> albums) {
		this.albums = albums;
	}
	/**
	 * Getter method for the list of photos.
	 * @return
	 * 		return the list of photos
	 */
	public ArrayList<Photo> getPhotos() {
		return photos;
	}
	/**
	 * Setter method for the list of photos.
	 * @param photos
	 * 		set the list of photos
	 */
	public void setPhotos(ArrayList<Photo> photos) {
		this.photos = photos;
	}

	/**
	 * Removes a photo from the user's list of photos.
	 * @param i
	 * 		index of photo
	 */
	public void delPhoto(int i){

		this.getPhotos().remove(i);
	}

	/**
	 * Removes an album from the user's list and deletes the associated directory.
	 * @param i
	 * 		index of album
	 * @param name
	 * 		the photo name
	 */
	public void delAlbum(int i, String name){

		this.getAlbums().remove(i);
		String filename = "data/users/" + this.getName()+"/" + name ;
		File AlbumFile = new File(filename);
		//delete all subfiles
		deleteDir(AlbumFile);
	}
	/**
	 * A method to recursively delete all subfiles and directories of a file.
	 * Used for deleting an occupied album.
	 * @param file
	 * 		file path to delete
	 */
	void deleteDir(File file) {
	    File[] contents = file.listFiles();
	    if (contents != null) {
	        for (File f : contents) {
	            deleteDir(f);
	        }
	    }
	    file.delete();
	}
	/**
	 * Finds the index of an album in the user list.
	 * @param name
	 * 		the album name
	 * @return
	 * 		index of the album
	 */		
	public int Albumindexer(String name){

		for(int i = 0; i < this.albums.size(); i++){
			if(albums.get(i) != null && albums.get(i).getName().equals(name)){

				return i;
			}
		}
		return -1;
	}
	/**
	 * Adds a new album and makes an associated directory with the same name.
	 * @param name
	 * 		the album name
	 * @return
	 * 		a boolean flag for whether the addition took place. 
	 */
	public boolean addAlbum(String name){
		Album a = new Album(name);
		String fileName = "data/users/" + this.getName()+"/" + name ;
		File AlbumFile = new File(fileName);
		boolean b = AlbumFile.mkdir();
		if(!b){return false;}
		this.getAlbums().add(a);
		return true;
	}
	/**
	 * Renames the current album.
	 * @param a
	 * 		the album
	 * @param newName
	 * 		new name for rename
	 * @return
	 * 		if the name exist then return false. otherwise return true
	 */
	public boolean renameAlbum(Album a, String newName){
		//check if new name already exists
		for(Album curr: this.getAlbums()){
			if(curr.getName().equals(newName)){
				return false;
			}
		}
		String oldName = a.getName();
		a.setName(newName);
		String filename = "data/users/" + this.getName()+"/" + oldName ;
		File albumDir = new File(filename);
		File newDir = new File(albumDir.getParent()+ "/" + newName);
		albumDir.renameTo(newDir);
		return true;
	}
	/**
	 * Getter method for the user's password.
	 * @return
	 * 		get the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * Getter method for a specific album.
	 * @param name
	 * 		get a specific album
	 * @return
	 * 		return the album, otherwise null
	 */
	public Album getAlbum(String name){
		if(this.getAlbums() == null){return null;}

		for(Album a: this.getAlbums()){
			if(a.getName().equals(name)){
				return a;
			}
		}
		return null;
	}
	/**
	 * Setter method for the user password.
	 * @param password
	 * 		set the password
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * Getter method for the directory associated with this user.
	 * @return
	 * 		get the directory of the user
	 */
	public File getDir(){
		return this.dir;
	}
}
