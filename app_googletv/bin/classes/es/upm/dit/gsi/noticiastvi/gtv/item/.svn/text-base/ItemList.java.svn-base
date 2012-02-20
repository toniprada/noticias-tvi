
package es.upm.dit.gsi.noticiastvi.gtv.item;

import java.io.Serializable;
import java.util.ArrayList;

public class ItemList implements Serializable {
	
	public static final String EXTRA = "es.upm.dit.gsit.noticiastvi.gtv.video.VideoList";
	
	private static final long serialVersionUID = 4927417847969379742L;
	
	private ArrayList<Item> videos;
	private int selected;
	
	public ItemList(ArrayList<Item> videos , int selected) {
		setVideos(videos);
		setSelected(selected);
	}
	
	public ArrayList<Item> getVideos() {
		return videos;
	}
	public void setVideos(ArrayList<Item> videos) {
		this.videos = videos;
	}
	public int getSelected() {
		return selected;
	}
	
	public boolean setSelected(int selected) {
		if (selected >= 0 && selected < videos.size()) {
			this.selected = selected;
			return true;
		} else {
			return false;
		}
	}
	
	public Item getSelectedItem() {
		return videos.get(selected);
	}
	
	

}
