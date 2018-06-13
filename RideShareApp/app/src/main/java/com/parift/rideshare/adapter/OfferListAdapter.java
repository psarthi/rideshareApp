package com.parift.rideshare.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parift.rideshare.R;
import com.parift.rideshare.component.OfferComp;
import com.parift.rideshare.fragment.BaseFragment;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.model.serviceprovider.domain.core.Offer;

import java.util.List;

/**
 * Created by psarthi on 1/13/18.
 */

public class OfferListAdapter extends RecyclerView.Adapter<OfferListAdapter.ViewHolder> {

    private static final String TAG = RideListAdapter.class.getName();
    private List<Offer> mOffers;
    private BaseFragment mBaseFragment;
    private CommonUtil mCommonUtil;

    public OfferListAdapter(List<Offer> offers, BaseFragment fragment) {
        mOffers = offers;
        mBaseFragment = fragment;
        mCommonUtil = new CommonUtil(fragment);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.offer_item, parent, false);
        OfferListAdapter.ViewHolder vh = new OfferListAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        View view = holder.itemView;
        Offer offer = mOffers.get(position);
        OfferComp offerComp = new OfferComp(mBaseFragment, offer);
        offerComp.setBasicOfferLayout(view);
    }

    @Override
    public int getItemCount() {
        return mOffers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
