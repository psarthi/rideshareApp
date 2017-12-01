package com.digitusrevolution.rideshare.fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

import com.digitusrevolution.rideshare.config.Constant;
import com.digitusrevolution.rideshare.model.app.RideType;

import java.util.Calendar;

/**
 * Created by psarthi on 11/22/17.
 */

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    public static final String TAG = TimePickerFragment.class.getName();
    public TimePickerFragmentListener mTimePickerFragmentListener;

    public TimePickerFragment() {
    }

    public static TimePickerFragment newInstance(TimePickerFragmentListener listener) {
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.mTimePickerFragmentListener = listener;
        return timePickerFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        //This will add extra time from current time, so that we don't get into issues of creating rides in the past
        c.add(Calendar.MINUTE, Constant.START_TIME_INCREMENT);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        Log.d(TAG,"Value of HH:MM-"+hourOfDay+":"+minute);
        mTimePickerFragmentListener.onTimeSet(view, hourOfDay, minute);
    }

    public interface TimePickerFragmentListener{

        public void onTimeSet(TimePicker view, int hourOfDay, int minute);
    }
}
