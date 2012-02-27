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

package es.upm.dit.gsi.noticiastvi.gtv.fragment;

import android.content.Context;
import android.os.Handler;
import es.upm.dit.gsi.noticiastvi.gtv.account.Account;
import es.upm.dit.gsi.noticiastvi.gtv.thread.GetItemsThread;
import es.upm.dit.gsi.noticiastvi.gtv.thread.PopularThread;

/**
 * Fragment to show the popular items
 * 
 * @author Antonio Prada <toniprada@gmail.com>
 * 
 */
public class PopularFragment extends FragmentItemsFromServer {

	public PopularFragment(Context context, Account account) {
		super(context, account);
	}

	@Override
	public GetItemsThread getThread(Handler handler) {
		return new PopularThread(handler);
	}
	
	

}
