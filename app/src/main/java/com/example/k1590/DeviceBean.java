package com.example.k1590;

/**
 * 实体类
 * 保存蓝牙设备信息
 */
public class DeviceBean {
	//声明变量
	protected String message;
	protected boolean isReceive;

	public DeviceBean(String msg, boolean isReceive) {
		this.message = msg;
		this.isReceive = isReceive;
	}
}