package com.procheck.intranet.payload.request;

import lombok.*;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public enum ParamRequest {
    ;
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class ParamWeekHourly {

        private String libelWeek;
        private String status;
        private List<Hourly> hourlyList;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class Hourly{
        private String libelDay;
        private String firstBeginning;
        private String firstEnding;
        private String secondBeginning;
        private String secondEnding;
        
        private double hoursDay;
        private double lunchbreak;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class ParamHourlyWorkEmployesRequest{
        private List<UUID> idPersonnel;
        private UUID idWeek;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class Country{
        private String libel;
        private String codeCountry;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class ParamHolidaysCountry{
        private String codeCountry;
        private List<ParamHolidays> paramHolidays;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class ParamHolidays{
        private String libelHoliday;
        private String dateHoliday;
    }


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class ParamEmploymentHolidays{
        private String codeCountry;
        private Collection<UUID> idPersonnels;
    }

    
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public class ParamAssignUserEmployee implements Serializable{
    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
	public String affecte;
    }
}
