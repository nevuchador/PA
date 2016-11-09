package models.utilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
/**
 * Provides a list and map of a number of users, which provides the basis for the
 * data associated with the applicaton, which is specific to each user.
 * @author Michael Alweis
 * @author Subin Ahn.
 *
 */
public class UserList implements Serializable {

	/**
	 * The UID of the object.
	 */
	private static final long serialVersionUID = -4856174181748056638L;
	/**
	 * The main list of users, used by the application to hold all users and their associated data.
	 */
	private ArrayList<User> userList;
	/**
	 * A FX-specific user list.
	 */
	public static ObservableList<User> users;
	/**
	 * A map from usernames to their associated User objects.
	 */
	public static HashMap<String,User> userMap;
	/**
	 * Constructor method.
	 */
	public UserList(){
		userList = new ArrayList<User>();
		users = FXCollections.observableArrayList(userList);
		userMap = new HashMap<String,User>();
	}
	/**
	 * Adds a user to the list.
	 * @param userName
	 * 		add a username
	 * @param pw
	 * 		add a password
	 */
	public void addUser(String userName, String pw){
		User u = new User(userName,pw);
		users.add(u);
		userMap.put(userName, u);
	}
	/**
	 * Adds a user to the list, given a User object.
	 * @param u
	 * 		add the user in the list
	 */
	public static void addUser(User u){
		users.add(u);
		userMap.put(u.getName(), u);
	}
}