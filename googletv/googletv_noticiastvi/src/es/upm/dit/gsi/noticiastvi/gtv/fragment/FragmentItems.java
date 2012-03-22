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

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import es.upm.dit.gsi.noticiastvi.gtv.ItemPlayerActivity;
import es.upm.dit.gsi.noticiastvi.gtv.NoticiasTVIActivity;
import es.upm.dit.gsi.noticiastvi.gtv.R;
import es.upm.dit.gsi.noticiastvi.gtv.account.Account;
import es.upm.dit.gsi.noticiastvi.gtv.adapter.GridAdapter;
import es.upm.dit.gsi.noticiastvi.gtv.item.Item;
import es.upm.dit.gsi.noticiastvi.gtv.item.ItemList;

/**
 * Fragments that shows a list of items.
 * 
 * @author Antonio Prada <toniprada@gmail.com>
 * 
 */
public abstract class FragmentItems extends Fragment implements OnItemClickListener {
	
	private static final String TAG = "FragmentTab";
	
	private Context mContext;
	private GridView mGridView;
	private GridAdapter mAdapter;
	protected Account mAccount;
	
	private int itemPosition;

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
	
	public FragmentItems(Context context, Account account) {
		mContext = context;
	    mAccount = account;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment, container, false);
        mGridView = (GridView) v.findViewById(R.id.gridView);
        mGridView.setOnItemClickListener(this);
        return v;
    }
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getVideos();
	}
	
	  public abstract void getVideos();
	    
	    public void setVideos(ArrayList<Item> videos) {
	    	if (videos == null || videos.size() == 0) {
	    		Log.w(TAG , "Empty videos list");
	    	} else {
	    		mAdapter = new GridAdapter(mContext, videos);
		    	mGridView.setAdapter(mAdapter);
		    	mGridView.setSelection(itemPosition);
	    	}
	    }
	    
	    
		public void click(int position) {


		}
		
		public Context getContext()  {
			return mContext;
		}

		@Override
        public void onItemClick(final AdapterView<?> parent, final View v, final int position,
                final long id) {
	        Item video = mAdapter.getItem(position);
	        ArrayList<Item> videos = mAdapter.getVideos();
	        int selected = videos.indexOf(video);
	        ItemList videoList = new ItemList(videos, selected);
  	        final Intent i = new Intent(mContext , ItemPlayerActivity.class);
  	        i.putExtra(ItemList.EXTRA, videoList);
  	        String title = ((NoticiasTVIActivity) getActivity()).getLeftNavBar().getSelectedTab().getText().toString();
  	        i.putExtra(NoticiasTVIActivity.TITLE, title);
  			Bundle bundle = mAccount.getAsBundle();
  			i.putExtra(Account.ACCOUNT, bundle);
  			itemPosition = position;
  			startActivityForResult(i, R.layout.player);
		}

}
