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

package es.upm.dit.gsi.noticiastvi.gtv.thread;

import org.apache.http.client.methods.HttpGet;

import es.upm.dit.gsi.noticiastvi.gtv.util.Constant;

import android.os.Handler;

/**
 * Get the list of favorites
 * 
 * @author Antonio Prada <toniprada@gmail.com>
 * 
 */
public class FavoritesThread extends GetItemsThread {
	
	private static final String ACTION = "getFavorites"; //&content=title 
	private int id;

	public FavoritesThread(Handler handler, int id) {
		super(handler);
		this.id = id;
	}

	@Override
	public HttpGet getRequest() {
		return  new HttpGet(Constant.SERVER_URL + "?action=" + ACTION + "&identifier=" + id);
	}

}
