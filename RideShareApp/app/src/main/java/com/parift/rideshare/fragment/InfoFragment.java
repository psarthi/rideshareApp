package com.parift.rideshare.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parift.rideshare.R;
import com.parift.rideshare.activity.HomePageActivity;
import com.parift.rideshare.component.FragmentLoader;
import com.parift.rideshare.config.APIUrl;
import com.parift.rideshare.config.Constant;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.model.user.dto.BasicUser;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InfoFragment extends BaseFragment {
    public static final String TAG = InfoFragment.class.getName();
    public static final String TITLE = "Information";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentLoader mFragmentLoader;

    private OnFragmentInteractionListener mListener;
    private CommonUtil mCommonUtil;

    public InfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InfoFragment newInstance(String param1, String param2) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mFragmentLoader = new FragmentLoader(this);
        mCommonUtil = new CommonUtil(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        view.findViewById(R.id.website).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentLoader.loadWebPageFragment(APIUrl.WEBSITE_URL,
                        getResources().getString(R.string.website));
            }
        });
        view.findViewById(R.id.terms_of_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentLoader.loadWebPageFragment(APIUrl.TERMS_OF_SERVICE_URL,
                        getResources().getString(R.string.terms_of_service));
            }
        });
        view.findViewById(R.id.privacy_policy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentLoader.loadWebPageFragment(APIUrl.PRIVACY_POLICY_URL,
                        getResources().getString(R.string.privacy_policy));
            }
        });
        view.findViewById(R.id.contact_us).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeMail();
            }
        });
        String version = null;
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String appVersion = getResources().getString(R.string.app_version) + version;
        ((TextView) view.findViewById(R.id.app_version)).setText(appVersion);
        return view;
    }

    private void composeMail() {
        BasicUser user = mCommonUtil.getUser();
        String[] addresses = {Constant.CONTACT_US_EMAIL_ID};
        String subject = Constant.CONTACT_US_EMAIL_SUBJECT.replace(APIUrl.USER_ID_KEY, Long.toString(user.getId()));
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(TITLE);
        ((HomePageActivity) getActivity()).showBackButton(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            mActivity = (FragmentActivity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //This will take care of dismissing progress dialog so that we don't get NPE (not attached to window manager)
        //This happens when you make http call which is async and when response comes, activity is no longer there
        //and then when dismissProgressDialog is called it will throw error
        mCommonUtil.dismissProgressDialog();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onInfoFragmentInteraction(String data);
    }
}
