package es.upm.dit.gsi.noticiastvi.gtv.fragment;

import android.content.Context;
import android.os.Handler;
import es.upm.dit.gsi.noticiastvi.gtv.account.Account;
import es.upm.dit.gsi.noticiastvi.gtv.thread.FavoriteThread;
import es.upm.dit.gsi.noticiastvi.gtv.thread.GetItemsThread;

public class FavoriteFragment extends StreamFragment {
	
	public FavoriteFragment(Context context, Account account) {
		super(context, account);
	}

	@Override
	public GetItemsThread getThread(Handler handler) {
		return new FavoriteThread(handler, mAccount.getName());
	}
	
	

}
