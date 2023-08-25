package com.procheck.intranet.services.specifications;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

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
			if (personnel.getNom() != null && !personnel.getNom().isEmpty()) {
				predicates.add(criteriaBuilder.equal(root.get("nom"), personnel.getNom()));
			}
			if (personnel.getPrenom() != null && !personnel.getPrenom().isEmpty()) {
				predicates.add(criteriaBuilder.equal(root.get("prenom"), personnel.getPrenom()));
			}
			if (personnel.getMatruculePaie() != null && !personnel.getMatruculePaie().isEmpty()) {
				predicates.add(criteriaBuilder.equal(root.get("matruculePaie"), personnel.getMatruculePaie()));
			}
			if (personnel.getPoste() != null && !personnel.getPoste().isEmpty()) {
				predicates.add(criteriaBuilder.equal(root.get("poste"), personnel.getPoste()));
			}
			if (!Objects.isNull(personnel.getProjetTs())) {
				predicates.add(criteriaBuilder.equal(root.get("projetTs"), personnel.projetTs));
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
				personnelFilter.getIdServices(), personnelFilter.getCin(), personnelFilter.getNom(),
				personnelFilter.getPrenom(), personnelFilter.getMatruculePaie(), personnelFilter.getPoste());
		List<PKPersonnel> personnels = findCandidaciesByPersonnel(personnelFilter);
		log.info("End getPersonnels input   : [PersonnelFilter : count : {} ]  ", personnels.size());
		return personnels;
	}

}
