package com.opdev.model.search;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class LocationFilter {

    private final String country;
    private final List<String> cities;

}
