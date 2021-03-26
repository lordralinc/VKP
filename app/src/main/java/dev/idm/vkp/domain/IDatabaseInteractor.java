package dev.idm.vkp.domain;

import java.util.List;

import dev.idm.vkp.model.City;
import dev.idm.vkp.model.database.Chair;
import dev.idm.vkp.model.database.Country;
import dev.idm.vkp.model.database.Faculty;
import dev.idm.vkp.model.database.School;
import dev.idm.vkp.model.database.SchoolClazz;
import dev.idm.vkp.model.database.University;
import io.reactivex.rxjava3.core.Single;

public interface IDatabaseInteractor {
    Single<List<Chair>> getChairs(int accountId, int facultyId, int count, int offset);

    Single<List<Country>> getCountries(int accountId, boolean ignoreCache);

    Single<List<City>> getCities(int accountId, int countryId, String q, boolean needAll, int count, int offset);

    Single<List<Faculty>> getFaculties(int accountId, int universityId, int count, int offset);

    Single<List<SchoolClazz>> getSchoolClasses(int accountId, int countryId);

    Single<List<School>> getSchools(int accountId, int cityId, String q, int count, int offset);

    Single<List<University>> getUniversities(int accountId, String filter, Integer cityId, Integer countyId, int count, int offset);
}
