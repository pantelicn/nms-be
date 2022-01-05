package com.opdev.dto.paging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class PageDto<T> {
    private int number;
    private int size;
    private int totalPages;
    private int numberOfElements;
    private long totalElements;
    private boolean first;
    private boolean last;
    @Builder.Default
    private List<T> content = new ArrayList<>();
    @Builder.Default
    private List<SortDto> sort = new ArrayList<>();

    /**
     *
     * Converts a {@link Page} to an instance of {@link PageDto}.
     *
     * @param page
     *                    an instance of {@link Page}
     * @param content
     *                    a list of content, usually a list of DTO objects
     * @param <T>
     *                    a generic parameter
     * @return an instance of {@link PageDto}
     */
    public static <T> PageDto<T> from(final Page<?> page, final List<T> content) {
        Objects.requireNonNull(page);

        return PageDto.<T>builder().content(content).first(page.isFirst()).last(page.isLast()).number(page.getNumber())
                .numberOfElements(page.getNumberOfElements()).size(page.getSize())
                .totalElements(page.getTotalElements()).totalPages(page.getTotalPages())
                .sort(SortDto.from(page.getSort())).build();
    }

    /**
     *
     * Creates a {@link PageDto} but with an empty list as a content.
     *
     * @param pageable
     *                     an instance of pageable, used to populate a
     *                     {@link PageDto}
     * @param <T>
     *                     a generic parameter
     * @return an instance of {@link PageDto} with an empty list as a content
     */
    public static <T> PageDto<T> fromEmpty(final Pageable pageable) {
        Objects.requireNonNull(pageable);

        return PageDto.<T>builder().content(Collections.emptyList()).first(true).last(true)
                .number(pageable.getPageNumber()).numberOfElements(0).size(pageable.getPageSize()).totalElements(0)
                .sort(SortDto.from(pageable.getSort())).build();
    }

}
