package com.digitusrevolution.rideshare.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.component.FragmentLoader;
import com.digitusrevolution.rideshare.component.RideComp;
import com.digitusrevolution.rideshare.config.APIUrl;
import com.digitusrevolution.rideshare.fragment.BaseFragment;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.helper.RESTClient;
import com.digitusrevolution.rideshare.model.billing.domain.core.Purpose;
import com.digitusrevolution.rideshare.model.billing.domain.core.Transaction;
import com.digitusrevolution.rideshare.model.billing.domain.core.TransactionType;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRide;
import com.digitusrevolution.rideshare.model.ride.dto.FullRide;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by psarthi on 12/9/17.
 */

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private static final String TAG = TransactionAdapter.class.getName();
    private List<Transaction> mTransactions;
    private BaseFragment mBaseFragment;
    private String mCurrencySymbol;
    private CommonUtil mCommonUtil;


    public TransactionAdapter(List<Transaction> transactions, BaseFragment fragment) {
        mTransactions = transactions;
        mBaseFragment = fragment;
        mCommonUtil = new CommonUtil(fragment);
        mCurrencySymbol = mCommonUtil.getCurrencySymbol(mCommonUtil.getUser().getCountry());
        Log.d(TAG, "Transaction Count is:"+transactions.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mDateTextView;
        private TextView mPurposeTextView;
        private TextView mRidesIdTextView;
        private TextView mAmountTextView;
        private ImageView mTransactionTypeImageView;
        private TextView mPersonTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mDateTextView = itemView.findViewById(R.id.transaction_date);
            mPurposeTextView = itemView.findViewById(R.id.transaction_purpose);
            mTransactionTypeImageView = itemView.findViewById(R.id.transaction_type);
            mAmountTextView = itemView.findViewById(R.id.transaction_amount);
            mPersonTextView = itemView.findViewById(R.id.transaction_person);
            mRidesIdTextView = itemView.findViewById(R.id.transaction_rides_id);
        }
    }


    @Override
    public TransactionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_item, parent, false);
        TransactionAdapter.ViewHolder vh = new TransactionAdapter.ViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(TransactionAdapter.ViewHolder holder, final int position) {
        Transaction transaction = getItem(position);

        String date = mCommonUtil.getFormattedDateTimeString(transaction.getDateTime());
        holder.mDateTextView.setText(date);

        holder.mPurposeTextView.setText(transaction.getRemark().getPurpose().toString());
        String amount = mCurrencySymbol+mCommonUtil.getDecimalFormattedString(transaction.getAmount());
        holder.mAmountTextView.setText(amount);

        if (transaction.getType().equals(TransactionType.Debit)){
            holder.mTransactionTypeImageView.setImageResource(R.drawable.ic_minus);
            holder.mPersonTextView.setText(transaction.getRemark().getPaidTo().split(" ")[0]);
            if (!transaction.getRemark().getPurpose().equals(Purpose.Ride)) {
                holder.mRidesIdTextView.setVisibility(View.GONE);
            } else {
                holder.mRidesIdTextView.setVisibility(View.VISIBLE);
                String rideText = mBaseFragment.getResources().getString(R.string.ride_request_id_text) +transaction.getRemark().getRideRequestId();
                holder.mRidesIdTextView.setText(rideText);
            }
        } else {
            holder.mPersonTextView.setText(transaction.getRemark().getPaidBy().split(" ")[0]);
            holder.mTransactionTypeImageView.setImageResource(R.drawable.ic_add);
            if (!transaction.getRemark().getPurpose().equals(Purpose.Ride)) {
                holder.mRidesIdTextView.setVisibility(View.GONE);
            } else {
                holder.mRidesIdTextView.setVisibility(View.VISIBLE);
                String rideText = mBaseFragment.getResources().getString(R.string.ride_offer_id_text) +transaction.getRemark().getRideId();
                holder.mRidesIdTextView.setText(rideText);
            }
        }
    }

    public Transaction getItem(int position){
        return mTransactions.get(position);
    }

    @Override
    public int getItemCount() {
        return mTransactions.size();
    }

}
