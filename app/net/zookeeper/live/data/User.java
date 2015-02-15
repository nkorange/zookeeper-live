package net.zookeeper.live.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import models.db.UploadInfo;
import play.Logger;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

/**
 *
 * @author zpf.073@gmail.com
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "zl_user")
public class User extends Model {

	public static final String NAME="name";
	public static final String PASSWORD="password";
	public static final String GROUP="group";
	
	private static Finder<String, User> find = new Finder<String, User>(
			String.class, User.class);

	@Id
	@Column(name = "id")
	public long id;
	
	@Column(name = NAME)
	public String name;
	
	@Column(name = PASSWORD)
	public String password;
	
	@Column(name = GROUP)
	public int group;
	
	public static boolean addUser(String name, String password, int group) {
		User user = new User();
		user.name = name;
		user.password = password;
		user.group = group;
		
		try {
			user.save();
		} catch (Exception e) {
			Logger.error("[User] ", e);
			return false;
		}
		return true;
	}
	
	public static User getUser(String name) {
		User user = null;
		try {
			user = find.where().eq(NAME, name).findUnique();
		} catch (Exception e) {
			Logger.error("[User] ", e);
		}
		return user;
	}
}
