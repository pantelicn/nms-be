package com.opdev.talent.availablelocation;

import com.opdev.model.location.TalentAvailableLocation;
import com.opdev.model.talent.Talent;

public interface AvailableLocationService {

    TalentAvailableLocation create(TalentAvailableLocation newAvailableLocation);

    TalentAvailableLocation addCity(Long id, String city, Talent talent);

    TalentAvailableLocation removeCity(Long id, String city, Talent talent);

    void remove(Long id, Talent talent);

}
