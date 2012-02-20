package es.upm.dit.gsi.noticiastvi.gtv.account;

import android.os.Bundle;

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
