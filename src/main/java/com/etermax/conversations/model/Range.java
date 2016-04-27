package com.etermax.conversations.model;

import com.etermax.conversations.error.InvalidRangeException;

public class Range {
	private Long firstDate;
	private Long lastDate;

	public Range(Long firstDate, Long lastDate) throws InvalidRangeException {
		this.firstDate = firstDate;
		this.lastDate = lastDate;
	}

	public boolean isInRange(Long date) {
		return isEmpty() || ((firstDate == null || date > firstDate)
				&& ( lastDate == null || date <= lastDate));
	}

	public boolean isInInclusiveRange(Long date) {
		return isEmpty() || ((firstDate == null || date >= firstDate)
				&& ( lastDate == null || date <= lastDate));
	}

	private boolean isEmpty() {
		return firstDate == null && lastDate == null;
	}


	public Long getFirstDate() {
		return firstDate;
	}

	public Long getLastDate() {
		return lastDate;
	}
}
