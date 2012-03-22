package es.upm.dit.gsi.noticiastvi.gtv.fragment;

import android.content.Context;
import android.os.Handler;
import es.upm.dit.gsi.noticiastvi.gtv.account.Account;
import es.upm.dit.gsi.noticiastvi.gtv.thread.GetItemsThread;
import es.upm.dit.gsi.noticiastvi.gtv.thread.NewThread;

public class NewFragment extends StreamFragment {

	public NewFragment(Context context, Account account) {
		super(context, account);
	}

	@Override
	public GetItemsThread getThread(Handler handler) {
		return new NewThread(handler);
	}		

}
