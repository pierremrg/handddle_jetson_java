package com.handddle.farm.farm_master;

public class AjaxResponse {

	public static final int AJAX_ERROR_NO_ERROR = 0;
	
	private int code;
	private boolean success;
	private String message;
	
	/**
	 * Create a new AjaxResponse
	 */
	public AjaxResponse() {
		code = AJAX_ERROR_NO_ERROR;
		success = true;
		message = null;
	}

	/**
	 * Return the code of the response
	 * @return The code of the response
	 */
	public int getCode() {
		return code;
	}

	/**
	 * Set the code of the response.
	 * Set success to true if the code is equal AJAX_ERROR_NO_ERROR, false otherwise.
	 * @param code The code to set.
	 */
	public void setCode(int code) {
		this.code = code;
		
		if(code == 0)
			setSuccess(true);
		else
			setSuccess(false);
	}

	/**
	 * Indicate the status of the response
	 * @return The status of the response
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * Set the status of the response
	 * @param success The status of the response
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}

	/**
	 * Return the message of the response
	 * @return The message of the response
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Set the message of the response
	 * @param message The message of the response
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
}
