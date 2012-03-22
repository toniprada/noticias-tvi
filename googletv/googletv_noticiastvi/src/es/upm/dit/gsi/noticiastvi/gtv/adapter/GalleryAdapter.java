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

package es.upm.dit.gsi.noticiastvi.gtv.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import es.upm.dit.gsi.noticiastvi.gtv.R;
import es.upm.dit.gsi.noticiastvi.gtv.item.Item;
import es.upm.dit.gsi.noticiastvi.gtv.item.image.ImageDownloader;
import es.upm.dit.gsi.noticiastvi.gtv.item.image.ImageDownloaderSingleton;

// This class just manages our cells.

/**
 * Adapter for the video player gallery
 * 
 * @author Antonio Prada <toniprada@gmail.com>
 * 
 */
public class GalleryAdapter extends BaseAdapter {
	
	public static final float SCALE = 0.5f;

    private final LayoutInflater inflater;
    private ArrayList<Item> videos;
    private final ImageDownloader imageDownloader = ImageDownloaderSingleton.getImageDownloader();


    // references to our images

    public GalleryAdapter(Context context, ArrayList<Item> videos) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.videos = videos;
    }

    public int getCount() {
        return videos.size();
    }
    
    public Item getItem(int index) {
    	return (Item) videos.get(index);
    }

    public View getView(final int position, final View convertView, final ViewGroup parent) {
    	View v = convertView;
    	if (v == null) {
    		v = inflater.inflate(R.layout.gallery_cell, null);
    	}
    	Item item = getItem(position);
    	TextView tt = (TextView) v.findViewById(R.id.title);
//    	TextView ts = (TextView) v.findViewById(R.id.subtitle);
    	ImageView iv = (ImageView) v.findViewById(R.id.thumbnail);
    	ImageView vid  = (ImageView) v.findViewById(R.id.video);
    	if (tt != null) {
    		tt.setText(item.getNombre());
    	}
//    	if (ts != null) {
//    		ts.setText(video.getSubTitle());
//    	}
    	if (iv != null && item.getCaptura() != null) {
//            iv.reset();
//    		iv.setImageUrl(video.getThumb());
//    		iv.loadImage();
//    		iv.setBackgroundColor(Color.BLACK);
            imageDownloader.download(item.getCaptura(), iv);
    	}
    	if (vid!= null) {
//    		if (item.getType() == Type.VIDEO) {
//    			vid.setVisibility(View.VISIBLE);
//    		} else {
    			vid.setVisibility(View.GONE);
//    		}
    	}

//        LinearLayout lg = (LinearLayout) convertView;
//        ImageView imageView;
//        if (lg == null) {
//            lg = (LinearLayout) mInflator.inflate(R.layout.cell, null);
//        }
//
//        imageView = (ImageView) lg.getChildAt(0);
//        mDownload.download(mVideos.get(position).mThumb.toString(), imageView);
//        ((TextView) lg.getChildAt(1)).setText(mVideos.get(position).mTitle);
//        ((TextView) lg.getChildAt(2)).setText(mVideos.get(position).mSubTitle);
//    	scaleView(v);
    	nonSelectedTransformation(v);
        return v;
       }
    
    
    public void nonSelectedTransformation(View v) {
		if (v != null) {
			v.setScaleX(SCALE);
			v.setScaleY(SCALE);
		}
    }
    
    public void selectedTransformation(View v) {
		if (v != null) {
			v.setScaleX(1.0f);
			v.setScaleY(1.0f);
		}
    }
  

        
    

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	public ArrayList<Item> getVideos() {
		return videos;
	}

	
}
