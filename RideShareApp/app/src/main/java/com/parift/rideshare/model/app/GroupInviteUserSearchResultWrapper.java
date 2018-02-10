package com.parift.rideshare.model.app;

import com.parift.rideshare.model.user.dto.GroupInviteUserSearchResult;

public class GroupInviteUserSearchResultWrapper extends GroupInviteUserSearchResult{
	
	private boolean selected;

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
