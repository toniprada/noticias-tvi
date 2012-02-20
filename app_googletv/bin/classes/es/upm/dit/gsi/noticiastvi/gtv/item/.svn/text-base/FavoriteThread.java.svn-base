//   Copyright 2011 UPM-GSI: Group of Intelligent Systems -
//   - Universidad Politécnica de Madrid (UPM)
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package es.upm.dit.gsi.noticiastvi.gtv.item;

import java.io.IOException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import es.upm.dit.gsi.noticiastvi.gtv.util.Constant;

/**
 * Favorite thread
 * 
 * @author Antonio Prada <toniprada@gmail.com>
 * 
 */
public class FavoriteThread extends Thread {
	
	public static final String SET = "setPreference";
	public static final String REMOVE = "removePreference";

	private static final boolean TEST = Constant.TEST;
	public static final int RESULT_OK = 30;
	public static final int RESULT_ERROR = 31;

	private Handler handler;
	private String user = "";
	private int id;
	private String action = SET;

	public FavoriteThread(Handler handler, String user, int id, String action) {
		this.handler = handler;
		this.user = user;
		this.id = id;
		this.action = action;
	}

	@Override
	public void run() {
		if (doFavorite()) {
			handler.sendMessage(Message.obtain(handler, RESULT_OK));
		} else {
			handler.sendMessage(Message.obtain(handler, RESULT_ERROR));
		}
	}

	private boolean doFavorite() {
		if (!TEST) {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet getRequest = new HttpGet(Constant.SERVER_URL + "?action="
					+ action + "&identifier=" + user + "&content=" + id + "&preference=1");
			try {
				client.execute(getRequest);
				return true;
			} catch (IOException e) {
				getRequest.abort();
				Log.w(getClass().getSimpleName(), "Error", e);
				return false;
			}
		} else {
			Log.i("favorite" , "TEST_MODE");
			return true;
		}

	}


}
