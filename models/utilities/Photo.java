package models.utilities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Provides a model for a photograph. Stores information such as the filepath of
 * the photo file and the tags associated with the picture.
 *
 * @author Michael Alweis
 * @author Subin Ahn
 */
public class Photo implements java.io.Serializable {


	/**
	 *The UID for the object.
	 */
	private static final long serialVersionUID = -858779163673918171L;
	/**
 	* The filepath for the image associated with this instance.
 	*/
	public String path;
	/**
	 * The key-value pairs of tags for this photo.
	 */
	public HashMap<String,ArrayList<String>> tags;
	/**
	 * The name of the photo.
	 */
	public String name;
	/**
	 * The caption for the photo.
	 */
	public String caption;
	/**
	 * The date of last modification of the photo file, provided by the system.
	 */
	public Calendar date;
	public static String[] possibleTags= {"location","person","animal","holiday","vacation","show","food"};

	/**
	 * Constructor, using filepath and name as arguments.
	 * @param path
	 * 		construct filepath
	 * @param name
	 * 		construct photo name
	 */
	public Photo(String path,String name){
		this.path = path;
		tags = new HashMap<String,ArrayList<String>>();
		this.name = name;
	}
	/**
	 * Getter method for the photo name.
	 * @return
	 * 		return the photo name
	 */
	public String getName(){
		return name;
	}
	/**
	 * Setter method for the photo name.
	 * @param name
	 * 		set the photo name
	 */
	public void setName(String name){
		this.name = name;
	}

}
