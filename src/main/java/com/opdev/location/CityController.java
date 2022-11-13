package com.opdev.location;

import com.opdev.location.dto.CityAddDto;
import com.opdev.model.location.City;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.opdev.config.security.SpELAuthorizationExpressions.IS_ADMIN;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/cities/{id}")
public class CityController {

    private final LocationService locationService;

    @PreAuthorize(IS_ADMIN)
    @PutMapping
    public City updateCity(@PathVariable Long id, @Valid @RequestBody CityAddDto city) {
        City newCity = city.toCity();
        return locationService.update(id, newCity);
    }

    @PreAuthorize(IS_ADMIN)
    @DeleteMapping
    public void deleteCity(@PathVariable Long id) {
        locationService.deleteCity(id);
    }

}
