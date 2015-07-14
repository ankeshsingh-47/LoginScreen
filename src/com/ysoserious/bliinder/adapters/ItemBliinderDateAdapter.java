package com.ysoserious.bliinder.adapters;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.ysoserious.bliinder.R;
import com.ysoserious.bliinder.entities.BliinderDate;
import com.ysoserious.bliinder.utils.DateState;

public class ItemBliinderDateAdapter extends BaseAdapter implements Filterable {
	private static List<BliinderDate> filteredData;
	private List<BliinderDate> originalData;
	private LayoutInflater l_Inflater;
	private Context c;

	public ItemBliinderDateAdapter(Context c, List<BliinderDate> results) {
		filteredData = results;
		l_Inflater = LayoutInflater.from(c);
		originalData = results;
		this.c = c;
	}

	@Override
	public int getCount() {
		return filteredData.size();
	}

	@Override
	public Object getItem(int position) {
		return filteredData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = l_Inflater.inflate(R.layout.item_details_view, null);

			holder = new ViewHolder(convertView);
			holder.txt_dateName.setText(filteredData.get(position).getPartner()
					.getFirstName());
			holder.txt_dateState.setText(filteredData.get(position).getState()
					.toString());
			holder.bliinderDateImage.setImageResource(DateState
					.getImageResource(filteredData.get(position).getState()));
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.txt_dateName.setText(filteredData.get(position).getPartner()
				.getFirstName());
		holder.txt_dateState.setText(filteredData.get(position).getState()
				.toString());
		holder.bliinderDateImage.setImageResource(DateState
				.getImageResource(filteredData.get(position).getState()));
		return convertView;
	}

	static class ViewHolder {
		TextView txt_dateName;
		TextView txt_dateState;
		ImageView bliinderDateImage;

		ViewHolder(View convertView) {
			this.txt_dateName = (TextView) convertView
					.findViewById(R.id.dateName);
			this.txt_dateState = (TextView) convertView
					.findViewById(R.id.dateState);
			this.bliinderDateImage = (ImageView) convertView
					.findViewById(R.id.dateStatePhoto);
		}
	}

	@Override
	public Filter getFilter() {
		return new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence charSequence) {
				FilterResults results = new FilterResults();

				// If there's nothing to filter on, return the original data for
				// your list
				if (charSequence == null || charSequence.length() == 0) {
					results.values = originalData;
					results.count = originalData.size();
				} else {
					ArrayList<BliinderDate> filterResultsData = new ArrayList<BliinderDate>();
					for (BliinderDate data : originalData) {
						// In this loop, we'll filter through originalData and
						// compare each item to charSequence.
						// If we find a match, we add it to our new ArrayList
						if (data.getDateName().startsWith(
								charSequence.toString())) {
							filterResultsData.add(data);
						}
					}
					results.values = filterResultsData;
					results.count = filterResultsData.size();
				}
				return results;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence charSequence,
					FilterResults filterResults) {
				filteredData = (ArrayList<BliinderDate>) filterResults.values;
				notifyDataSetChanged();
			}
		};
	}
}
