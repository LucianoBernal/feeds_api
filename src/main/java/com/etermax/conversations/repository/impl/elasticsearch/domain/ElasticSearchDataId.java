package com.etermax.conversations.repository.impl.elasticsearch.domain;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class ElasticSearchDataId {

	public static final int MONTH_A = 7;
	public static final int MONTH_B = 11;
	public static final int YEAR_A = 3;
	public static final int YEAR_B = 17;
	private int month;
	private int year;
	private String conversationId;

	public ElasticSearchDataId(String conversationId, Date date) {
		this.conversationId = conversationId;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		month = cal.get(Calendar.MONTH);
		year = cal.get(Calendar.YEAR);
	}

	public static void main(String[] args) {
		System.out.println(UUID.randomUUID().toString());
	}

	public String getHashedId() {
		int hashedMonth = month * MONTH_B + MONTH_A;
		int hashedYear = year * YEAR_B + YEAR_A;
		return getConversationHash() + "-" + getUUID() + "-" + hashedMonth + "-" + hashedYear;
	}

	private String getUUID() {
		String[] split = UUID.randomUUID().toString().split("-");
		return split[0] + "-" + split[4];
	}

	private String getConversationHash() {
		try {
			String[] split = conversationId.split("-");
			return split[2] + "-" + split[1];
		}catch (ArrayIndexOutOfBoundsException e){
			return  conversationId;
		}
	}
}
