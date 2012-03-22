package es.upm.dit.gsi.noticiastvi.gtv.fragment;

import android.content.Context;
import android.os.Handler;
import es.upm.dit.gsi.noticiastvi.gtv.account.Account;
import es.upm.dit.gsi.noticiastvi.gtv.thread.GetItemsThread;
import es.upm.dit.gsi.noticiastvi.gtv.thread.ParrillaThread;

public class ParrillaFragment extends StreamFragment {
	
	private String user;

	public ParrillaFragment(Context context, Account account) {
		super(context, account);

	}

	@Override
	public GetItemsThread getThread(Handler handler) {
		return new ParrillaThread(handler, mAccount.getName());
	}
	
	

}
