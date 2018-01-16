package com.digitusrevolution.rideshare.model.app;

import com.digitusrevolution.rideshare.model.user.dto.GroupInviteUserSearchResult;

public class GroupInviteUserSearchResultWrapper extends GroupInviteUserSearchResult{
	
	private boolean selected;

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
