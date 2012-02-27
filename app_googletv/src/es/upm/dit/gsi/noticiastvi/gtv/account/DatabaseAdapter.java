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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Database of accounts.
 * 
 * @author Antonio Prada <toniprada@gmail.com>
 * 
 */
public class DatabaseAdapter {
	
	private static final int DB_VERSION = 2;
	private static final String DB_TABLE = "user";
	private static final String DB_NAME = "login";
	private static final String DB_CREATE = "create table " + DB_TABLE + " ( "
			+ Account.ID + " integer primary key" + ", "
			+ Account.NAME + " text not null " + " );";

	private Context context;
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;


	private class DatabaseHelper extends SQLiteOpenHelper {

		/**
		 * Database Helper constructor.
		 * 
		 * @param context
		 */
		public DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		/**
		 * Creates the database tables.
		 */
		@Override
		public void onCreate(SQLiteDatabase database) {
			database.execSQL(DB_CREATE);
		}

		/**
		 * Handles the table version and the drop of a table.
		 */
		@Override
		public void onUpgrade(SQLiteDatabase database, int oldVersion,
				int newVersion) {
			Log.w(DatabaseHelper.class.getName(),
					"Upgrading database from version" + oldVersion + "to "
							+ newVersion + ", which will destroy all old data");
			database.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
			onCreate(database);
		}
	}

	/**
	 * The adapter constructor.
	 * 
	 * @param context
	 */
	public DatabaseAdapter(Context context) {
		this.context = context;
	}

	/**
	 * Creates the database helper and gets the database.
	 * 
	 * @return
	 * @throws SQLException
	 */
	public DatabaseAdapter open() throws SQLException {
		dbHelper = new DatabaseHelper(context);
		database = dbHelper.getWritableDatabase();
		return this;
	}

	/**
	 * Closes the database.
	 */
	public void close() {
		dbHelper.close();
	}
	
	public boolean isOpen() {
		return database.isOpen();
	}

	/**
	 * Creates the user name and password.
	 * 
	 * @param username
	 *            The username.
	 * @param password
	 *            The password.
	 * @return the row ID of the newly inserted row, or -1 if an error occurred 
	 * 
	 */
	public int createUser(String username, int id) {
		ContentValues initialValues = createContentValues(username, id);
		return (int) database.insert(DB_TABLE, null, initialValues);
	}

	/**
	 * Removes a user's details given an id.
	 * 
	 * @param rowId
	 *            Column id.
	 * @return if deleted
	 */
	public boolean deleteUser(int id) {
		return database.delete(DB_TABLE, Account.ID + "=" + id, null) > 0;
	}

	public boolean updateUserTable(int id, String name) {
		ContentValues updateValues = createContentValues(name, id);
		return database.update(DB_TABLE, updateValues, Account.ID + "=" + id,
				null) > 0;
	}

	/**
	 * Retrieves the details of all the users stored in the login table.
	 * 
	 * @return
	 */
	public Cursor fetchAllUsers() {
		return database.query(DB_TABLE,
				new String[] { Account.ID, Account.NAME }, null, null, null,
				null, null);
	}

//	/**
//	 * Retrieves the details of a specific user, given a username and password.
//	 * 
//	 * @return
//	 */
//	public Cursor fetchUser(String username, String password) {
//		Cursor myCursor = database.query(DB_TABLE, new String[] { COL_ID,
//				COL_USERNAME, COL_PASSWORD }, COL_USERNAME + "='" + username
//				+ "' AND " + COL_PASSWORD + "='" + password + "'", null, null,
//				null, null);
//
//		if (myCursor != null) {
//			myCursor.moveToFirst();
//		}
//		return myCursor;
//	}

//	/**
//	 * Returns the table details given a row id.
//	 * 
//	 * @param rowId
//	 *            The table row id.
//	 * @return
//	 * @throws SQLException
//	 */
//	public Cursor fetchUserById(long rowId) throws SQLException {
//		return database.query(DB_TABLE, new String[] { Account.ID,
//				 Account.NAME}, Account.ID + "=" + rowId, null, null,
//				null, null);
//	}

	/**
	 * Stores the username and password upon creation of new login details.
	 * 
	 * @param username
	 *            The user name.
	 * @param password
	 *            The password.
	 * @return The entered values.
	 */
	private ContentValues createContentValues(String username, int id) {
		ContentValues values = new ContentValues();
		values.put(Account.NAME, username);
		values.put(Account.ID, id);
		return values;
	}
}