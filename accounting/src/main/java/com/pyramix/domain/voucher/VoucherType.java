package com.pyramix.domain.voucher;

public enum VoucherType {
	GENERAL(0), PETTYCASH(1), SALES_CASH(2), SALES_CREDIT(3), PAYMENT_CASH(4), PAYMENT_BANK(5),
	CANCEL(6), POSTING_GENERAL(7), POSTING_PETTYCASH(8), POSTING_SALESCASH(9), POSTING_SALESCREDIT(10),
	POSTING_PAYMENTBANK(11), POSTING_PAYMENTCASH(12);

	private int value;

	private VoucherType(int value) {
		this.setValue(value);
	}

	public String toCode(int value) {
		switch (value) {
			case 0: return "JV";
			case 1: return "PC";
			case 2: return "SO";	// <-- SALES_CASH
			case 3: return "SC";
			case 4: return "PC";
			case 5: return "PB";
			case 6: return "CN";
			// posting voucher codes
			case 7:  return "PJV";
			case 8: return "PPC";
			case 9: return "PSO";	// <-- PAYMENT_SALESCASH
			case 10: return "PSC";
			case 11: return "PPB";
			case 12: return "PPC";
		default:
			return null;
		}
	}
	
	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	
}
