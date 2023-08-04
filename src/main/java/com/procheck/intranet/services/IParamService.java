package com.procheck.intranet.services;

import com.procheck.intranet.models.PKPays;
import com.procheck.intranet.models.PKPersonnel;
import com.procheck.intranet.models.PKSemaineTravail;
import com.procheck.intranet.payload.request.ParamRequest;

import java.util.List;

public interface IParamService {

    PKSemaineTravail addHoursWork(ParamRequest.ParamWeekHourly paramWeekHourly) throws Exception;

    List<PKPersonnel> paramHourlyWorkForEmployees(ParamRequest.ParamHourlyWorkEmployesRequest paramHourlyWorkEmployesRequest) throws Exception;

    PKPays createCountry(ParamRequest.Country country) throws Exception;

    PKPays addHolidaysCountry(ParamRequest.ParamHolidaysCountry paramHolidaysCountry) throws Exception;

    List<PKPersonnel> paramEmployeesHolidays(ParamRequest.ParamEmploymentHolidays paramEmploymentHolidays) throws Exception;

    List<PKSemaineTravail> findAllWeekWork() throws Exception;


}
