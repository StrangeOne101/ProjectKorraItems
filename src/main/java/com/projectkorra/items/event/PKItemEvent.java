package com.projectkorra.items.event;

import com.projectkorra.items.PKItem;

public abstract class PKItemEvent extends PKIEvent {
	
	private PKItem item;
	
	public PKItem getItem() {
		return item;
	}
	
	protected void setItem(PKItem item) {
		this.item = item;
	}

	public PKItemEvent(PKItem item) {
		this.item = item;
	}

}
