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

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;
import es.upm.dit.gsi.noticiastvi.gtv.account.Account;
import es.upm.dit.gsi.noticiastvi.gtv.account.AccountHelper;
import es.upm.dit.gsi.noticiastvi.gtv.account.ListAdapter;

/**
 * Activity called by the launcher when the icon is pressed
 * 
 * @author Antonio Prada <toniprada@gmail.com>
 * 
 */
public class AccountsActivity extends ListActivity implements OnClickListener {

	private Context mContext;
	private ListAdapter adapter;
	private AccountHelper helper;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.account);
		mContext = this;
		setTitle(R.string.choose_user);
		helper = new AccountHelper(mContext);

		Button add = (Button) findViewById(R.id.button1);
		add.setOnClickListener(this);

		ArrayList<Account> accounts = helper.getUsers();
		adapter = new ListAdapter(mContext, accounts);
		setListAdapter(adapter);

		ListView list = getListView();
		list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Account account = adapter.getItem(position);
				deleteAccount(account);
				return true;
			}
		});
	}

	private void deleteAccount(final Account account) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				getText(R.string.are_you_sure_delete) + account.getName()
						+ getText(R.string.are_you_sure_delete_finish))
				.setCancelable(false).setPositiveButton(getText(R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
								if (helper.deleteUser(account.getId())) {
									// TODO make toast: deleted user
									Toast.makeText(mContext, "Deleted",
											Toast.LENGTH_LONG).show();
									adapter.remove(account);
									adapter.notifyDataSetChanged();
								}
							}
						}).setNegativeButton(getText(R.string.no),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			showAddDialog();
		}

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// Get the item that was clicked
		Account account = adapter.getItem(position);
		Intent data = new Intent();
		Bundle bundle = account.getAsBundle();
		data.putExtra(Account.ACCOUNT, bundle);
		setResult(RESULT_OK , data);
		
		finish();
	}

	private void showAddDialog() {
		final Dialog dialog = new Dialog(mContext);

		dialog.setContentView(R.layout.account_add_dialog);
		dialog.setTitle(getText(R.string.new_user));

		Button b = (Button) dialog.findViewById(R.id.buttonAddOk);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText et = (EditText) dialog.findViewById(R.id.editTextAdd);
				createUser(et.getText().toString());
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	private void createUser(String name) {
		Account account = helper.addUser(name);
		if (account != null) {
			adapter.add(account);
			adapter.notifyDataSetChanged();
		} else {
			// TODO Toast: error while adding user to DB
		}

	}

	@Override
	public void onDestroy() {
		if (helper != null) {
			helper.close();
		}
		setResult(RESULT_CANCELED);
		super.onDestroy();
	}


}