package com.digitusrevolution.rideshare.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by psarthi on 11/21/17.
 */

public class BaseFragment extends Fragment{

    public static final String TAG = BaseFragment.class.getName();

    public void showBackStackDetails(){

        int count = getActivity().getSupportFragmentManager().getBackStackEntryCount();
        Log.d(TAG,"Fragment count in backstack is:"+count);
        for (int i=0; i< count;i++){
            String name = getActivity().getSupportFragmentManager().getBackStackEntryAt(i).getName();
            int id = getActivity().getSupportFragmentManager().getBackStackEntryAt(i).getId();
            Log.d(TAG, "Backstack entry [name,id] at index "+i+":"+name+","+id);
        }
    }

    public void showChildFragmentDetails(){

        List<Fragment> fragments = getChildFragmentManager().getFragments();
        Log.d(TAG,"Child Fragment details of:"+this.getTag());
        Log.d(TAG,"Child Fragment count is:"+fragments.size());
        for (int i=0; i< fragments.size();i++){
            Log.d(TAG, "Child Fragment Id:"+Integer.toString(fragments.get(i).getId()));
            if (fragments.get(i).getId()!=0) Log.d(TAG, "Child Container Resource Name of Id:"+getResources().getResourceName(fragments.get(i).getId()));
        }
    }

    public void populateSpinner(ArrayList<String> arrayList, Spinner spinner){

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,arrayList);
        // Specify the layout to use when the list of choices appears
        // Don't setDropDownView here and instead overwrite getDropDownView since we are using Object instead of String.
        //If you are using plan String in ArrayAdapter then no need to write custom adapter and below set function would do the job
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

    }

    public void setRecyclerView(RecyclerView recyclerView, RecyclerView.Adapter adapter, int orientation){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), orientation, false);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    public void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        if(imm.isAcceptingText()) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }


}
