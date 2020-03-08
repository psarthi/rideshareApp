package com.parift.rideshare.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.parift.rideshare.R;
import com.parift.rideshare.component.UserComp;
import com.parift.rideshare.helper.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by psarthi on 11/21/17.
 */

public class BaseFragment extends Fragment implements UserComp.OnUserCompListener{

    public static final String TAG = BaseFragment.class.getName();
    private ProgressDialog mProgressDialog;
    public FragmentActivity mActivity;
    private String mUserMobileNumber;

    public void showBackStackDetails(){

        int count = getActivity().getSupportFragmentManager().getBackStackEntryCount();
        Logger.debug(TAG,"Fragment count in backstack is:"+count);
        for (int i=0; i< count;i++){
            String name = getActivity().getSupportFragmentManager().getBackStackEntryAt(i).getName();
            int id = getActivity().getSupportFragmentManager().getBackStackEntryAt(i).getId();
            Logger.debug(TAG, "Backstack entry [name,id] at index "+i+":"+name+","+id);
        }
    }

    public void showChildFragmentDetails(){

        List<Fragment> fragments = getChildFragmentManager().getFragments();
        Logger.debug(TAG,"Child Fragment details of:"+this.getTag());
        Logger.debug(TAG,"Child Fragment count is:"+fragments.size());
        for (int i=0; i< fragments.size();i++){
            Logger.debug(TAG, "Child Fragment Id:"+Integer.toString(fragments.get(i).getId()));
            if (fragments.get(i).getId()!=0) Logger.debug(TAG, "Child Container Resource Name of Id:"+getResources().getResourceName(fragments.get(i).getId()));
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

    @Override
    public void call(String number) {
        mUserMobileNumber = number;
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 1);
        } else {
            startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:"+mUserMobileNumber)));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    call(mUserMobileNumber);
                } else {
                    Logger.debug("TAG", "Call Permission Not Granted");
                }
                break;
            default:
                break;
        }
    }
}
