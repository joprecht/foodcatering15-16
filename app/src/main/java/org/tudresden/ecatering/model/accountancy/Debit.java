package org.tudresden.ecatering.model.accountancy;

import org.salespointframework.payment.PaymentMethod;

public class Debit extends PaymentMethod {
	private int iban;
	private int bic;
	public Debit(String description, int iban, int bic) {
		super(description);
		this.iban = iban;
		this.bic = bic;
	}
	public int getIban() {
		return iban;
	}
	public int getBic() {
		return bic;
	}
	
}