
package es.upm.dit.gsi.noticiastvi.gtv.account;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;

/**
 * Wrapper for the database user-related operations
 * 
 * @author Antonio Prada <toniprada@gmail.com>
 * 
 */
public class AccountHelper {
	
	private DatabaseAdapter database;
	private Context context;
	
	public AccountHelper(Context context) {
		this.context = context;
		database = new DatabaseAdapter(context);
		database.open();
	}
	
	public Account addUser(String name) {
		open();
		int id = database.createUser(name);
		if (id != -1) {
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
