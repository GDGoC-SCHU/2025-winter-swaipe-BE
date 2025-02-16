package com.donut.swaipe.global.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {

	private String code;
	private String message;
	private T data;

	// 데이터가 있는 응답을 위한 메서드
	public static <T> ApiResponse<T> success(MessageCode messageCode, T data) {
		return new ApiResponse<>(messageCode.getCode(), messageCode.getMessage(), data);
	}

	// 데이터가 없는 응답을 위한 메서드
	public static ApiResponse<Void> success(MessageCode messageCode) {
		return new ApiResponse<>(messageCode.getCode(), messageCode.getMessage(), null);
	}

	// 에러 응답을 위한 메서드
	public static <T> ApiResponse<T> error(MessageCode messageCode) {
		return new ApiResponse<>(messageCode.getCode(), messageCode.getMessage(), null);
	}
}
