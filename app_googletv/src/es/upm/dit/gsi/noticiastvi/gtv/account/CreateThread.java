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

package es.upm.dit.gsi.noticiastvi.gtv.account;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;

import es.upm.dit.gsi.noticiastvi.gtv.util.Constant;

/**
 * Thread to create accounts on the server.
 * 
 * @author Antonio Prada <toniprada@gmail.com>
 * 
 */
public class CreateThread extends Thread {
	
	private static final String ACTION = "newUser";
	public static final int RESULT_OK = 40;
	public static final int RESULT_ERROR = 41;

	private Handler handler;
	private String username = "";

	public CreateThread(Handler handler, String username) {
		this.handler = handler;
		this.username = username;
	}

	@Override
	public void run() {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(Constant.SERVER_URL + "?action=" + ACTION + "&name=" + username);
		try {
			HttpResponse getResponse = client.execute(get);
			final int statusCode = getResponse.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.w(getClass().getSimpleName(), "Error " + statusCode);
				handler.sendEmptyMessage(RESULT_ERROR);
				return;
			}
			InputStream source = getResponse.getEntity().getContent();
			if (source != null) {
				Gson gson = new Gson();
				Reader reader = new InputStreamReader(source);
				Account account = gson.fromJson(reader, Account.class);
				handler.sendMessage(Message.obtain(handler, RESULT_OK, account));
			} else {
				handler.sendEmptyMessage(RESULT_ERROR);
			}			
		} catch (Exception e) {
			handler.sendEmptyMessage(RESULT_ERROR);
			Log.e("ERROR", e.getMessage());
		}
	}
	
//	private String inputStreamToString(InputStream is) throws IOException {
//	    String s = "";
//	    String line = "";
//	    
//	    // Wrap a BufferedReader around the InputStream
//	    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
//	    
//	    // Read response until the end
//	    while ((line = rd.readLine()) != null) { s += line; }
//	    
//	    // Return full string
//	    return s;
//	}

}
