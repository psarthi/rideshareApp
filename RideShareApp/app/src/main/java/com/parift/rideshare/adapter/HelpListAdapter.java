package com.parift.rideshare.adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parift.rideshare.R;
import com.parift.rideshare.component.FragmentLoader;
import com.parift.rideshare.fragment.BaseFragment;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.model.serviceprovider.domain.core.HelpQuestionAnswer;
import com.google.gson.Gson;

import java.util.List;

public class HelpListAdapter extends RecyclerView.Adapter<HelpListAdapter.ViewHolder> {

    private static final String TAG = RideListAdapter.class.getName();
    private List<HelpQuestionAnswer> mQuestionAnswers;
    private BaseFragment mBaseFragment;
    private CommonUtil mCommonUtil;

    public HelpListAdapter(List<HelpQuestionAnswer> questionAnswers, BaseFragment fragment) {
        mQuestionAnswers = questionAnswers;
        mBaseFragment = fragment;
        mCommonUtil = new CommonUtil(fragment);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_text_item, parent, false);
        HelpListAdapter.ViewHolder vh = new HelpListAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        View view = holder.itemView;
        final HelpQuestionAnswer questionAnswer = mQuestionAnswers.get(position);
        ((TextView) view.findViewById(R.id.row_item_text_view)).setText(questionAnswer.getQuestion());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentLoader fragmentLoader = new FragmentLoader(mBaseFragment);
                fragmentLoader.loadHelpQuestionAnswerFragment(new Gson().toJson(questionAnswer));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mQuestionAnswers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
