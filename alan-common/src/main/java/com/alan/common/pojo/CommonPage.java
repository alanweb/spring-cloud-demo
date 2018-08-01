package com.alan.common.pojo;

import java.util.List;

/**
 * Created by Administrator on 2016/7/6.
 */
public class CommonPage {
	/**
	 * 当前页
	 */
	private int pageNo;
	/**
	 * 每页显示数
	 */
	private int pageSize;
	/**
	 * 总记录数
	 */
	private long totalCount;
	/**
	 * 数据项
	 */
	private List<? extends Object> datas;

	public CommonPage() {
		setPageNo(1);
		setPageSize(10);
	}

	public CommonPage(int pageNo, int pageSize) {
		this.pageNo = pageNo;
		this.pageSize = pageSize;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		if (pageNo < 1) {
			pageNo = 1;
		}
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		if (pageSize < 1) {
			this.pageSize = 10;
		}
		this.pageSize = pageSize;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		if (totalCount < 0) {
			this.totalCount = 0;
		}
		this.totalCount = totalCount;
	}

	/**
	 * 获取第一条记录数
	 * 
	 * @return
	 */
	public int getStartNo() {
		if (pageNo > 0 && pageSize > 0) {
			return (pageNo - 1) * pageSize;
		} else {
			return 0;
		}
	}

	/**
	 * 获取总页数
	 * 
	 * @return
	 */
	public long getTotalPage() {
		if (totalCount == 0) {
			return 1;
		} else {
			return totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;
		}
	}

	public List<? extends Object> getDatas() {
		return datas;
	}

	public void setDatas(List<? extends Object> datas) {
		this.datas = datas;
	}

}
