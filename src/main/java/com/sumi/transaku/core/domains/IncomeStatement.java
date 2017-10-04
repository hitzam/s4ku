package com.sumi.transaku.core.domains;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class IncomeStatement {
	
	private String trxType;
	private double credit;
	private double debit;

	@JsonFormat(pattern="yyyy-MM-dd hh:mm:ss", timezone="Asia/Jakarta")
	private Date trxDate;
	private String note;
	
	
	public String getTrxType() {
		return trxType;
	}
	public void setTrxType(String trxType) {
		this.trxType = trxType;
	}
	public double getCredit() {
		return credit;
	}
	public void setCredit(double credit) {
		this.credit = credit;
	}
	public double getDebit() {
		return debit;
	}
	public void setDebit(double debit) {
		this.debit = debit;
	}
	public Date getTrxDate() {
		return trxDate;
	}
	public void setTrxDate(Date trxDate) {
		this.trxDate = trxDate;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	
	@Override
	public String toString() {
		return "IncomeStatement [credit=" + credit + ", debit=" + debit + ", trxDate=" + trxDate + ", note=" + note
				+ "]";
	}
	
}
