package com.digitusrevolution.rideshare.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.digitusrevolution.rideshare.model.user.domain.Country;

import java.util.List;

/**
 * Created by psarthi on 11/14/17.
 */

public class CustomCountryAdapter extends ArrayAdapter<Country>{

    private List<Country> mCountries;
    private Context mContext;

    public CustomCountryAdapter(@NonNull Context context, List<Country> countries) {
        //Note - We are passing dummy value for resourceId as we will be mentioning the same in getView method explicitly
        super(context, -1, countries);
        mCountries = countries;
        mContext = context;

    }

    private static class ViewHolder {
        TextView countryTextView;
    }

    @Override
    public int getCount() {
        return mCountries.size();
    }

    @Nullable
    @Override
    public Country getItem(int position) {
        return mCountries.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Country country= getItem(position);
        ViewHolder viewHolder;

        if (convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
            //text1 is the id of the TextView configured in spinner_item
            viewHolder.countryTextView = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Reason for adding "+" in the code as while sending the GET request for getting OTP with +91,
        // somehow + gets omitted, so we are storing the data without + and while sending OTP we can drawable.add + from backend
        viewHolder.countryTextView.setText(country.getName() +" (" + country.getCode() +")");
        return convertView;
    }

    //Its important to note that in Spinner this function plays key role in getting the view when you click on drop down
    //If you don't have this and you have just set the setDropDownView on adapter using countryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    //Then adapter would use Object.toString method to get the view on dropdown. So its mandatory to override this whenever you have custom object which is not String
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position,convertView,parent);
    }

}
