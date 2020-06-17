package com.example.lts;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CustomListAdapter extends ArrayAdapter<CustomList> {
    private static final String LOG_TAG = CustomListAdapter.class.getSimpleName();

    public CustomListAdapter(Activity context, List<CustomList> listItems) {
        super(context, 0, listItems);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        CustomList list = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list, parent, false);
        }

        ImageView iconView = (ImageView) convertView.findViewById(R.id.list_item_icon);
        iconView.setImageResource(list.image);

        TextView versionNameView = (TextView) convertView.findViewById(R.id.txt_subheading1);
        String text= list.leaveType.toUpperCase()+ " application &lt;br&gt; for the period: "+list.fromDate+" to "+list.toDate+ " ["+String.valueOf(list.daysCount)+
                 " days] &lt;br&gt; Applied on:"+list.entryDate;
        versionNameView.setText(Html.fromHtml(Html.fromHtml(text).toString()));

        TextView versionNumberView = (TextView) convertView.findViewById(R.id.txt_heading);
        versionNumberView.setText(list.username);

        return convertView;
    }
}
