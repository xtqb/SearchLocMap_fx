package com.lhzw.searchlocmap.bean;

/**
 * Created by xtqb on 2019/3/26.
 */
public class SearchInfo {
    private int total;
    private int searchNum;

    public SearchInfo(int total, int searchNum) {
        this.total = total;
        this.searchNum = searchNum;
    }

    public int getSearchNum() {
        return searchNum;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setSearchNum(int searchNum) {
        this.searchNum = searchNum;
    }
}
