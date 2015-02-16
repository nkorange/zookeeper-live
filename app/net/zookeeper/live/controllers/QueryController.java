package net.zookeeper.live.controllers;

import java.util.concurrent.Callable;

import net.zookeeper.live.common.PathTrie;
import net.zookeeper.live.conn.ZooKeeperConn;

import play.libs.Akka;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Result;

/**
 *
 * @author zpf.073@gmail.com
 *
 */
public class QueryController extends Controller {

	public static Result getPaths() {

		Promise<Object> promise = Akka.future(new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				PathTrie pathTrie = ZooKeeperConn.getInstance().getAllNodes();
				return pathTrie.getNode("/");
			}
			
		});
		
		return async(promise.map(new Function<Object, Result>() {

			@Override
			public Result apply(Object object) throws Exception {
				return ok(object.toString());
			}
		}));
	}
}
