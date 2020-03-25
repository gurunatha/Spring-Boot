package io.guruinfotech.coronavirustracker.models;

public class HelplineNumbers {
	
	private String state;
	private String number;
	
	public HelplineNumbers() {
		// TODO Auto-generated constructor stub
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	@Override
	public String toString() {
		return "HelplineNumbers [state=" + state + ", number=" + number + "]";
	}
	
	

}
