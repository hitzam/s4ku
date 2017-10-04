package com.sumi.transaku.core.domains;

import java.util.Map;

import org.springframework.http.HttpStatus;

public class ResponseModel {

	private HttpStatus status;
	private boolean success;
	private int msgCode;
	private String msg;
	private Map<String, String> additionalInfo;
	private Object data;
	
	public ResponseModel(){
		
	}
	
	public ResponseModel(HttpStatus status, int msgCode, String msg, Object data) {
		super();
		this.status = status;
		this.msgCode = msgCode;
		this.msg = msg;
		this.data = data;
	}
	
	public ResponseModel(HttpStatus status, boolean success, int msgCode, String msg, Object data) {
		super();
		this.status = status;
		this.success = success;
		this.msgCode = msgCode;
		this.msg = msg;
		this.data = data;
	}
	
	public ResponseModel(HttpStatus status, boolean success, int msgCode, String msg, Map<String, String> additionalInfo, Object data) {
		super();
		this.status = status;
		this.success = success;
		this.msgCode = msgCode;
		this.msg = msg;
		this.additionalInfo = additionalInfo;
		this.data = data;
	}
	
	public ResponseModel(HttpStatus status, int msgCode, String msg) {
		super();
		this.status = status;
		this.msgCode = msgCode;
		this.msg = msg;
	}
	
	public ResponseModel(HttpStatus status, boolean success, int msgCode, String msg) {
		super();
		this.status = status;
		this.success = success;
		this.msgCode = msgCode;
		this.msg = msg;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public int getMsgCode() {
		return msgCode;
	}

	public void setMsgCode(int msgCode) {
		this.msgCode = msgCode;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Map<String, String> getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(Map<String, String> additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	
	/*public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}*/

	
	
}