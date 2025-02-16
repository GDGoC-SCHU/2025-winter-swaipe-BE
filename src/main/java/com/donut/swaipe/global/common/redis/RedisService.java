package com.donut.swaipe.global.common.redis;

/**
 * Redis 데이터 접근을 위한 기본 인터페이스입니다.
 * 모든 도메인별 Redis 서비스는 이 인터페이스를 구현해야 합니다.
 *
 * @author donut
 * @version 1.0
 * @since 2024-01-19
 */
public interface RedisService {
	/**
	 * 키-값 쌍을 Redis에 저장하고 만료 시간을 설정합니다.
	 *
	 * @param key 저장할 데이터의 키
	 * @param value 저장할 데이터의 값
	 * @param duration 만료 시간(초)
	 */
	void setDataExpire(String key, String value, long duration);

	/**
	 * 주어진 키에 해당하는 데이터를 조회합니다.
	 *
	 * @param key 조회할 데이터의 키
	 * @return 저장된 데이터 값, 없는 경우 null
	 */
	String getData(String key);

	/**
	 * 주어진 키에 해당하는 데이터를 삭제합니다.
	 *
	 * @param key 삭제할 데이터의 키
	 */
	void deleteData(String key);

	/**
	 * 주어진 키가 존재하는지 확인합니다.
	 *
	 * @param key 확인할 데이터의 키
	 * @return 키 존재 여부
	 */
	boolean hasKey(String key);
}