package com.procheck.intranet.outils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.procheck.intranet.payload.request.DemandeMTSFilter;

public class DateValidator {
	
	
	 public static boolean isValid(String dateStr) {
	        DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	        sdf.setLenient(false);
	        try {
	            sdf.parse(dateStr);
	        } catch (ParseException e) {
	            return false;
	        }
	        return true;
	    }
	 
	 public static LocalDate StringToDate(String date) throws ParseException {
		 
		 LocalDate localDateD=null;
		 if(!date.isEmpty()) {
		 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		 localDateD = LocalDate.parse(date, formatter);
		 }
			
		 
		 return localDateD;
		
	}

}
