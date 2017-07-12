package gttrade.guantang.com.tradeerp.sortlistview;

import java.util.Map;

//SortModel 一个实体类，里面一个是ListView的name,另一个就是显示的name拼音的首字母
public class SortModel {

	private String country;   //显示的数据
	private String sortLetters;  //显示数据拼音的首字母

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
}
