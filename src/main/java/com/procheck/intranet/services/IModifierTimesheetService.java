package com.procheck.intranet.services;

import java.util.List;
import java.util.UUID;

import com.procheck.intranet.models.PKModifierTimesheet;

public interface IModifierTimesheetService {

	void save(PKModifierTimesheet modifierTimesheet);

	List<PKModifierTimesheet> findAll();

	PKModifierTimesheet findModifierTimesheetById(UUID id);

	PKModifierTimesheet update(PKModifierTimesheet modifierTimesheet);

	void delete(PKModifierTimesheet modifierTimesheet);
}
