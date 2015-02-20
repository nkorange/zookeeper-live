package net.zookeeper.live.controllers;

import net.zookeeper.live.jobs.ProcessResultJob;
import play.Logger;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.mvc.Controller;
import play.mvc.WebSocket;

/**
 * 
 * @author zpf.073@gmail.com
 *
 */
public class WebSockController extends Controller {

	public static WebSocket<Object> index() {
		return new WebSocket<Object>() {

			// Called when the Websocket Handshake is done.
			public void onReady(WebSocket.In<Object> in,
					WebSocket.Out<Object> out) {

				// For each event received on the socket,
				in.onMessage(new Callback<Object>() {
					public void invoke(Object event) {

						// Log events to the console
						Logger.info(event.toString());

					}
				});

				// When the socket is closed.
				in.onClose(new Callback0() {
					public void invoke() {

						Logger.info("Disconnected");

					}
				});

				// Send a single 'Hello!' message
				out.write("Hello!");
				ProcessResultJob.setWebSocket(out);
			}
		};
	}
}
