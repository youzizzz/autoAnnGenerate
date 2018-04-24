package com.youzi.autoAnn.entity;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import com.youzi.autoAnn.Annotation.Igone;

/**
 * 用户类
 * 
 * @author Administrator
 *
 */
@Repository
public class User implements Serializable {
	@Igone
	private static final long serialVersionUID = 1L;
	
	/**
	 * 用户ID
	 */
	private Integer id;
	/**
	 * 用户名称
	 */
	private String name;
	/**
	 * 用户地址
	 */
	private String address;
	/**
	 * 用户地址
	 */
	private String telephone;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", address=" + address
				+ ", telephone=" + telephone + "]";
	}
	public User(Integer id, String name, String address, String telephone) {
		super();
		this.id = id;
		this.name = name;
		this.address = address;
		this.telephone = telephone;
	}
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

}
