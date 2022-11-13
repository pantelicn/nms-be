package com.opdev.location;

import com.opdev.location.dto.CityAddDto;
import com.opdev.location.dto.CityViewDto;
import com.opdev.location.dto.CountryAddDto;
import com.opdev.location.dto.CountryViewDto;
import com.opdev.model.location.City;
import com.opdev.model.location.Country;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static com.opdev.config.security.SpELAuthorizationExpressions.IS_ADMIN;
import static com.opdev.config.security.SpELAuthorizationExpressions.IS_AUTHENTICATED;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/countries")
public class CountryController {

    private final LocationService locationService;

    @PreAuthorize(IS_AUTHENTICATED)
    @GetMapping
    public List<CountryViewDto> getCountries() {
        List<Country> found = locationService.getCountries();
        return found.stream().map(CountryViewDto::new).collect(Collectors.toList());
    }

    @PreAuthorize(IS_ADMIN)
    @PostMapping
    public List<Country> addCountries(@Valid @RequestBody List<CountryAddDto> countries) {
        List<Country> newCountries = countries.stream().map(CountryAddDto::toCountry).collect(Collectors.toList());
        return locationService.addCountries(newCountries);
    }

    @PreAuthorize(IS_ADMIN)
    @PutMapping("/{id}")
    public Country updateCountry(@PathVariable Long id, @Valid @RequestBody CountryAddDto country) {
        Country newCountry = country.toCountry();
        return locationService.update(id, newCountry);
    }

    @PreAuthorize(IS_ADMIN)
    @DeleteMapping("/{id}")
    public void deleteCountry(@PathVariable Long id) {
        locationService.deleteCountry(id);
    }

    @PreAuthorize(IS_AUTHENTICATED)
    @GetMapping("/{countryId}/cities")
    public List<CityViewDto> getCities(@PathVariable Long countryId) {
        List<City> cities = locationService.getCities(countryId);
        return cities.stream().map(CityViewDto::new).collect(Collectors.toList());
    }

    @PreAuthorize(IS_ADMIN)
    @PostMapping("/{countryId}/cities")
    public List<City> addCities(@PathVariable Long countryId, @Valid @RequestBody List<CityAddDto> cities) {
        List<City> newCities = cities.stream().map(CityAddDto::toCity).collect(Collectors.toList());
        return locationService.addCities(countryId, newCities);
    }

}
