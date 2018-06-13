package com.parift.rideshare.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parift.rideshare.R;
import com.parift.rideshare.component.RewardCouponTransactionComp;
import com.parift.rideshare.component.RewardReimbursementTransactionComp;
import com.parift.rideshare.fragment.BaseFragment;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.model.serviceprovider.domain.core.RewardCouponTransaction;
import com.parift.rideshare.model.serviceprovider.domain.core.RewardReimbursementTransaction;

import java.util.List;

/**
 * Created by psarthi on 1/13/18.
 */

public class RewardCouponTransactionListAdapter extends RecyclerView.Adapter<RewardCouponTransactionListAdapter.ViewHolder> {

    private static final String TAG = RideListAdapter.class.getName();
    private List<RewardCouponTransaction> mRewardCouponTransactions;
    private BaseFragment mBaseFragment;
    private CommonUtil mCommonUtil;

    public RewardCouponTransactionListAdapter(List<RewardCouponTransaction> rewardCouponTransactions, BaseFragment fragment) {
        mRewardCouponTransactions = rewardCouponTransactions;
        mBaseFragment = fragment;
        mCommonUtil = new CommonUtil(fragment);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reward_transaction_item, parent, false);
        RewardCouponTransactionListAdapter.ViewHolder vh = new RewardCouponTransactionListAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        View view = holder.itemView;
        RewardCouponTransaction rewardCouponTransaction = mRewardCouponTransactions.get(position);
        RewardCouponTransactionComp rewardCouponTransactionComp = new RewardCouponTransactionComp(mBaseFragment, rewardCouponTransaction);
        rewardCouponTransactionComp.setBasicRewardTransactionLayout(view);
    }

    @Override
    public int getItemCount() {
        return mRewardCouponTransactions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
