package com._1.spring_rest_api.converter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 엔티티 컬렉션과 DTO 컬렉션 간의 변환을 담당하는 유틸리티 클래스
 */
public class ConverterUtils {

    public static <E, D> List<D> toDtoList(List<E> entities, Converter<E, D> converter) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(converter::toDto)
                .collect(Collectors.toList());
    }

    public static <E, D> List<E> toEntityList(List<D> dtos, Converter<E, D> converter) {
        if (dtos == null) {
            return List.of();
        }

        return dtos.stream()
                .map(converter::toEntity)
                .collect(Collectors.toList());
    }
}
