package com.procheck.intranet.controllers;


import com.procheck.intranet.models.PKPersonnel;
import com.procheck.intranet.models.PKSemaineTravail;
import com.procheck.intranet.payload.request.ParamRequest;
import com.procheck.intranet.payload.response.MessageResponse;
import com.procheck.intranet.repository.PaysReporsitory;
import com.procheck.intranet.services.IParamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/param/")
public class ParamController {

	@Autowired
	private IParamService paramService;

	@Autowired
	private PaysReporsitory paysReporsitory;

	@GetMapping("/week")
	@PreAuthorize("hasRole('findAll_semaine')")
	public ResponseEntity<List<PKSemaineTravail>> findWeekTravail() throws Exception {
		try {
			return new ResponseEntity<>(paramService.findAllWeekWork(), HttpStatus.OK);

		} catch (Exception ex) {
			throw ex;
		}
	}

	@PostMapping("WeekHourly")
	@PreAuthorize("hasRole('create_semaine')")
	public ResponseEntity<PKSemaineTravail> paramWeekHWork(@RequestBody ParamRequest.ParamWeekHourly paramWeekHourly)
			throws Exception {
		try {
			return new ResponseEntity<>(paramService.addHoursWork(paramWeekHourly), HttpStatus.OK);
		} catch (Exception ex) {
			throw ex;

		}
	}

	@PostMapping("HourlyParam")
	@PreAuthorize("hasRole('hourly_param')")
	public ResponseEntity<List<PKPersonnel>> paramHourlyParam(
			@RequestBody ParamRequest.ParamHourlyWorkEmployesRequest paramHourlyWorkEmployesRequest) throws Exception {
		try {
			return new ResponseEntity<>(paramService.paramHourlyWorkForEmployees(paramHourlyWorkEmployesRequest),
					HttpStatus.OK);
		} catch (Exception e) {
			throw e;
		}
	}

	@PostMapping("createCountry")
	@PreAuthorize("hasRole('create_country')")
	public ResponseEntity<?> createCountry(@RequestBody ParamRequest.Country country) throws Exception {
		try {
			if (paysReporsitory.existsByCodePays(country.getCodeCountry())) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: country is exist !"));
			}
			return new ResponseEntity<>(paramService.createCountry(country), HttpStatus.OK);

		} catch (Exception e) {
			throw e;
		}
	}

	@PostMapping("createCountryHolliday")
	@PreAuthorize("hasRole('create_country_holliday')")
	public ResponseEntity<?> addHolidaysCountry(
			@RequestBody ParamRequest.ParamHolidaysCountry paramHolidaysCountry) throws Exception {
		try {
			if (!paysReporsitory.existsByCodePays(paramHolidaysCountry.getCodeCountry())) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: country is not exist !"));
			}
			return new ResponseEntity<>(paramService.addHolidaysCountry(paramHolidaysCountry), HttpStatus.OK);

		} catch (Exception e) {
			throw e;
		}
	}

}
