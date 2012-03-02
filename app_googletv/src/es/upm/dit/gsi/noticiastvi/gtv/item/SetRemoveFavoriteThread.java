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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import es.upm.dit.gsi.noticiastvi.gtv.util.Constant;

/**
 * set or removes a item from favorites
 * 
 * @author Antonio Prada <toniprada@gmail.com>
 * 
 */
public class SetRemoveFavoriteThread extends Thread {
	
	public static final String SET = "setPreference";
	public static final String REMOVE = "removePreference";

	private static final boolean TEST = Constant.TEST;
	public static final int RESULT_OK = 30;
	public static final int RESULT_ERROR = 31;

	private Handler handler;
	private int userId;
	private int id;
	private String action = SET;

	public SetRemoveFavoriteThread(Handler handler, int id, int userId, String action) {
		this.handler = handler;
		this.userId = userId;
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
					+ action + "&identifier=" + userId + "&content=" + id + "&preference=5");
			try {
				HttpResponse getResponse = client.execute(getRequest);
				InputStream source = getResponse.getEntity().getContent();
				if (source != null) {
					String res = convertStreamToString(source);
					return res.contains("ok");
				} else {
					return false;
				}			
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
	
    public String convertStreamToString(InputStream is)
            throws IOException {
        /*
         * To convert the InputStream to String we use the
         * Reader.read(char[] buffer) method. We iterate until the
         * Reader return -1 which means there's no more data to
         * read. We use the StringWriter class to produce the string.
         */
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {        
            return "";
        }
    }



}
