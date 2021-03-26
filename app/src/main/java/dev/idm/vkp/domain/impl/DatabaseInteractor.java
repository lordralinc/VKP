package dev.idm.vkp.domain.impl;

import java.util.ArrayList;
import java.util.List;

import dev.idm.vkp.api.interfaces.INetworker;
import dev.idm.vkp.api.model.VKApiCity;
import dev.idm.vkp.api.model.VKApiCountry;
import dev.idm.vkp.api.model.database.ChairDto;
import dev.idm.vkp.api.model.database.FacultyDto;
import dev.idm.vkp.api.model.database.SchoolClazzDto;
import dev.idm.vkp.api.model.database.SchoolDto;
import dev.idm.vkp.api.model.database.UniversityDto;
import dev.idm.vkp.db.interfaces.IDatabaseStore;
import dev.idm.vkp.db.model.entity.CountryEntity;
import dev.idm.vkp.domain.IDatabaseInteractor;
import dev.idm.vkp.model.City;
import dev.idm.vkp.model.database.Chair;
import dev.idm.vkp.model.database.Country;
import dev.idm.vkp.model.database.Faculty;
import dev.idm.vkp.model.database.School;
import dev.idm.vkp.model.database.SchoolClazz;
import dev.idm.vkp.model.database.University;
import dev.idm.vkp.util.Utils;
import io.reactivex.rxjava3.core.Single;

public class DatabaseInteractor implements IDatabaseInteractor {

    private final IDatabaseStore cache;
    private final INetworker networker;

    public DatabaseInteractor(IDatabaseStore cache, INetworker networker) {
        this.cache = cache;
        this.networker = networker;
    }

    @Override
    public Single<List<Chair>> getChairs(int accountId, int facultyId, int count, int offset) {
        return networker.vkDefault(accountId)
                .database()
                .getChairs(facultyId, offset, count)
                .map(items -> {
                    List<ChairDto> dtos = Utils.listEmptyIfNull(items.getItems());
                    List<Chair> chairs = new ArrayList<>(dtos.size());

                    for (ChairDto dto : dtos) {
                        chairs.add(new Chair(dto.id, dto.title));
                    }

                    return chairs;
                });
    }

    @Override
    public Single<List<Country>> getCountries(int accountId, boolean ignoreCache) {
        if (ignoreCache) {
            return networker.vkDefault(accountId)
                    .database()
                    .getCountries(true, null, null, 1000)
                    .flatMap(items -> {
                        List<VKApiCountry> dtos = Utils.listEmptyIfNull(items.getItems());
                        List<CountryEntity> dbos = new ArrayList<>(dtos.size());
                        List<Country> countries = new ArrayList<>(dbos.size());

                        for (VKApiCountry dto : dtos) {
                            dbos.add(new CountryEntity(dto.id, dto.title));
                            countries.add(new Country(dto.id, dto.title));
                        }

                        return cache.storeCountries(accountId, dbos)
                                .andThen(Single.just(countries));
                    });
        }

        return cache.getCountries(accountId)
                .flatMap(dbos -> {
                    if (!dbos.isEmpty()) {
                        List<Country> countries = new ArrayList<>(dbos.size());
                        for (CountryEntity dbo : dbos) {
                            countries.add(new Country(dbo.getId(), dbo.getTitle()));
                        }

                        return Single.just(countries);
                    }

                    return getCountries(accountId, true);
                });
    }

    @Override
    public Single<List<City>> getCities(int accountId, int countryId, String q, boolean needAll, int count, int offset) {
        return networker.vkDefault(accountId)
                .database()
                .getCities(countryId, null, q, needAll, offset, count)
                .map(items -> {
                    List<VKApiCity> dtos = Utils.listEmptyIfNull(items.getItems());
                    List<City> cities = new ArrayList<>(dtos.size());

                    for (VKApiCity dto : dtos) {
                        cities.add(new City(dto.id, dto.title)
                                .setArea(dto.area)
                                .setImportant(dto.important)
                                .setRegion(dto.region));
                    }

                    return cities;
                });
    }

    @Override
    public Single<List<Faculty>> getFaculties(int accountId, int universityId, int count, int offset) {
        return networker.vkDefault(accountId)
                .database()
                .getFaculties(universityId, offset, count)
                .map(items -> {
                    List<FacultyDto> dtos = Utils.listEmptyIfNull(items.getItems());
                    List<Faculty> faculties = new ArrayList<>(dtos.size());

                    for (FacultyDto dto : dtos) {
                        faculties.add(new Faculty(dto.id, dto.title));
                    }

                    return faculties;
                });
    }

    @Override
    public Single<List<SchoolClazz>> getSchoolClasses(int accountId, int countryId) {
        return networker.vkDefault(accountId)
                .database()
                .getSchoolClasses(countryId)
                .map(dtos -> {
                    List<SchoolClazz> clazzes = new ArrayList<>(dtos.size());

                    for (SchoolClazzDto dto : dtos) {
                        clazzes.add(new SchoolClazz(dto.id, dto.title));
                    }

                    return clazzes;
                });
    }

    @Override
    public Single<List<School>> getSchools(int accountId, int cityId, String q, int count, int offset) {
        return networker.vkDefault(accountId)
                .database()
                .getSchools(q, cityId, offset, count)
                .map(items -> {
                    List<SchoolDto> dtos = Utils.listEmptyIfNull(items.getItems());
                    List<School> schools = new ArrayList<>(dtos.size());

                    for (SchoolDto dto : dtos) {
                        schools.add(new School(dto.id, dto.title));
                    }

                    return schools;
                });
    }

    @Override
    public Single<List<University>> getUniversities(int accountId, String filter, Integer cityId, Integer countyId, int count, int offset) {
        return networker.vkDefault(accountId)
                .database()
                .getUniversities(filter, countyId, cityId, offset, count)
                .map(items -> {
                    List<UniversityDto> dtos = Utils.listEmptyIfNull(items.getItems());
                    List<University> universities = new ArrayList<>(dtos.size());

                    for (UniversityDto dto : dtos) {
                        universities.add(new University(dto.id, dto.title));
                    }

                    return universities;
                });
    }
}
