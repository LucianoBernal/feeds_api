package com.etermax.conversations.model;

public class ReceivedType implements ReceiptType{

	@Override
	public void accept(ReceiptVisitor readResetter) {
		readResetter.visit(this);
	}

	@Override
	public String toString(){
		return  "received";
	}
}
