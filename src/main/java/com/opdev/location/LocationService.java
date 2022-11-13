package com.opdev.location;

import com.opdev.model.location.City;
import com.opdev.model.location.Country;

import java.util.List;

public interface LocationService {

    List<Country> getCountries();
    List<Country> addCountries(List<Country> countries);
    Country update(Long id, Country country);
    void deleteCountry(Long id);
    List<City> getCities(Long countryId);
    List<City> addCities(Long countryId, List<City> cities);
    City update(Long id, City city);
    void deleteCity(Long id);

}
