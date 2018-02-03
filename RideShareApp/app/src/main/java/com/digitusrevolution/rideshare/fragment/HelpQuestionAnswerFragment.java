package com.digitusrevolution.rideshare.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.activity.HomePageActivity;
import com.digitusrevolution.rideshare.model.serviceprovider.domain.core.HelpQuestionAnswer;
import com.google.gson.Gson;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HelpQuestionAnswerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HelpQuestionAnswerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HelpQuestionAnswerFragment extends BaseFragment {
    public static final String TAG = HelpQuestionAnswerFragment.class.getName();
    public static final String TITLE = "Help";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_QUESTION_ANSWER = "questionAnswer";

    // TODO: Rename and change types of parameters
    private String mQuestionAnswerData;
    private HelpQuestionAnswer mQuestionAnswer;
    private OnFragmentInteractionListener mListener;

    public HelpQuestionAnswerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param questionAnswer help question answer
     * @return A new instance of fragment HelpQuestionAnswerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HelpQuestionAnswerFragment newInstance(String questionAnswer) {
        HelpQuestionAnswerFragment fragment = new HelpQuestionAnswerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUESTION_ANSWER, questionAnswer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mQuestionAnswerData = getArguments().getString(ARG_QUESTION_ANSWER);
        }
        mQuestionAnswer = new Gson().fromJson(mQuestionAnswerData, HelpQuestionAnswer.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_help_question_answer, container, false);
        ((TextView) view.findViewById(R.id.question)).setText(mQuestionAnswer.getQuestion());
        ((TextView) view.findViewById(R.id.answer)).setText(Html.fromHtml(mQuestionAnswer.getAnswer()));
        return view;
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
    public void onResume() {
        super.onResume();
        getActivity().setTitle(TITLE);
        ((HomePageActivity) getActivity()).showBackButton(true);
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
        void onHelpQuestionAnswerFragmentInteraction(String data);
    }
}
