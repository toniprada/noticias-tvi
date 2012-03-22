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

package es.upm.dit.gsi.noticiastvi.gtv.fragment;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import es.upm.dit.gsi.noticiastvi.gtv.R;
import es.upm.dit.gsi.noticiastvi.gtv.account.Account;
import es.upm.dit.gsi.noticiastvi.gtv.item.Item;
import es.upm.dit.gsi.noticiastvi.gtv.thread.GetItemsThread;

/**
 * Fragments that shows a list of items from the server.
 * 
 * @author Antonio Prada <toniprada@gmail.com>
 * 
 */

public abstract class FragmentItemsFromServer extends FragmentItems {
	
	private Context mContext;

	public FragmentItemsFromServer(Context context, Account account) {
		super(context, account);
		mContext = context;
	}

	@Override
	public void getVideos() {
		// Show a dialog while we're getting the videos list
		Context context = super.getContext();
		  final ProgressDialog pd = ProgressDialog.show( context,
				  context.getText(R.string.please_wait),
				  context.getText(R.string.getting_videos),
				  true, false);
	    	Handler handler = new Handler() {
	    		@SuppressWarnings("unchecked")
				@Override
	    		public void handleMessage(Message msg) {
	    			// First of all, dismiss the dialog
	    			pd.dismiss();
	    			// Now we'll see see what happened in the thread
	    			switch(msg.what) {
	    			case GetItemsThread.RESULT_OK:
	    				ArrayList<Item> videos = (ArrayList<Item>) msg.obj;
	    				setVideos(videos);
	    				break;
	    			case GetItemsThread.RESULT_ERROR:
	    				Toast.makeText(mContext, mContext.getText(R.string.error_connection), Toast.LENGTH_LONG).show();
	    				// TODO error handling, show a dialog to retry
	    			}
	    		}
	    	};
	    	GetItemsThread thread = getThread(handler);
	    	thread.start();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		getVideos();
	}
	
	
	public abstract GetItemsThread getThread(Handler handler);

}
