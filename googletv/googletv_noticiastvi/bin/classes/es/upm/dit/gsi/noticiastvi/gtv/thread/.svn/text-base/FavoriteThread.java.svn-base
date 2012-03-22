package es.upm.dit.gsi.noticiastvi.gtv.thread;

import org.apache.http.client.methods.HttpGet;

import es.upm.dit.gsi.noticiastvi.gtv.util.Constant;

import android.os.Handler;

public class FavoriteThread extends GetItemsThread {
	
	private static final String ACTION = "getContentsFromUser"; //&content=title 
	private String user = "";

	public FavoriteThread(Handler handler, String user) {
		super(handler);
		this.user = user;
	}

	@Override
	public HttpGet getRequest() {
		return  new HttpGet(Constant.SERVER_URL + "?action=" + ACTION + "&identifier=" + user);
	}

}
