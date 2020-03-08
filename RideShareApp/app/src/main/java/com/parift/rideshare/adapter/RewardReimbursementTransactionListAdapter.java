package com.parift.rideshare.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parift.rideshare.R;
import com.parift.rideshare.component.RewardReimbursementTransactionComp;
import com.parift.rideshare.fragment.BaseFragment;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.model.serviceprovider.domain.core.RewardReimbursementTransaction;

import java.util.List;

/**
 * Created by psarthi on 1/13/18.
 */

public class RewardReimbursementTransactionListAdapter extends RecyclerView.Adapter<RewardReimbursementTransactionListAdapter.ViewHolder> {

    private static final String TAG = RideListAdapter.class.getName();
    private List<RewardReimbursementTransaction> mRewardReimbursementTransactions;
    private BaseFragment mBaseFragment;
    private CommonUtil mCommonUtil;

    public RewardReimbursementTransactionListAdapter(List<RewardReimbursementTransaction> rewardReimbursementTransactions, BaseFragment fragment) {
        mRewardReimbursementTransactions = rewardReimbursementTransactions;
        mBaseFragment = fragment;
        mCommonUtil = new CommonUtil(fragment);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reward_transaction_item, parent, false);
        RewardReimbursementTransactionListAdapter.ViewHolder vh = new RewardReimbursementTransactionListAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        View view = holder.itemView;
        RewardReimbursementTransaction rewardReimbursementTransaction = mRewardReimbursementTransactions.get(position);
        RewardReimbursementTransactionComp rewardReimbursementTransactionComp = new RewardReimbursementTransactionComp(mBaseFragment, rewardReimbursementTransaction);
        rewardReimbursementTransactionComp.setBasicRewardTransactionLayout(view);
    }

    @Override
    public int getItemCount() {
        return mRewardReimbursementTransactions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
