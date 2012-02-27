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
import android.database.Cursor;

/**
 * Wrapper for the accounts database operations.
 * 
 * @author Antonio Prada <toniprada@gmail.com>
 * 
 */
public class DBHelper {
	
	private DatabaseAdapter database;
//	private Context context;
	
	public DBHelper(Context context) {
//		this.context = context;
		database = new DatabaseAdapter(context);
		database.open();
	}
	
	public Account createUser(String name, int id) {
		open();
		long rowId = database.createUser(name, id);
		if (rowId != -1) {
			return new Account(id, name);
		} else {
			return null;
		}
	}
	
	public ArrayList<Account> getUsers() {
		open();
		ArrayList<Account> accounts = new ArrayList<Account>();
		Cursor c = database.fetchAllUsers();
		while (c.moveToNext()) {
			int id =  c.getInt(c.getColumnIndex(Account.ID));
			String name = c.getString(c.getColumnIndex(Account.NAME));
			accounts.add(new Account(id, name));
		}
		c.close();
		return accounts;
	}
	
	public boolean deleteUser(int id) {
		open();
		return database.deleteUser(id);
	}
	
	private void open() {
		if (!database.isOpen()) {
			database.open();
		}
	}
	
	public void close() {
		if (database.isOpen()) {
			database.close();
		}
	}

	
}
