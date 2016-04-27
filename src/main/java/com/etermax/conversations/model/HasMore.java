package com.etermax.conversations.model;

import java.util.Date;

public class HasMore {
	private Integer totalMessages;
	private Date hasMoreFirstDate;
	private Date hasMoreLastDate;

	public HasMore(Integer dataLeft, Date hasMoreFirstDate, Date hasMoreLastDate) {
		if (dataLeft.compareTo(0) < 0) {
			this.totalMessages = 0;
		} else {
			this.totalMessages = dataLeft;
		}
		this.hasMoreFirstDate = hasMoreFirstDate;
		this.hasMoreLastDate = hasMoreLastDate;
	}

	public Integer getTotalMessages() {
		return totalMessages;
	}

	public Date getFirstDate() {
		return hasMoreFirstDate;
	}

	public Date getLastDate() {
		return hasMoreLastDate;
	}

	public Boolean getHasMore() {
		return totalMessages > 0;
	}


}
