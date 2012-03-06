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

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.google.tv.leftnavbar.LeftNavBar;
import com.example.google.tv.leftnavbar.LeftNavBarService;

import es.upm.dit.gsi.noticiastvi.gtv.account.Account;
import es.upm.dit.gsi.noticiastvi.gtv.account.AccountActivity;
import es.upm.dit.gsi.noticiastvi.gtv.fragment.FavoriteFragment;
import es.upm.dit.gsi.noticiastvi.gtv.fragment.NewFragment;
import es.upm.dit.gsi.noticiastvi.gtv.fragment.PopularFragment;
import es.upm.dit.gsi.noticiastvi.gtv.fragment.RecommendationFragment;
import es.upm.dit.gsi.noticiastvi.gtv.fragment.SocialFragment;
import es.upm.dit.gsi.noticiastvi.gtv.util.CustomPreferenceManager;

/**
 * Entry point activity.
 * Shows the accounts dialogs and the content fragments.
 * 
 * @author Antonio Prada <toniprada@gmail.com>
 * 
 */
public class NoticiasTVIActivity extends Activity {
	
	private static final String TAG = "NoticiasTVIActivity";
	public static final String TITLE = "title";
	
	private static final int MENU_ABOUT = 0;
	private static final int MENU_SETTINGS = 1;
	private static final int MENU_USER = 2;
		
	private Context mContext;
//    private ActionBar mBar;
    private CustomPreferenceManager mPreferences;
    private Account mAccount;
//    private boolean initialized = false;
    
    private LeftNavBar mLeftNavBar;

    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mContext = this;
        mPreferences = new CustomPreferenceManager(mContext);
        
        LeftNavBar bar = (LeftNavBarService.instance()).getLeftNavBar((Activity) this);
        bar.setBackgroundDrawable(getResources().getDrawable(R.drawable.leftnav_bar_background_dark));
        setupBar();
		showAccounts();
    }
    
    private void setupBar() {
        ActionBar bar = getLeftNavBar();
        bar.setDisplayShowTitleEnabled(false);
    }
    
    public LeftNavBar getLeftNavBar() {
        if (mLeftNavBar == null) {
            mLeftNavBar = new LeftNavBar(this);
            mLeftNavBar.setOnClickHomeListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                	showAccounts();
                }
            });
        }
        return mLeftNavBar;
    }

	private void setupTabs() {
		ActionBar bar = getLeftNavBar();
		bar.removeAllTabs();
		ActionBar.Tab tabPopular = bar.newTab().setText(getText(R.string.popular));
//		ActionBar.Tab tabStream = bar.newTab().setText(getText(R.string.stream));
		ActionBar.Tab tabNew = bar.newTab().setText(getText(R.string.news));
		ActionBar.Tab tabRecommendation = bar.newTab().setText(getText(R.string.recommendation));
		ActionBar.Tab tabSocial = bar.newTab().setText(getText(R.string.social));
		ActionBar.Tab tabFavorite = bar.newTab().setText(getText(R.string.favorites));
		// Create fragments and add it to the tabs
		Fragment fragmentPopular = new PopularFragment(mContext, mAccount);
//		Fragment fragmentParrilla = new ParrillaFragment(mContext, mAccount);
		Fragment fragmentNew = new NewFragment(mContext, mAccount);
		Fragment fragmentRecommendation = new RecommendationFragment(mContext, mAccount);
		Fragment fragmentSocial = new SocialFragment(mContext, mAccount);
		Fragment fragmentFavorite = new FavoriteFragment(mContext, mAccount);
		tabPopular.setTabListener(new MyTabListener(fragmentPopular));
//		tabStream.setTabListener(new MyTabListener(fragmentParrilla));
		tabNew.setTabListener(new MyTabListener(fragmentNew));
		tabSocial.setTabListener(new MyTabListener(fragmentSocial));
		tabRecommendation.setTabListener(new MyTabListener(fragmentRecommendation));
		tabFavorite.setTabListener(new MyTabListener(fragmentFavorite));
		// Add tabs to the ActionBar
		bar.addTab(tabPopular);
		bar.addTab(tabNew);
//		bar.addTab(tabStream);
		bar.addTab(tabRecommendation);
		bar.addTab(tabSocial);
		bar.addTab(tabFavorite);
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		// Remember the last tab open:
		
		Tab tab = null;
		try {
			tab = bar.getTabAt(mPreferences.getLastTab());
		} catch (Exception e) {
			Log.w(TAG, "Exception getting the last tab, is the position in a valid range?");
		}
		if (tab != null) {
			bar.selectTab(tab);
		}
	}



    
    
    private class MyTabListener implements ActionBar.TabListener {

        private Fragment mFragment;

        // Called to create an instance of the listener when adding a new tab
        public MyTabListener(Fragment fragment) {
            mFragment = fragment;
        }

        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            ft.add(R.id.fragment_container, mFragment, null);
            mPreferences.setLastTab(tab.getPosition());
        }
        
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            ft.remove(mFragment);
        }

        public void onTabReselected(Tab tab, FragmentTransaction ft) {
            // do nothing
        }
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Settings
        MenuItem settings = menu.add(0, MENU_SETTINGS, 0, R.string.settings);
        settings.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        settings.setIcon(android.R.drawable.ic_menu_preferences);
        MenuItem user = menu.add(0, MENU_USER, 1, R.string.change_user);
        user.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        user.setIcon(R.drawable.ic_menu_cc);
        // About
        menu.add(0, MENU_ABOUT, 2,  getText(R.string.about));
        return true;
    }
        
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_USER:
            	showAccounts();
                return true;
            case MENU_ABOUT:
            	about();
                return true;
            case MENU_SETTINGS:
            	settings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
	private void showAccounts() {
		Intent intent = new Intent(this, AccountActivity.class);
		startActivityForResult(intent, R.layout.account);
	}
	
	private void about() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.about).setIcon(null)
				.setMessage(getText(R.string.about_text))
				.setCancelable(true)
				.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
							}
						}	
				);
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	private void settings() {
		Log.e(TAG, "Settings");

	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case R.layout.account:
			if (resultCode != RESULT_OK) {
				Toast.makeText(mContext,
						getText(R.string.choose_user),
						Toast.LENGTH_SHORT).show();
				showAccounts();
			} else {
		        final Bundle b = data.getBundleExtra(Account.ACCOUNT);
				Account account = new Account(b.getInt(Account.ID), b.getString(Account.NAME));
				mAccount = account;
				Toast.makeText(mContext,
						getText(R.string.welcome) + " " + account.getNombre(),
						Toast.LENGTH_SHORT).show();
				setupTabs();
			}
		}
	}
    

}