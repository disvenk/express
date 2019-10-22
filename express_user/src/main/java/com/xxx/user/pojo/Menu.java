package com.xxx.user.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User:
 * Date: 2017/3/9/0009
 * Time: 19:18
 * To change this template use File | Settings | File Templates.
 */
public class Menu {
	public int id;
	public String name;
	public String code;
	public String iconURL;
	public Boolean isComplete;
	public Boolean isSubmenu = false;
	public List<Menu> submenu = new ArrayList();
	public String description;//说明


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Menu(int id, String name, String code, String iconURL, Boolean isComplete) {
		this.id = id;
		this.name = name;
		this.code = code;
		this.iconURL = iconURL;

		this.isComplete = isComplete;

	}
}