package com._1.spring_rest_api.converter;

/**
 * 엔티티와 DTO 간의 변환을 담당하는 기본 인터페이스
 *
 * @param <E> 엔티티 타입
 * @param <D> DTO 타입
 */
public interface Converter<E, D> {

    D toDto(E entity);

    E toEntity(D dto);
}