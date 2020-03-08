package com.parift.rideshare.adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parift.rideshare.R;
import com.parift.rideshare.fragment.BaseFragment;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.model.billing.domain.core.Transaction;
import com.parift.rideshare.model.billing.domain.core.TransactionType;

import java.util.List;

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
        Logger.debug(TAG, "Transaction Count is:"+transactions.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mDateTextView;
        private TextView mPurposeTextView;
        private TextView mTransactionIdTextView;
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
            mTransactionIdTextView = itemView.findViewById(R.id.transaction_id);
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
        if (mBaseFragment.isAdded()){
            String transactionId = mBaseFragment.getResources().getString(R.string.transaction_id_text) +transaction.getId();
            holder.mTransactionIdTextView.setText(transactionId);
        }

        if (transaction.getType().equals(TransactionType.Debit)){
            holder.mTransactionTypeImageView.setImageResource(R.drawable.ic_minus);
            holder.mPersonTextView.setText(transaction.getRemark().getPaidTo().split(" ")[0]);
        } else {
            holder.mPersonTextView.setText(transaction.getRemark().getPaidBy().split(" ")[0]);
            holder.mTransactionTypeImageView.setImageResource(R.drawable.ic_add);
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
