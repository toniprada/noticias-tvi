package es.upm.dit.gsi.noticiastvi.gtv.thread;

import org.apache.http.client.methods.HttpGet;

import es.upm.dit.gsi.noticiastvi.gtv.util.Constant;

import android.os.Handler;

public class PopularThread extends GetItemsThread {
	
	private static final String ACTION = "getPopular";

	
	public PopularThread(Handler handler) {
		super(handler);
	}

	@Override
	public HttpGet getRequest() {
		return  new HttpGet(Constant.SERVER_URL + "?action=" + ACTION);
	}

}
