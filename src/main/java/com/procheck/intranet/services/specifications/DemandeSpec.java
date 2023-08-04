package com.procheck.intranet.services.specifications;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.procheck.intranet.models.PKDemande;
import com.procheck.intranet.models.PKPersonnel;
import com.procheck.intranet.payload.request.DemandeFilter;
import com.procheck.intranet.payload.request.PersonnelFilterByService;
import com.procheck.intranet.repository.DemandeReporsitory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DemandeSpec {
	
	@Autowired
	private DemandeReporsitory demandeReporsitory;
	
	public Specification<PKDemande> getFiltreDemande(DemandeFilter demande) {

		return (root, criteriaQuery, criteriaBuilder) -> {

			List<Predicate> predicates = new ArrayList<>();
			
			Join<PKDemande, PKPersonnel> joinNom  = root.join("personnel");

			if (demande.getMatricule() != null && !demande.getMatricule().equals(null) && !demande.getMatricule().isEmpty()) {
				predicates.add(criteriaBuilder.equal(joinNom.get("sMatruculePaie"),demande.getMatricule()));
			}
			if (demande.getNom() != null && !demande.getNom().equals(null) && !demande.getNom().isEmpty()) {
				predicates.add(criteriaBuilder.equal(joinNom.get("sNom"),demande.getNom()));
			}
			if (demande.getPrenom() != null && !demande.getPrenom().equals(null) && !demande.getPrenom().isEmpty()) {
				predicates.add(criteriaBuilder.equal(joinNom.get("sPrenom"),demande.getPrenom()));
			}
			if (demande.getCodeSuper() != null && !demande.getCodeSuper().equals(null)) {
				predicates.add(criteriaBuilder.equal(root.get("codeSup"), demande.getCodeSuper()));
			}
			if (demande.getDateDemande() != null) {
				predicates.add(criteriaBuilder.equal(root.get("dDateCreation"), demande.getDateDemande()));
			}
			if (demande.getStatus() != null && !demande.getStatus().isEmpty()) {
				predicates.add(criteriaBuilder.equal(root.get("status"), demande.getStatus()));
			}
			
			criteriaQuery.orderBy(criteriaBuilder.desc(root.get("dDateCreation")));

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}

	private List<PKDemande> findCandidaciesByDemande(DemandeFilter demande) {

		Specification<PKDemande> demandeSpec = getFiltreDemande(demande);

		List<PKDemande> pageResult = demandeReporsitory.findAll(demandeSpec);

		return pageResult;
	}

	public List<PKDemande> getDemandsByFilter(DemandeFilter demandFilter) {
		log.info(
				"Begin getDemands input : [DemandsFilter : Utilisateur : {}  NOM : {} PRENOM : {} Matrucule : {} date :{} status :{}] ",
				demandFilter.getCodeSuper(),demandFilter.getNom(),demandFilter.getPrenom(),demandFilter.getMatricule(),demandFilter.getDateDemande(),demandFilter.getStatus());
		List<PKDemande> personnels = findCandidaciesByDemande(demandFilter);
		log.info("End getDemands input   : [DemandFilter : count : {} ]  ", personnels.size());
		return personnels;
	}

}
