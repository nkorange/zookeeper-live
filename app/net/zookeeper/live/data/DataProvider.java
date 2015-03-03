package net.zookeeper.live.data;

import net.zookeeper.live.constants.UserGroup;
import net.zookeeper.live.utils.EncriptUtil;
import play.Configuration;

import com.avaje.ebean.CallableSql;
import com.avaje.ebean.Ebean;

/**
 *
 * @author zpf.073@gmail.com
 *
 */
public class DataProvider {
	
	public static void initialize() {
		createUserTable();
		createDefaultUser();
	}

	private static void createUserTable() {
		String sql = "DROP TABLE IF EXISTS `zl_user`;"+
				"CREATE TABLE `zl_user` ("+
				"`id` bigint(20) NOT NULL AUTO_INCREMENT,"+
				"`name` varchar(64) NOT NULL,"+
				"`password` varchar(256) NOT NULL,"+
				"`group` tinyint(4) NOT NULL DEFAULT '0' COMMENT '1:admin, 2:guest',"+
				"PRIMARY KEY (`id`),"+
				"UNIQUE KEY `idx_name` (`name`) USING BTREE" +
  				") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
		CallableSql call = Ebean.createCallableSql(sql);
		Ebean.execute(call);
	}
	
	private static void createDefaultUser() {
		Configuration config = Configuration.root();
		String userName = config.getString("user.default.name", "admin");
		String userPasswd = config.getString("user.default.password", "admin");
		User.addUser(userName, EncriptUtil.encriptPassword(userPasswd), UserGroup.GROUP_ADMIN);
	}
	
	public static boolean validateUserToken(String token) {
		//TODO
		return false;
	}
	
	public static User getUser(String userName) {
		return User.getUser(userName);
	}
}
