package com.etermax.conversations.model;

public interface ReceiptVisitor {
	void visit(ReadType readType);

	void visit(ReceivedType receivedType);
}
