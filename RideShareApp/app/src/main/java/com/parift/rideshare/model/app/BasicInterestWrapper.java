package com.parift.rideshare.model.app;

import com.parift.rideshare.model.user.dto.BasicInterest;

/**
 * Created by psarthi on 3/30/18.
 */

public class BasicInterestWrapper extends BasicInterest {

    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
