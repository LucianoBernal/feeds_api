package com.etermax.conversations.model;

public class ReadType implements ReceiptType {

	@Override
	public void accept(ReceiptVisitor readResetter) {
		readResetter.visit(this);
	}

	@Override
	public String toString(){
		return  "read";
	}

}
