package com.xxx.core.response;

/**
 * Created by wanghua on 17/1/19.
 */
public class PageResponseEntity extends RestResponseEntity {
    private int pageNum;   //最小值为1
    private int pageSize;
    private long total;
    private long pageSum; //总页数

    public PageResponseEntity(int responseStatus, String message, Object data, int pageNum, int pageSize, long total) {
        super(responseStatus, message, data);
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.pageSum = total%pageSize == 0 ? total/pageSize : total/pageSize+1;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getPageSum() {
        return pageSum;
    }

    public void setPageSum(long pageSum) {
        this.pageSum = pageSum;
    }
}
