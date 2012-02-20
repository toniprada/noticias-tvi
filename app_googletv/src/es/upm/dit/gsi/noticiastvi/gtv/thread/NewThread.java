package es.upm.dit.gsi.noticiastvi.gtv.thread;

import org.apache.http.client.methods.HttpGet;

import android.os.Handler;

public class NewThread extends GetItemsThread {
	
	private static final String URL = "http://138.4.3.224:8080/Recommender/noticias?action=getRecommendation&identifier=toni";

	public NewThread(Handler handler) {
		super(handler);
	}

	@Override
	public HttpGet getRequest() {
		return  new HttpGet(URL);
	}

}
