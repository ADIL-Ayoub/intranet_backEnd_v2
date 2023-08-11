package com.procheck.intranet.models;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;


import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

@Entity
@Table(name = "t_personnel", schema = "public")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PKPersonnel extends BaseEntity {

	@Column(name = "s_code_personnel")
	private String sCodePersonnel;
	@Column(name = "s_societe")
	private String sSociete;
	@Column(name = "s_nom")
	private String nom;
	@Column(name = "s_prenom")
	private String prenom;
	@Column(name = "s_civilite")
	private String sCivilite;
	@Column(name = "d_date_naissance")
	private Date dDateNaissance;
	@Column(name = "s_lieu_naissance")
	private String sLieuNaissance;
	@Column(name = "s_nationalite")
	private String sNationalite;
	@Column(name = "s_situation_famille")
	private String sSituationFamille;
	@Column(name = "d_date_mariage")
	private Date dDateMariage;
	@Column(name = "n_nombre_enfant")
	private int nNombreEnfant;
	@Column(name = "s_nom_epouse")
	private String sNomEpouse;
	@Column(name = "s_adresse_1")
	private String sAdresse1;
	@Column(name = "s_adresse_2")
	private String sAdresse2;
	@Column(name = "s_code_postal")
	private String sCodePostal;
	@Column(name = "s_pays")
	private String sPays;
	@Column(name = "s_cin")
	private String cin;
	@Column(name = "d_entre_le")
	private Date dEntreLe;
	@Column(name = "d_sortie_le")
	private Date dSortieLe;
	@Column(name = "s_motif")
	private String sMotif;
	@Column(name = "s_telephone")
	private String sTelephone;
	@Column(name = "s_banque_affectation")
	private String sbanqueaffectation;
	@Column(name = "f_salaire_brut")
	private Double fSalaireBrut;
	@Column(name = "s_matricule_cnss")
	private String sMatriculeCnss;
	@Column(name = "s_type_contrat")
	private String sTypeContrat;
	@Column(name = "s_no_contrat_anapec")
	private String sNoContratAnapec;
	@Column(name = "d_date_embauche")
	private Date dDateEmbauche;
	@Column(name = "s_matricule_paie")
	private String sMatruculePaie;
	@Column(name = "s_email")
	private String sEmail;
	@Column(name = "n_heure_allaitement")
	private boolean nHeureAllaitement;
	@Column(name = "d_allaitement_du")
	private Date dHeureAllaitementDu;
	@Column(name = "d_allaitement_au")
	private Date dHeureAllaitementAu;
	@Column(name = "d_date_debut_contrat")
	private Date dDateDebutContrat;
	@Column(name = "d_date_fin_contrat")
	private Date dDateFinContrat;
	@Column(name = "f_nb_jour_conge")
	private double fNbJourConge;
	@Column(name = "f_nb_rest_conge")
	private double fNbRestConge;
	@Column(name = "s_path_photo")
	private String sPathPhoto;
	@Column(name = "s_poste")
	private String sPoste;

	@Column(name = "s_code_superieur")
	private UUID superieur;
	
	@Column(name = "b_affectation")
	private Boolean bAffectation;

	@Column(name = "b_generation_ts")
	private boolean bGenerationTs;

	@Column(name = "s_type_generation_ts")
	private String sTypeGenerationTs;

	@Column(name = "b_projet_ts")
	private boolean bProjetTs;

	@Column(name = "s_type")
	private String type;

	@JsonIgnore
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private PKUser user;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "pays_id")
	private PKPays pkPays;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "nIdSemaineTravail")
	private PKSemaineTravail semaineTravail;

	@JsonIgnore
	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	@JoinColumn(name = "nIdService")
	private PKService service;

	@JsonIgnore
	@OneToMany(mappedBy = "personnel")
	private Collection<PKHistoriqueTransfaire> historiTransfaires;

	@JsonIgnore
	@OneToMany(mappedBy = "personnel")
	private Collection<PKTimesheet> timesheets;

	@JsonIgnore
	@OneToMany(mappedBy = "personnel")
	private List<PKDemande> demandes;
	

	public PKPersonnel(String cin, String nom, String prenom, String poste, boolean affectation) {
		this.cin = cin;
		this.nom = nom;
		this.prenom = prenom;
		this.sPoste = poste;
		this.bAffectation = affectation;
	}

	public PKPersonnel(PKService service,String cin, String nom, String prenom, String matrucule,String post) {
		this.service=service;
		this.cin = cin;
		this.nom = nom;
		this.prenom = prenom;
		this.sMatruculePaie = matrucule;
		this.sPoste=post;
	}
}
