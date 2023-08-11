package com.procheck.intranet.services.specifications;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.procheck.intranet.models.PKPersonnel;
import com.procheck.intranet.payload.request.PersonnelFilter;
import com.procheck.intranet.repository.PersonnelReporsitory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PersonnelFilterAll {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private PersonnelReporsitory personnelReporsitory;

	public Specification<PKPersonnel> getFiltrePersonnel(PersonnelFilter personnel) {

		return (root, criteriaQuery, criteriaBuilder) -> {

			List<Predicate> predicates = new ArrayList<>();

			if (personnel.getCin() != null && !personnel.getCin().isEmpty()) {
				predicates.add(criteriaBuilder.equal(root.get("cin"), personnel.getCin()));
			}

			if (personnel.getNom() != null && !personnel.getNom().isEmpty()) {
				predicates.add(criteriaBuilder.equal(root.get("sNom"), personnel.getNom()));
			}

			if (personnel.getPrenom() != null && !personnel.getPrenom().isEmpty()) {
				predicates.add(criteriaBuilder.equal(root.get("sPrenom"), personnel.getPrenom()));
			}
			if (personnel.getSPoste() != null && !personnel.getSPoste().isEmpty()) {
				predicates.add(criteriaBuilder.equal(root.get("sPoste"), personnel.getSPoste()));
			}
			
			criteriaQuery.orderBy(criteriaBuilder.desc(root.get("sNom")));

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}

	private Page<PKPersonnel> findCandidaciesByPersonnel(PersonnelFilter personnel, PageRequest pageRequest) {
		
		int pageNumber = pageRequest.getPageNumber();
		int pageSize = pageRequest.getPageSize();

		Pageable pagereq = PageRequest.of(pageNumber, pageSize, Sort.by("sNom").ascending());

		Specification<PKPersonnel> personnelSpec = getFiltrePersonnel(personnel);

		Page<PKPersonnel> pageResult = personnelReporsitory.findAll(personnelSpec, pagereq);

		return new PageImpl<>(pageResult.getContent(), pageRequest, pageResult.getTotalElements());
	}

	public Page<PKPersonnel> getPersonnelsByFilter(PersonnelFilter personnelFilter, int size, int page) {
		log.info("Begin getPersonnels input : [PersonnelFilter : CIN : {} NOM : {} PRENOM : {} POSTE : {} ] ",
				personnelFilter.getCin(), personnelFilter.getNom(), personnelFilter.getPrenom(),
				personnelFilter.getSPoste());
		Page<PKPersonnel> personnels = findCandidaciesByPersonnel(personnelFilter,
				PageRequest.of(Integer.valueOf(page), Integer.valueOf(size)));
		log.info("End getPersonnels input   : [PersonnelFilter : count : {} ]  ", personnels.getTotalElements());
		return personnels;
	}

}
