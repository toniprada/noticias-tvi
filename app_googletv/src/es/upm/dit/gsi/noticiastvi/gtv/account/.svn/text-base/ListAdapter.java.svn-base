
package es.upm.dit.gsi.noticiastvi.gtv.account;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import es.upm.dit.gsi.noticiastvi.gtv.R;

public class ListAdapter extends ArrayAdapter<Account> {

		private final Context context;
		private final ArrayList<Account> accounts;
		private LayoutInflater inflater;

		public ListAdapter(Context context, ArrayList<Account> accounts) {
			super(context, R.layout.account_row, accounts);
			this.context = context;
			this.accounts = accounts;
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v;
			if (convertView == null) {
				v = inflater.inflate(R.layout.account_row, null, true);
			} else {
				v = convertView;
			}
			Account account = getItem(position);
			TextView tv = (TextView) v.findViewById(R.id.text);
			if (tv != null) {
				tv.setText(account.getName());
			}
			return v;
	}
		
		@Override
		public Account getItem(int position) {
			return accounts.get(position);
		}
		
		
	

}
