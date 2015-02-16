package net.zookeeper.live.controllers;

import java.util.Map;
import java.util.concurrent.Callable;

import net.zookeeper.live.common.PathTrie;
import net.zookeeper.live.common.PathTrie.TrieNode;
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
public class ManageController extends Controller {

	public static Result addMonitorPath() {
		
		Map<String, String[]> params = request().body().asFormUrlEncoded();
		final String path = BaseController.TransformParam("path", params);
		
		Promise<Object> promise = Akka.future(new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				//check if path exists in PathTrie:
				TrieNode node = PathTrie.zkPathTrie().getNode(path);
				if (node == null) {
					return null;
				}
				
				return null;
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
