package com.etermax.conversations.model;

public interface ReceiptType {
	void accept(ReceiptVisitor readResetter);
	String toString();
}
