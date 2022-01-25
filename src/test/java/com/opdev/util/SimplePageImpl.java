package com.opdev.util;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SimplePageImpl<T> implements Page<T> {

    private Page<T> delegate;

    public SimplePageImpl() { }

    public SimplePageImpl(@JsonProperty("content") List<T> content,
                          @JsonProperty("number") int number,
                          @JsonProperty("size") int size,
                          @JsonProperty("totalElements") @JsonAlias({"total-elements", "total_elements", "totalelements", "TotalElements"}) long totalElements) {
            if (size > 0) {
                PageRequest pageRequest;
                pageRequest = PageRequest.of(number, size);

                this.delegate = new PageImpl(content, pageRequest, totalElements);
            } else {
                this.delegate = new PageImpl(content);
            }

    }

    @JsonIgnore
    public int getTotalPages() {
        return this.delegate.getTotalPages();
    }

    @JsonProperty
    public long getTotalElements() {
        return this.delegate.getTotalElements();
    }

    @JsonProperty
    public int getNumber() {
        return this.delegate.getNumber();
    }

    @JsonProperty
    public int getSize() {
        return this.delegate.getSize();
    }

    @JsonIgnore
    public int getNumberOfElements() {
        return this.delegate.getNumberOfElements();
    }

    @JsonProperty
    public List<T> getContent() {
        return this.delegate.getContent();
    }

    @JsonProperty
    public boolean hasContent() {
        return this.delegate.hasContent();
    }

    @JsonIgnore
    public Sort getSort() {
        return this.delegate.getSort();
    }

    @JsonIgnore
    public boolean isFirst() {
        return this.delegate.isFirst();
    }

    @JsonIgnore
    public boolean isLast() {
        return this.delegate.isLast();
    }

    @JsonIgnore
    public boolean hasNext() {
        return this.delegate.hasNext();
    }

    @JsonIgnore
    public boolean hasPrevious() {
        return this.delegate.hasPrevious();
    }

    @JsonIgnore
    public Pageable nextPageable() {
        return this.delegate.nextPageable();
    }

    @JsonIgnore
    public Pageable previousPageable() {
        return this.delegate.previousPageable();
    }

    @JsonIgnore
    public <S> Page<S> map(Function<? super T, ? extends S> converter) {
        return this.delegate.map(converter);
    }

    @JsonIgnore
    public Iterator<T> iterator() {
        return this.delegate.iterator();
    }

    @JsonIgnore
    public Pageable getPageable() {
        return this.delegate.getPageable();
    }

    @JsonIgnore
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }
}

