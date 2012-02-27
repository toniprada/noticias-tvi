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

import android.os.Bundle;

/**
 * POJO of the user accounts.
 * 
 * @author Antonio Prada <toniprada@gmail.com>
 * 
 */
public class Account {
	
	public static final String ACCOUNT = "account";
	public static final String ID = "_id";
	public static final String NAME = "name";
	
	private int id;
	private String name;
	
	public Account(int id, String name) {
		setId(id);
		setName(name);
	}
	
	/**
	 * Get the account as a bundle to be passed with the intents
	 * 
	 * @return The video object as a bundle
	 */
    public Bundle getAsBundle() {
        Bundle b = new Bundle();
        b.putInt(ID, id);
        b.putString(NAME, name);
        return b;
    }

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}
