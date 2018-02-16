package com.parift.rideshare.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

import com.parift.rideshare.helper.Logger;

import java.util.Calendar;

/**
 * Created by psarthi on 11/22/17.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public static final String TAG = DatePickerFragment.class.getName();
    public DatePickerFragmentListener mDatePickerFragmentListener;
    private Calendar mCalendar;

    public DatePickerFragment() {
    }

    public static DatePickerFragment newInstance(DatePickerFragmentListener listener, Calendar calendar) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.mDatePickerFragmentListener = listener;
        datePickerFragment.mCalendar = calendar;
        return datePickerFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        Logger.debug(TAG,"Value of DD/MM/YY-"+day+"/"+month+"/"+year);
        mDatePickerFragmentListener.onDateSet(view, year, month, day);

    }

    public interface DatePickerFragmentListener{
        public void onDateSet(DatePicker view, int year, int month, int day);
    }
}
