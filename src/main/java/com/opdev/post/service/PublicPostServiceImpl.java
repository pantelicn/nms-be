package com.opdev.post.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opdev.location.LocationService;
import com.opdev.model.location.Country;
import com.opdev.model.post.Post;
import com.opdev.post.service.noimpl.PublicPostService;
import com.opdev.repository.PostRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PublicPostServiceImpl implements PublicPostService {

    private final PostRepository repository;
    private final LocationService locationService;

    @Override
    @Transactional(readOnly = true)
    public List<Post> findLatest10ByCountry(@NonNull final String countryName) {
        Country foundCountry = locationService.findByCountryName(countryName);
        return repository.findTop10ByCountryOrderByCreatedOnDesc(foundCountry);
    }
}
