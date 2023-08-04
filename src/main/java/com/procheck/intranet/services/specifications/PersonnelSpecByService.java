package com.procheck.intranet.services.specifications;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.EntityManager;

import javax.persistence.criteria.CriteriaBuilder;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.procheck.intranet.models.PKPersonnel;
import com.procheck.intranet.payload.request.PersonnelFilterByService;
import com.procheck.intranet.repository.PersonnelReporsitory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PersonnelSpecByService {


	@Autowired
	private PersonnelReporsitory personnelReporsitory;

	public Specification<PKPersonnel> getFiltrePersonnel(PersonnelFilterByService personnel) {

		return (root, criteriaQuery, criteriaBuilder) -> {

			List<Predicate> predicates = new ArrayList<>();

			if (personnel.getIdServices() != null && !personnel.getIdServices().equals(null)) {
				Expression<UUID> uuid_exp = root.get("service").<UUID>get("id");
				predicates.add(uuid_exp.in(personnel.idServices));
			}
			if (personnel.getCin() != null && !personnel.getCin().isEmpty()) {
				predicates.add(criteriaBuilder.equal(root.get("cin"), personnel.getCin()));
			}
			if (personnel.getSNom() != null && !personnel.getSNom().isEmpty()) {
				predicates.add(criteriaBuilder.equal(root.get("sNom"), personnel.getSNom()));
			}
			if (personnel.getSPrenom() != null && !personnel.getSPrenom().isEmpty()) {
				predicates.add(criteriaBuilder.equal(root.get("sPrenom"), personnel.getSPrenom()));
			}
			if (personnel.getSMatruculePaie() != null && !personnel.getSMatruculePaie().isEmpty()) {
				predicates.add(criteriaBuilder.equal(root.get("sMatruculePaie"), personnel.getSMatruculePaie()));
			}
			if (personnel.getSPoste() != null && !personnel.getSPoste().isEmpty()) {
				predicates.add(criteriaBuilder.equal(root.get("sPoste"), personnel.getSPoste()));
			}
			if (!Objects.isNull(personnel.getBProjetTs())) {
				predicates.add(criteriaBuilder.equal(root.get("bProjetTs"), personnel.bProjetTs));
			}

			criteriaQuery.orderBy(criteriaBuilder.desc(root.get("service")));

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}

	private List<PKPersonnel> findCandidaciesByPersonnel(PersonnelFilterByService personnel) {

		Specification<PKPersonnel> personnelSpec = getFiltrePersonnel(personnel);

		List<PKPersonnel> pageResult = personnelReporsitory.findAll(personnelSpec);

		return pageResult;
	}

	public List<PKPersonnel> getPersonnelsByFilter(PersonnelFilterByService personnelFilter) {
		log.info(
				"Begin getPersonnels input : [PersonnelFilter :SERVICE : {} CIN : {} NOM : {} PRENOM : {} Matrucule : {} Post :{}] ",
				personnelFilter.getIdServices(), personnelFilter.getCin(), personnelFilter.getSNom(),
				personnelFilter.getSPrenom(), personnelFilter.getSMatruculePaie(), personnelFilter.getSPoste());
		List<PKPersonnel> personnels = findCandidaciesByPersonnel(personnelFilter);
		log.info("End getPersonnels input   : [PersonnelFilter : count : {} ]  ", personnels.size());
		return personnels;
	}

}
