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

package es.upm.dit.gsi.noticiastvi.gtv;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import es.upm.dit.gsi.noticiastvi.gtv.account.Account;
import es.upm.dit.gsi.noticiastvi.gtv.adapter.GalleryAdapter;
import es.upm.dit.gsi.noticiastvi.gtv.item.Item;
import es.upm.dit.gsi.noticiastvi.gtv.item.ItemList;
import es.upm.dit.gsi.noticiastvi.gtv.item.SetRemoveFavoriteThread;

/**
 * Video player activity.
 * It also shows additional info, has rating capabilities, etc
 * 
 * @author Antonio Prada <toniprada@gmail.com>
 * 
 */
public class ItemPlayerActivity extends Activity implements OnClickListener, OnCompletionListener {
		
	private Context mContext;
    private InfoPanel mInfoPanel;
    private ItemListPanel mListPanel;
	private ItemList mItemList;
    private CheckBox mStar;
    private VideoView mVideoView;
    private ScrollView mNewsView;
//    private TextView mNewTitle;
//    private TextView mNewSubtitle;
//    private TextView mNewText;
//    private ImageView mImage;
    private Account mAccount;
    
//    private MediaController mc;
    
    private static final String TAG = "VideoPlayerActivity";

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);
        mContext = this;
        
        mInfoPanel = new InfoPanel(this);
        
        Intent i = getIntent();
        String title = i.getStringExtra(NoticiasTVIActivity.TITLE);
        mItemList = (ItemList) i.getSerializableExtra(ItemList.EXTRA);
        mListPanel = new ItemListPanel(this, mItemList, title );
        Bundle b = i.getBundleExtra(Account.ACCOUNT);
		mAccount = new Account(b.getInt(Account.ID), b.getString(Account.NAME));
        
        mStar = (CheckBox) findViewById(R.id.star);
        
        setInfo();
   
    }
    
	@Override
	public void onResume() {
		super.onResume();
		initViews();
		playItem();
	}
	
	private void initViews() {
        // Setting video:
        mVideoView = (VideoView) findViewById(R.id.videoView);
        // Starting video:
        mVideoView.setClickable(true);
        mVideoView.setOnClickListener(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setVisibility(View.GONE);
        // Init news
        mNewsView = (ScrollView) findViewById(R.id.scrollViewNews);
        mNewsView.setVisibility(View.GONE);
//        mNewTitle = (TextView) findViewById(R.id.textTitle);
//        mNewSubtitle = (TextView) findViewById(R.id.textSubtitle);
//        mNewText = (TextView) findViewById(R.id.text);
//        mImage = (ImageView) findViewById(R.id.image);
	}
	
	public void playItem() {
//		Type type = mItemList.getSelectedItem().getType();
//		if ( type == Type.VIDEO) {
			ProgressDialog pd = ProgressDialog.show(mContext, mContext
					.getText(R.string.please_wait), mContext
					.getText(R.string.getting_videos), true, false);
	        mVideoView.setVideoPath(mItemList.getSelectedItem().getVideo());
	        mNewsView.setVisibility(View.GONE);
	        mVideoView.setVisibility(View.VISIBLE);
	        mInfoPanel.enable();
	        mVideoView.requestFocus();
		    mVideoView.start();
			pd.dismiss();
//		} else if (type == Type.PIECEOFNEWS) {
//	        mVideoView.setVisibility(View.GONE);
//	        mNewsView.setVisibility(View.VISIBLE);
//	        mInfoPanel.disable();
//	        PieceOfNews p = (PieceOfNews) mItemList.getSelectedItem();
//	        mNewTitle.setText(p.getNombre());
//	        mNewSubtitle.setText(p.getAutor());
//	        mNewText.setText(p.getContenido());
//	        ImageDownloader imageDowloader = ImageDownloaderSingleton.getImageDownloader();
//	        imageDowloader.download(p.getCaptura(), mImage);
//		}
	}
	
	private void changeVideoDiff(int diff) {
		changeVideoTo(mItemList.getSelected() + diff);
	}
	
	private void changeVideoTo(int position) {
		if (mItemList.setSelected(position)) {
	        mListPanel.hide();
	        mInfoPanel.hide();
	        setInfo();
	        mListPanel.setSelection(position);
	        playItem();
	        mInfoPanel.show(); 
		}
	}
	
	private void setInfo() {
		 Item i = mItemList.getSelectedItem(); 
		 mInfoPanel.setInfo(i);
	}
	
	@Override
	public void onCompletion(MediaPlayer mp) {
		changeVideoDiff(1);
	}

	@Override
	public void onClick(View v) {
		handleKeyCode(KeyEvent.KEYCODE_DPAD_CENTER);
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (handleKeyCode(keyCode)) {
			 return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
	
	private boolean handleKeyCode(int keyCode) {
		if (mListPanel.isVisible()) {
			if (mListPanel.handleKey(keyCode)) {
				return true;
			}
		} else if (mInfoPanel.isVisible()) {
			if (mInfoPanel.handleKey(keyCode)) {
				return true;
			}
		} else {
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_CENTER:
			case KeyEvent.KEYCODE_DPAD_UP:
			case KeyEvent.KEYCODE_DPAD_DOWN:
				mInfoPanel.show();
				return true;
			case KeyEvent.KEYCODE_DPAD_LEFT:
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				mListPanel.show();
				return true;
			}
		}
		
		switch (keyCode) {
		case KeyEvent.KEYCODE_BOOKMARK:
			favorite();
			return true;
		case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
		case KeyEvent.KEYCODE_MEDIA_NEXT:
		       changeVideoDiff(1);
			return true;
		case KeyEvent.KEYCODE_MEDIA_REWIND:
		case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
		    changeVideoDiff(-1);
			return true;
		case KeyEvent.KEYCODE_MEDIA_PAUSE:
//			if (mItemList.getSelectedItem().getType() == Type.VIDEO) {
				pause();
//			}
			return true;
		case KeyEvent.KEYCODE_MEDIA_PLAY:
//			if (mItemList.getSelectedItem().getType() == Type.VIDEO) {
				play();
//			}
			return true;
		default:
			return false;
		}
	}
	

	private void pause() {
		Toast.makeText(mContext,
				getText(R.string.paused),
				Toast.LENGTH_SHORT).show();
		mVideoView.pause();
	}
	
	 private void play() {
			Toast.makeText(mContext,
					getText(R.string.started),
					Toast.LENGTH_SHORT).show();
		mVideoView.start();
	 }
	

	
	private void favorite() {
		Log.i("favorite", "favorite");
		String action;
		final boolean remove;
		if (mStar.isChecked()) {
			action = SetRemoveFavoriteThread.REMOVE;
			remove = true;
		} else {
			action = SetRemoveFavoriteThread.SET;
			remove = false;
		}
		Handler handler = new Handler() {
			@Override
    		public void handleMessage(Message msg) {
    			switch(msg.what) {
    			case SetRemoveFavoriteThread.RESULT_OK:
					if (remove) {
						mStar.setChecked(false);
						Toast.makeText(mContext,
								getText(R.string.deleted_favorite),
								Toast.LENGTH_LONG).show();
					} else {
						mStar.setChecked(true);
						Toast.makeText(mContext,
								getText(R.string.added_favorite),
								Toast.LENGTH_LONG).show();
					}
					break;
				case SetRemoveFavoriteThread.RESULT_ERROR:
					Toast.makeText(mContext, getText(R.string.error_favorite),
							Toast.LENGTH_SHORT).show();
    				break;
    			}
    		}
    	};
    	SetRemoveFavoriteThread favoriteThread = new SetRemoveFavoriteThread(handler, mItemList.getSelectedItem().getId(),  mAccount.getId(), action);
    	favoriteThread.start();
	}
	
	private class ItemListPanel {
		private LinearLayout panel;
		private Animation appear;
		private Animation disappear;
		private boolean visible;
		private GalleryAdapter mAdapter;
		private Gallery gallery;
		
		public ItemListPanel(Context context , ItemList videoList, String title) {
			panel = (LinearLayout) findViewById(R.id.linearVideoList);
			panel.setVisibility(View.GONE);
			visible = false;
		    appear = AnimationUtils.loadAnimation(context, R.anim.appear);
		    disappear = AnimationUtils.loadAnimation(context, R.anim.disappear);
		     
		    TextView titleView = (TextView)findViewById(R.id.textViewTitle);
		    titleView.setText(title);
	        gallery = (Gallery)findViewById(R.id.gallery);
	        mAdapter = new GalleryAdapter(context, videoList.getVideos());
	        gallery.setAdapter(mAdapter);
	        setSelection(videoList.getSelected());
	        gallery.setOnItemClickListener( new OnItemClickListener() {
				@Override
		        public void onItemClick(final AdapterView<?> parent, final View v, final int position,
		                final long id) {
					Log.i(TAG, "Gallery: selected " + position);
			        changeVideoTo(position);
				}
			});
	        gallery.setOnItemSelectedListener( new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> adapter, View selectedView,
						int position, long arg3) {
					int first = gallery.getFirstVisiblePosition();
					int last = gallery.getLastVisiblePosition();
					for (int i = 0; i <= (last - first); i++) {
						View v = gallery.getChildAt(i);
						mAdapter.nonSelectedTransformation(v);
					}
					mAdapter.selectedTransformation(selectedView);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					Log.w(TAG , "Gallery: nothing selected");
				}
			});
		}
		
		
		
		public void setSelection(int position) {
	        gallery.setSelection(position, false);
	        View v = gallery.getChildAt(position - gallery.getFirstVisiblePosition());
	        mAdapter.selectedTransformation(v);
		}
		
		public boolean handleKey(int keyCode) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_UP:
			case KeyEvent.KEYCODE_DPAD_DOWN:
			case KeyEvent.KEYCODE_BACK:
				hide();
				return true;
			default:
				return false;
			}
		}
		
		public void show() {
			if (!isVisible()) {
				clearAnim();
				panel.startAnimation(appear);
				panel.setVisibility(View.VISIBLE);
				visible = true;
				gallery.requestFocus();
			}
		}
		
		
		public void hide() {
			if (isVisible()) {
				clearAnim();
				panel.startAnimation(disappear);
				panel.setVisibility(View.GONE);
				visible = false;
			}
		}
		
		private void clearAnim() {
			appear.reset();
			disappear.reset();
			panel.clearAnimation();
		}

		
		public boolean isVisible() {
			return visible;
		}

	}
	
	/**
	 * Panel that show info from the video, rating panel, etc. 
	 * Should be hidden if the user wants to.
	 * 
	 * @author Antonio Prada <toniprada@gmail.com>
	 */
	private class InfoPanel {
		
		private RelativeLayout header;
		private LinearLayout footer;
		private TextView mTitle;
		private TextView mSubtitle;
		private TextView mText;
		private Animation headerShow;
		private Animation footerShow;
		private Animation headerHide;
		private Animation footerHide;
		private boolean visible;
		private boolean enabled;
		
		public InfoPanel(Context context) {
			header = (RelativeLayout) findViewById(R.id.header);
			footer = (LinearLayout) findViewById(R.id.footer);
			mTitle = (TextView) findViewById(R.id.videoTitle);
			mSubtitle = (TextView) findViewById(R.id.videoSubTitle);
			mText = (TextView) findViewById(R.id.videoDescription);
			visible = true;
		    headerShow = AnimationUtils.loadAnimation(context, R.anim.header_show);
		    footerShow = AnimationUtils.loadAnimation(context, R.anim.footer_show);
		    headerHide = AnimationUtils.loadAnimation(context, R.anim.header_hide);
		    footerHide = AnimationUtils.loadAnimation(context, R.anim.footer_hide);
		    enabled = true;
		}
		
		public void setInfo(Item item) { 
	        mTitle.setText(Html.fromHtml(item.getNombre()));
	        mSubtitle.setText(Html.fromHtml(item.getFecha() + " - " + item.getAutor()));
	        mText.setText(Html.fromHtml(item.getContenido()));
	        mStar.setChecked(item.getHave() == 1);
		}
		
		
		public void enable() {
			show();
			enabled = true;
		}
		
		// ENABLE & DISABLE IS FOR TEXT NEWS
//		public void disable() {
//			hide();
//			enabled = false;
//		}
		
		public boolean handleKey(int keyCode) {
			if (!enabled) {
				return false;
			}
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_CENTER:
			case KeyEvent.KEYCODE_DPAD_UP:
			case KeyEvent.KEYCODE_DPAD_DOWN:
			case KeyEvent.KEYCODE_DPAD_LEFT:
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				hide();
				return true;
			default:
				return false;
			}
		}		
		
		private void show() {
			if (!enabled) {
				return;
			}
			if (!isVisible()) {
				clearAnim();
				header.startAnimation(headerShow);
				footer.startAnimation(footerShow);
				header.setVisibility(View.VISIBLE);
				footer.setVisibility(View.VISIBLE);
				visible = true;
			}
		}
		
		private void hide() {
			if (!enabled) {
				return;
			}
			if (isVisible()) {
				clearAnim();
				header.startAnimation(headerHide);
				footer.startAnimation(footerHide);
				header.setVisibility(View.GONE);
				footer.setVisibility(View.GONE);
				visible = false;
			}
		}
		
		private void clearAnim() {
			if (!enabled) {
				return;
			}
			headerShow.reset();
			footerShow.reset();
			header.clearAnimation();
			footer.clearAnimation();
		}
		
		public boolean isVisible() {
			return visible;
		}
		
	}


	

}
