package com.parift.rideshare.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parift.rideshare.R;
import com.parift.rideshare.activity.MobileRegistrationActivity;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.model.user.domain.Country;

import java.util.List;

/**
 * Created by psarthi on 11/14/17.
 */

public class CustomCountryAdapter extends ArrayAdapter<Country>{

    private static final String TAG = CustomCountryAdapter.class.getName();
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
        Logger.debug(TAG, "Country Count:"+mCountries.size());
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
            //Don't use simple_spinner_item as it inherits text color etc property from somewhere
            //which makes some times text as white color, so use our own spinner layout so that
            //we are sure we have text color as black
            convertView = inflater.inflate(R.layout.spinner_item, parent, false);
            //text1 is the id of the TextView configured in spinner_item
            viewHolder.countryTextView = convertView.findViewById(android.R.id.text1);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Reason for adding "+" in the code as while sending the GET request for getting OTP with +91,
        // somehow + gets omitted, so we are storing the data without + and while sending OTP we can drawable.add + from backend
        String countryNameWithCode = country.getName() +" (" + country.getCode() +")";
        Logger.debug(TAG, "Country Name with Code:"+countryNameWithCode);
        viewHolder.countryTextView.setText(countryNameWithCode);
        return convertView;
    }

    //Its important to note that in Spinner this function plays key role in getting the view when you click on drop down
    //If you don't have this and you have just set the setDropDownView on adapter using countryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    //Then adapter would use Object.toString method to get the view on dropdown. So its mandatory to override this whenever you have custom object which is not String
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = getView(position,convertView,parent);
        Logger.debug(TAG, "Get Drop Down view:"+((ViewHolder)convertView.getTag()).countryTextView.getText().toString());
        return convertView;
    }

}
