package com.cat.appmonitor.UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.cat.appmonitor.R;
import java.util.List;


public class PackageInfoAdapter extends BaseAdapter {

	private LayoutInflater mlayoutInflater = null;
	private List<AppInfo> mpackageInfo = null;
	private boolean[] misSelected; 

	public PackageInfoAdapter(Context context, List<AppInfo> packageInfo, boolean[] isSelected) {
		super();
		mlayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mpackageInfo = packageInfo;
		misSelected = isSelected;
	}

	@Override
	public int getCount() {
		return mpackageInfo.size();
	}

	@Override
	public Object getItem(int position) {
		return mpackageInfo.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		ViewHolder holder = null;

		if (convertView == null) {
			view = mlayoutInflater.inflate(R.layout.process_item, null);
			holder = new ViewHolder(view);
			view.setTag(holder);
		} else {
			view = convertView; 
			holder = (ViewHolder)view.getTag();
		}
		
		AppInfo appInfo = (AppInfo)getItem(position);
		holder.appIcon.setImageDrawable(appInfo.getAppIcon());
		holder.appName.setText(appInfo.getAppLabel());
		holder.appPkgName.setText(appInfo.getPkgName());
		holder.isChooseButton.setChecked(misSelected[position]);
		return view;
	}

	class ViewHolder {

		ImageView appIcon;
		TextView appName;
		TextView appPkgName;
		CheckBox isChooseButton;

		public ViewHolder(View view) {
			this.appIcon = (ImageView) view.findViewById(R.id.app_icon);
            this.appName = (TextView) view.findViewById(R.id.app_name);
			this.appPkgName = (TextView) view.findViewById(R.id.package_name);
			this.isChooseButton = (CheckBox)view.findViewById(R.id.isChoose);
		}
	}
}
