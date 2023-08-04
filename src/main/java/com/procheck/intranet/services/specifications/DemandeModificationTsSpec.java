package com.procheck.intranet.services.specifications;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.procheck.intranet.models.PKModifierTimesheet;
import com.procheck.intranet.models.PKPersonnel;
import com.procheck.intranet.payload.request.DemandeMTSFilter;
import com.procheck.intranet.repository.ModificationTSRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DemandeModificationTsSpec {
	
	@Autowired
	private ModificationTSRepository modificationReporsitory;
	
	public Specification<PKModifierTimesheet> getFiltrePersonnel(DemandeMTSFilter dmdMTS) {

		return (root, criteriaQuery, criteriaBuilder) -> {

			List<Predicate> predicates = new ArrayList<>();

			if (dmdMTS.getRecepteur() != null) {
				predicates.add(criteriaBuilder.equal(root.get("recepteur"), dmdMTS.getRecepteur()));
			}
			
			if (dmdMTS.getStatus() != null && !dmdMTS.getStatus().isEmpty()) {
				predicates.add(criteriaBuilder.equal(root.get("status"), dmdMTS.getStatus()));
			}

			if (dmdMTS.getDateDemande() != null ) {
				predicates.add(criteriaBuilder.equal(root.get("dateDemande"), dmdMTS.getDateDemande()));
			}

			if (dmdMTS.getTypeDemande() != null && !dmdMTS.getTypeDemande().isEmpty()) {
				predicates.add(criteriaBuilder.equal(root.get("dateDemande"), dmdMTS.getTypeDemande()));
			}
			
			criteriaQuery.orderBy(criteriaBuilder.desc(root.get("dateDemande")));

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
	
	private Page<PKModifierTimesheet> findCandidaciesByDemandMTS(DemandeMTSFilter dmdMTS, PageRequest pageRequest) {

		int pageNumber = pageRequest.getPageNumber();
		int pageSize = pageRequest.getPageSize();

		Pageable pagereq = PageRequest.of(pageNumber, pageSize, Sort.by("dateDemande").ascending());

		Specification<PKModifierTimesheet> personnelSpec = getFiltrePersonnel(dmdMTS);

		Page<PKModifierTimesheet> pageResult = modificationReporsitory.findAll(personnelSpec, pagereq);

		return new PageImpl<>(pageResult.getContent(), pageRequest, pageResult.getTotalElements());
	}
	
	public Page<PKModifierTimesheet> getDemandMTSsByFilter(DemandeMTSFilter dmdMTS, int size, int page) {
		log.info("Begin getDemandeMTS input : [DemandeMTSFilter : Recepteur : {}   Status : {} TypeDemande : {} Date Demande : {} ] ",
				dmdMTS.getRecepteur(),dmdMTS.getStatus(), dmdMTS.getTypeDemande(), dmdMTS.getDateDemande());
		Page<PKModifierTimesheet> demands = findCandidaciesByDemandMTS(dmdMTS,
				PageRequest.of(Integer.valueOf(page), Integer.valueOf(size)));
		log.info("End getDemandMTS input   : [DemandeMTSFilter : count : {} ]  ", demands.getTotalElements());
		return demands;
	}
}
