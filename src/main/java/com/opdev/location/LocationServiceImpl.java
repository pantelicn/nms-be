package com.opdev.location;

import com.opdev.exception.ApiEntityNotFoundException;
import com.opdev.model.location.City;
import com.opdev.model.location.Country;
import com.opdev.repository.CityRepository;
import com.opdev.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;

    @Transactional(readOnly = true)
    @Override
    public List<Country> getCountries() {
        LOGGER.info("Fetching all countries");
        return countryRepository.findAll();
    }

    @Transactional
    @Override
    public List<Country> addCountries(List<Country> countries) {
        LOGGER.info("Saving {} countries", countries.size());
        return countryRepository.saveAll(countries);
    }

    @Transactional
    @Override
    public Country update(Long id, Country country) {
        validateCountryExists(id);
        country.setId(id);
        LOGGER.info("Updating country with id {}", id);
        return save(country);
    }

    @Transactional
    @Override
    public void deleteCountry(Long id) {
        validateCountryExists(id);
        LOGGER.info("Deleting country with id {}", id);
        countryRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<City> getCities(Long countryId) {
        Country foundCountry = getById(countryId);
        return foundCountry.getCities();
    }

    @Transactional
    @Override
    public List<City> addCities(Long countryId, List<City> cities) {
        Country foundCountry = getById(countryId);
        foundCountry.getCities().addAll(cities);
        Country updated = save(foundCountry);
        LOGGER.info("Added {} cities to country with id {}", cities.size(), countryId);
        return updated.getCities();
    }

    @Transactional
    @Override
    public City update(Long id, City city) {
        validateCityExists(id);
        city.setId(id);
        LOGGER.info("Updating city with id {}", id);
        return save(city);
    }

    @Transactional
    @Override
    public void deleteCity(Long id) {
        validateCityExists(id);
        cityRepository.deleteById(id);
    }

    private Country getById(Long id) {
        return countryRepository.findById(id)
                .orElseThrow(() -> ApiEntityNotFoundException.builder().entity("Country").id(id.toString()).build());
    }

    private void validateCountryExists(Long id) {
        ApiEntityNotFoundException.builder()
                .entity("Country")
                .id(id.toString())
                .build()
                .throwIf(() -> !countryExistsById(id));
    }

    private void validateCityExists(Long id) {
        ApiEntityNotFoundException.builder()
                .entity("City")
                .id(id.toString())
                .build()
                .throwIf(() -> !cityExistsById(id));
    }

    private boolean countryExistsById(Long id) {
        return countryRepository.existsById(id);
    }

    private boolean cityExistsById(Long cityId) {
        return cityRepository.existsById(cityId);
    }

    private Country save(Country country) {
        return countryRepository.save(country);
    }

    private City save(City city) {
        return cityRepository.save(city);
    }

}
