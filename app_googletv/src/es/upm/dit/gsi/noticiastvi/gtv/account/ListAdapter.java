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

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import es.upm.dit.gsi.noticiastvi.gtv.R;

/**
 * List adaptar that shows the accounts.
 * 
 * @author Antonio Prada <toniprada@gmail.com>
 * 
 */

public class ListAdapter extends ArrayAdapter<Account> {

//		private final Context context;
		private final ArrayList<Account> accounts;
		private LayoutInflater inflater;

		public ListAdapter(Context context, ArrayList<Account> accounts) {
			super(context, R.layout.account_row, accounts);
//			this.context = context;
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
				tv.setText(account.getNombre());
			}
			return v;
	}
		
		@Override
		public Account getItem(int position) {
			return accounts.get(position);
		}
		
		
	

}
