package com.lhzw.searchlocmap.utils;

import com.lhzw.searchlocmap.bean.HttpPersonInfo;

import java.util.Comparator;

public class PinyinComparator implements Comparator<HttpPersonInfo> {

	public int compare(HttpPersonInfo o1, HttpPersonInfo o2) {
		if (o1.getLetters().equals("@")
				|| o2.getLetters().equals("#")) {
			return -1;
		} else if (o1.getLetters().equals("#")
				|| o2.getLetters().equals("@")) {
			return 1;
		} else {
			return o1.getLetters().compareTo(o2.getLetters());
		}
	}

}
