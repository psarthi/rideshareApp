package com.parift.rideshare.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.parift.rideshare.R;
import com.parift.rideshare.activity.HomePageActivity;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.helper.RESTClient;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WebPageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WebPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WebPageFragment extends BaseFragment {
    public static final String TAG = WebPageFragment.class.getName();

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_URL = "url";
    private static final String ARG_PAGE_TITLE = "title";

    // TODO: Rename and change types of parameters
    private String mUrl;
    private String mPageTitle;
    private CommonUtil mCommonUtil;
    private WebView mWebView;

    private OnFragmentInteractionListener mListener;

    public WebPageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param url URL of the web page
     * @param pageTitle Title of the web fragment
     * @return A new instance of fragment WebPageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WebPageFragment newInstance(String url, String pageTitle) {
        WebPageFragment fragment = new WebPageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        args.putString(ARG_PAGE_TITLE, pageTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUrl = getArguments().getString(ARG_URL);
            mPageTitle = getArguments().getString(ARG_PAGE_TITLE);
        }
        mCommonUtil = new CommonUtil(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_web_page, container, false);
        mWebView = view.findViewById(R.id.content_web_view);
        Logger.debug(TAG, "URL is:"+mUrl);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.loadUrl(mUrl);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(mPageTitle);
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
        // TODO: Update argument type and name
        void onWebPageFragmentInteraction(String data);
    }
}
