package com.yadda.demo;

import java.io.Serializable;
import java.util.List;

public class Student implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;

	private int age;

	private List<String> fav;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public List<String> getFav() {
		return fav;
	}

	public void setFav(List<String> fav) {
		this.fav = fav;
	}

}
