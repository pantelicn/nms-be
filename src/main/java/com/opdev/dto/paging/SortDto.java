package com.opdev.dto.paging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Sort;

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
public class SortDto {
	// TODO: if the `Page<T>` is used as the return type, we get:
	/**
	 * 
	 * ``` "sort": { "unsorted": true, "sorted": false, "empty": true }, ```
	 * 
	 */
	// as part of the response.
	// This is not present in our `SortDto`, therefore we should add it.
	private boolean unsorted;
	private boolean sorted;
	private boolean empty;

	private Sort.Direction direction;
	private String property;
	private boolean ignoreCase;
	private Sort.NullHandling nullHandling;
	private boolean descending;
	private boolean ascending;

	/**
	 * Converts a {@link Sort} to an instance of {@link SortDto}.
	 *
	 * @param sort
	 *                 a {@link Sort}
	 * @return a list of {@link SortDto} objects, or an empty list if the sort
	 *         parameter is <code>null</code>
	 */
	public static List<SortDto> from(final Sort sort) {
		if (sort == null) {
			return Collections.emptyList();
		}

		final List<SortDto> orderList = new ArrayList<>();
		for (final Sort.Order order : sort) {
			final SortDto sortDto = SortDto.builder().ascending(order.isAscending()).descending(order.isDescending())
					.direction(order.getDirection()).ignoreCase(order.isIgnoreCase())
					.nullHandling(order.getNullHandling()).property(order.getProperty()).build();

			orderList.add(sortDto);
		}

		return orderList;
	}

}