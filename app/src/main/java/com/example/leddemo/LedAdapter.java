package com.example.leddemo;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class LedAdapter extends BaseAdapter {
    private List<listInfo> list;
    private Context context;

    public  LedAdapter(List<listInfo> list, Context context) {
        this.list = list;
        this.context = context;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder ;
        if (convertView == null) {
            viewHolder = new ViewHolder() ;
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, null);
            viewHolder.text1 = (TextView) convertView.findViewById(R.id.text1);
            viewHolder.text2 = (TextView) convertView.findViewById(R.id.text2);
            viewHolder.text3 = (TextView) convertView.findViewById(R.id.text3);
            viewHolder.text4 = (TextView) convertView.findViewById(R.id.text4);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (list != null && !list.isEmpty()) {
            listInfo tag = list.get(position);
            viewHolder.text1.setText(tag.getId());
            viewHolder.text2.setText(tag.getTypeA());
            viewHolder.text3.setText(tag.getTypeB());
            viewHolder.text4.setText(tag.getUnit());
        }
        return convertView;
    }

    class ViewHolder{
        TextView text1;
        TextView text2;
        TextView text3;
        TextView text4;
    }
}
