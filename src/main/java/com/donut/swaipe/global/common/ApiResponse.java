package com.donut.swaipe.global.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {

	private boolean success;
	private String code;
	private String message;
	private T data;

	public static <T> ApiResponse<T> success(MessageCode messageCode, T data) {
		return new ApiResponse<>(true, messageCode.getCode(), messageCode.getMessage(), data);
	}

	public static <T> ApiResponse<T> error(MessageCode messageCode) {
		return new ApiResponse<>(false, messageCode.getCode(), messageCode.getMessage(), null);
	}
}
