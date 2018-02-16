package com.parift.rideshare.dialog;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

import com.parift.rideshare.config.Constant;
import com.parift.rideshare.helper.Logger;

import java.util.Calendar;

/**
 * Created by psarthi on 11/22/17.
 */

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    public static final String TAG = TimePickerFragment.class.getName();
    public TimePickerFragmentListener mTimePickerFragmentListener;
    private Calendar mCalendar;

    public TimePickerFragment() {
    }

    public static TimePickerFragment newInstance(TimePickerFragmentListener listener, Calendar calendar) {
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.mTimePickerFragmentListener = listener;
        timePickerFragment.mCalendar = calendar;
        return timePickerFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //This will ensure we are using user selected previous time and not resetting that
        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = mCalendar.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        Logger.debug(TAG,"Value of HH:MM-"+hourOfDay+":"+minute);
        mTimePickerFragmentListener.onTimeSet(view, hourOfDay, minute);
    }

    public interface TimePickerFragmentListener{

        public void onTimeSet(TimePicker view, int hourOfDay, int minute);
    }
}
