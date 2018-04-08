package com.example.listview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Sabrina Chira and Dillion Sykes on 4/3/2018.
 * referenced: https://developer.android.com/training/improving-layouts/smooth-scrolling.html
 * referenced: Keith Perkins's website
 */

public class CustomAdapter extends ArrayAdapter<BikeData>  {
    private Viewholder myVh;
    List<BikeData> bikeList;
    private LayoutInflater inflater;
    private final Context ctx;
    private String MYURL;

    public CustomAdapter(Context context, int resource, List<BikeData> bikes, String URL) {
        super(context, resource, bikes);
        this.ctx = context;
        this.bikeList = bikes;
        this.MYURL = URL;
    }

    @Override
    public int getCount() {
        return bikeList.size();
    }

    @Override
    public BikeData getItem(int position) {
        return bikeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public static class Viewholder {
        TextView tv_price;
        TextView tv_description;
        TextView tv_model;
        ImageView tv_image;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Viewholder myVH;

        //if cannot recycle, then create a new one, this should only happen
        //with first screen of data (or rows)
        if (convertView == null) {
            if (inflater == null)
                inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //get a row
            convertView = inflater.inflate(R.layout.listview_row_layout, null);

            //create a viewholder for effeciency (and for thread usage)
            myVH = new Viewholder();

            //get refs to widgets
            myVH.tv_price = (TextView) convertView.findViewById(R.id.Price);
            myVH.tv_description = (TextView) convertView.findViewById(R.id.Description);
            myVH.tv_model = (TextView) convertView.findViewById(R.id.Model);
            myVH.tv_image = (ImageView) convertView.findViewById(R.id.imageView1);

            //marry the viewholder to the convertview row
            convertView.setTag(myVH);
        }


        myVH = (Viewholder) convertView.getTag();

        //set the first field
        myVH.tv_price.setText("$ " + getItem(position).Price);
        myVH.tv_description.setText(getItem(position).Description);
        myVH.tv_model.setText(getItem(position).Model);

        DownloadImageTask myTask = new DownloadImageTask(MYURL, myVH.tv_image);
        myTask.execute(MYURL + getItem(position).Picture);

        return convertView;
    }

}
