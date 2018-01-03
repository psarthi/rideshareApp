package com.digitusrevolution.rideshare.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.config.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by psarthi on 11/21/17.
 */

public class BaseFragment extends Fragment{

    public static final String TAG = BaseFragment.class.getName();
    private ProgressDialog mProgressDialog;

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

        //Don't use simple_spinner_item as it inherits text color etc property from somewhere
        //which makes some times text as white color, so use our own spinner layout so that
        //we are sure we have text color as black
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_item,arrayList);
        // Specify the layout to use when the list of choices appears
        // Don't setDropDownView here and instead overwrite getDropDownView since we are using Object instead of String.
        //If you are using plan String in ArrayAdapter then no need to write custom adapter and below set function would do the job
        //Don't use simple_dropdown_spinner_item as it inherits text color etc property from somewhere
        //which makes some times text as white color, so use our own spinner layout so that
        //we are sure we have text color as black
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
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
        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    //Note - Not using commonUtil show/dismiss function from convinience perspective
    //Let it be duplicate for the time being until we get better solution
    public void showProgressDialog(){
        mProgressDialog = ProgressDialog.show(getActivity(), "", Constant.LOADING_MSG);
    }

    public void dismissProgressDialog(){
        mProgressDialog.dismiss();
    }


}
