package com.procheck.intranet.services.impl;

import com.procheck.intranet.exception.*;
import com.procheck.intranet.models.*;
import com.procheck.intranet.outils.DateValidator;
import com.procheck.intranet.outils.Outils;
import com.procheck.intranet.outils.TimeValidator;
import com.procheck.intranet.payload.request.ParamRequest;
import com.procheck.intranet.repository.PaysReporsitory;
import com.procheck.intranet.repository.PersonnelReporsitory;
import com.procheck.intranet.repository.SemaineReporsitory;
import com.procheck.intranet.services.IParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ParamServiceImpl implements IParamService {

    @Autowired
    private SemaineReporsitory semaineReporsitory;

    @Autowired
    private PersonnelReporsitory personnelReporsitory;

    @Autowired
    private PaysReporsitory paysReporsitory;

    DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
    @Override
    public PKSemaineTravail addHoursWork(ParamRequest.ParamWeekHourly paramWeekHourly) throws ExceptionResponse, ParseException {
        PKSemaineTravail pkSemaineTravail = new PKSemaineTravail();
        pkSemaineTravail.setSSemaine(paramWeekHourly.getLibelWeek());
        pkSemaineTravail.setStatus("NOUVEAU");
        List<PKHoraire> pkHoraires = new ArrayList<>();
        List<ParamRequest.Hourly> pkHoraireRequestList = paramWeekHourly.getHourlyList();
        for(ParamRequest.Hourly hourly : pkHoraireRequestList){
            PKHoraire pkHoraire = new PKHoraire();
            pkHoraire.setJour(hourly.getLibelDay());
            if(hourly.getFirstBeginning().length()==0 && hourly.getFirstEnding().length()>0 || hourly.getFirstBeginning().length()>0 && hourly.getFirstEnding().length()==0){
                throw new TechnicalException(TechnicalExceptionType.HOURLY_ERROR);
            }
            if((hourly.getFirstBeginning().length()>0 &&!Outils.isValid(hourly.getFirstBeginning(), TimeValidator.TIME_FORMAT)) || (hourly.getFirstEnding().length()>0 &&!Outils.isValid(hourly.getFirstEnding(), TimeValidator.TIME_FORMAT)) || (hourly.getSecondBeginning().length()>0 &&!Outils.isValid(hourly.getSecondBeginning(),TimeValidator.TIME_FORMAT)) || (hourly.getSecondEnding().length()>0&&!Outils.isValid(hourly.getSecondEnding(), TimeValidator.TIME_FORMAT))){
                throw new TechnicalException(TechnicalExceptionType.TIME_FORMAT_ERROR);
            }
            else if(hourly.getSecondBeginning().length()==0 && hourly.getSecondEnding().length()>0 || hourly.getSecondBeginning().length()>0 && hourly.getSecondEnding().length()==0) {
                throw new TechnicalException(TechnicalExceptionType.HOURLY_ERROR);
            }else{
                if(hourly.getFirstBeginning().length()==0){
                    pkHoraire.setSParty1He("00:00");
                }else{
                    pkHoraire.setSParty1He(hourly.getFirstBeginning());
                }
                if(hourly.getFirstEnding().length()==0){
                    pkHoraire.setSParty1Hs("00:00");
                }else{
                    pkHoraire.setSParty1Hs(hourly.getFirstEnding());
                }
                if(hourly.getSecondBeginning().length()==0){
                    pkHoraire.setSParty2He("00:00");
                }else{
                    pkHoraire.setSParty2He(hourly.getSecondBeginning());
                }

                if(hourly.getSecondEnding().length()==0){
                    pkHoraire.setSParty2Hs("00:00");
                }else {
                    pkHoraire.setSParty2Hs(hourly.getSecondEnding());
                }

               String heurJournee= Outils.FormatageNbHeurs(Outils.getNbHeureDemi(pkHoraire.getSParty1He(), pkHoraire.getSParty1Hs(), pkHoraire.getSParty2He(), pkHoraire.getSParty2Hs()));

               pkHoraire.setHeurJournee(heurJournee);
                
            }
            pkHoraires.add(pkHoraire);
            pkSemaineTravail.setHoraires(pkHoraires);

        }
        try{
            return semaineReporsitory.save(pkSemaineTravail);
        }catch (Exception ex){
            throw new TechnicalException(TechnicalExceptionType.DATABASE_ERROR);
        }

    }

    public List<PKPersonnel> paramHourlyWorkForEmployees(ParamRequest.ParamHourlyWorkEmployesRequest paramHourlyWorkEmployesRequest) throws ExceptionResponse{
        List<PKPersonnel> pkPersonnel = new ArrayList<>();
        for(UUID idPersonnel : paramHourlyWorkEmployesRequest.getIdPersonnel()){
            Optional<PKPersonnel> optionalPersonnel = personnelReporsitory.findById(idPersonnel);
            Optional<PKSemaineTravail> optionalWeekWork = semaineReporsitory.findById(paramHourlyWorkEmployesRequest.getIdWeek());
            if(!optionalPersonnel.isPresent()){
                throw new AccesException(AccesExceptionType.ID_PERSONNEL_NOT_VALID);
            }
            if(!optionalWeekWork.isPresent()){
                throw new AccesException(AccesExceptionType.ID_WEEK_NOT_VALID);
            }
            PKPersonnel personnel = optionalPersonnel.get();
            PKSemaineTravail pkSemaineTravail = optionalWeekWork.get();
            personnel.setSemaineTravail(pkSemaineTravail);
            try{
                PKPersonnel personnelSaved = personnelReporsitory.save(personnel);
                pkPersonnel.add(personnelSaved);


            }catch (Exception e){
                throw new TechnicalException(TechnicalExceptionType.DATABASE_ERROR);
            }

        }
        return pkPersonnel;

    }

    @Override
    public PKPays createCountry(ParamRequest.Country country) throws Exception {
        PKPays pays = new PKPays();
        pays.setCodePays(country.getCodeCountry());
        pays.setLabelPays(country.getLibel());
        try {
            return paysReporsitory.save(pays);
        }catch (Exception e){
            throw new TechnicalException(TechnicalExceptionType.DATABASE_ERROR);
        }
    }

    @Override
    public PKPays addHolidaysCountry(ParamRequest.ParamHolidaysCountry paramHolidaysCountry) throws Exception {
        Optional<PKPays> pkPaysOptional = paysReporsitory.findByCodePays(paramHolidaysCountry.getCodeCountry());
        Set<PKJoureFerie> pkJoureFerieSet = new HashSet<>();
        PKPays pkPays = null;
        if(!pkPaysOptional.isPresent()){
            pkPays = new PKPays();
            pkPays.setCodePays(paramHolidaysCountry.getCodeCountry());
        }
        else{
            pkPays = pkPaysOptional.get();
        }
        for(ParamRequest.ParamHolidays paramHolidays: paramHolidaysCountry.getParamHolidays()){
            PKJoureFerie pkJoureFerie = new PKJoureFerie();
            pkJoureFerie.setPays(pkPays);
            if(!DateValidator.isValid(paramHolidays.getDateHoliday())){
                throw new TechnicalException(TechnicalExceptionType.DATE_FORMAT_ERROR);
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    		LocalDate date = LocalDate.parse(paramHolidays.getDateHoliday(), formatter);
            pkJoureFerie.setDateJoureFerie(date);
            pkJoureFerie.setSDescriptionJoureFerie(paramHolidays.getLibelHoliday());
            pkJoureFerieSet.add(pkJoureFerie);
        }
        pkPays.setPJoureFerie(pkJoureFerieSet);
        try{
            return paysReporsitory.save(pkPays);
        }catch (Exception e){
            throw new TechnicalException(TechnicalExceptionType.DATABASE_ERROR);
        }
    }

    @Override
    public List<PKPersonnel> paramEmployeesHolidays(ParamRequest.ParamEmploymentHolidays paramEmploymentHolidays) throws Exception {
        Optional<PKPays> pkPaysOptional = paysReporsitory.findByCodePays(paramEmploymentHolidays.getCodeCountry());
        List<PKPersonnel> personnelList = new ArrayList<>();
        if(!pkPaysOptional.isPresent()){
            throw new TechnicalException(TechnicalExceptionType.COUNTRY_DOES_NOT_EXIST);
        }
        PKPays pkPays = pkPaysOptional.get();
        for(UUID idPersonnel : paramEmploymentHolidays.getIdPersonnels()){
            Optional<PKPersonnel> optionalPersonnel = personnelReporsitory.findById(idPersonnel);
            if(!optionalPersonnel.isPresent()){
                throw new TechnicalException(TechnicalExceptionType.PERSONNEL_DOES_NOT_EXIST);
            }
            PKPersonnel personnel = optionalPersonnel.get();
            personnel.setPkPays(pkPays);
            personnelList.add(personnel);
            try{
                personnelReporsitory.saveAll(personnelList);
            }catch (Exception e){
                throw new TechnicalException(TechnicalExceptionType.DATABASE_ERROR);
            }
        }
        return personnelList;
    }

    @Override
    public List<PKSemaineTravail> findAllWeekWork() throws Exception {
        try {
           return semaineReporsitory.findAll();
        }catch(Exception ex){
            throw new TechnicalException(TechnicalExceptionType.DATABASE_ERROR);
        }
    }


}
