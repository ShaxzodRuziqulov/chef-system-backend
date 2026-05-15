package com.example.oshpazbackendsystem.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * PageImpl serializatsiya warningini oldini olish uchun barqaror sahifalash DTO.
 * Spring Data'ning PageImpl o'rniga shu sinfdan foydalanamiz.
 */
@Getter
@Builder
public class PageResponse<T> {

    private final List<T>  content;
    private final int      totalPages;
    private final long     totalElements;
    private final int      size;
    private final int      number;
    private final boolean  first;
    private final boolean  last;
    private final boolean  empty;

    /** Spring Page → PageResponse konvertatsiya */
    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .size(page.getSize())
                .number(page.getNumber())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();
    }
}
