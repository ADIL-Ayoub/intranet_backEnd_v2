package com.procheck.intranet;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.procheck.intranet.models.PKCheck;
import com.procheck.intranet.models.PKDemande;
import com.procheck.intranet.models.PKDetailConge;
import com.procheck.intranet.models.PKPrivilege;
import com.procheck.intranet.models.PKRole;
import com.procheck.intranet.models.PKTypeConge;
import com.procheck.intranet.models.PKTypeDemande;
import com.procheck.intranet.models.PKUser;
import com.procheck.intranet.outils.Outils;
import com.procheck.intranet.payload.request.TimsheetByProjet;
//import com.procheck.intranet.outils.ImportFileCSV;
import com.procheck.intranet.repository.CheckRepository;
import com.procheck.intranet.repository.DemandeReporsitory;
import com.procheck.intranet.repository.PrivilegeRepository;
import com.procheck.intranet.repository.RoleRepository;
import com.procheck.intranet.repository.TypeCongeReporsitory;
import com.procheck.intranet.repository.TypeDemandeReporsitory;
import com.procheck.intranet.repository.UserRepository;
import com.procheck.intranet.security.services.IPrivilegeService;
import com.procheck.intranet.security.services.IRoleService;
import com.procheck.intranet.security.services.IUserDetailsService;
import com.procheck.intranet.services.IPersonnelService;
import com.procheck.intranet.services.ITypeCongeService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@EnableScheduling
public class IntranetApplication implements CommandLineRunner {

	@Autowired
	IPrivilegeService privilegeService;

	@Autowired
	PrivilegeRepository privilegeRepository;

	@Autowired
	IRoleService roleService;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	IUserDetailsService userservice;

	@Autowired
	TypeCongeReporsitory typeCongeReporsitory;

	@Autowired
	ITypeCongeService typeCongeService;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	CheckRepository checkReporsitory;

	@Autowired
	TypeDemandeReporsitory typeDemandeReporsitory;
	
	@Autowired
	DemandeReporsitory demandeReporsitory;

	public static void main(String[] args) {
		SpringApplication.run(IntranetApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		/*** Horaire **/
		PKPrivilege p001 = privilegeService.createPrivilegeIfNotFound("Horaire - ajouter Semaine", "create_semaine");
		PKPrivilege p002 = privilegeService.createPrivilegeIfNotFound("Horaire - find all semaine", "findAll_semaine");
		PKPrivilege p003 = privilegeService
				.createPrivilegeIfNotFound("Horaire - affectation heuraire list des employee", "hourly_param");
		PKPrivilege p004 = privilegeService.createPrivilegeIfNotFound("PAYS - ajouter pays", "create_country");
		PKPrivilege p005 = privilegeService.createPrivilegeIfNotFound("PAYS - ajouter jour ferier pour pays",
				"create_country_holliday");
		/*** USER **/
		PKPrivilege p1 = privilegeService.createPrivilegeIfNotFound("USER - afficher les utilisateurs",
				"findAll_users");
		PKPrivilege p2 = privilegeService.createPrivilegeIfNotFound("USER - modifier utilisateur", "update_user");
		PKPrivilege p3 = privilegeService.createPrivilegeIfNotFound("USER - ajouter utilisateur", "add_user");
		PKPrivilege p4 = privilegeService.createPrivilegeIfNotFound("USER - supprimer l'utilisateur", "delete_user");
		PKPrivilege p5 = privilegeService.createPrivilegeIfNotFound("USER - afficher utilisateur", "findOne_user");
		PKPrivilege p11 = privilegeService.createPrivilegeIfNotFound("USER - active ou desactive utilisateur",
				"enabled_user");
		PKPrivilege p35 = privilegeService.createPrivilegeIfNotFound("USER - change password", "change_password");
		/*** ROLE **/
		PKPrivilege p6 = privilegeService.createPrivilegeIfNotFound("ROLE - afficher les roles", "findAll_roles");
		PKPrivilege p7 = privilegeService.createPrivilegeIfNotFound("ROLE - modifier role", "update_role");
		PKPrivilege p8 = privilegeService.createPrivilegeIfNotFound("ROLE - ajouter role", "add_role");
		PKPrivilege p9 = privilegeService.createPrivilegeIfNotFound("ROLE - afficher role", "findOne_role");
		/*** PRIVILEGE **/
		PKPrivilege p12 = privilegeService.createPrivilegeIfNotFound("PRIVILEGE - afficher les privileges",
				"findAll_privileges");
		PKPrivilege p13 = privilegeService.createPrivilegeIfNotFound("PRIVILEGE - afficher privilege",
				"findOne_privilege");
		PKPrivilege p10 = privilegeService.createPrivilegeIfNotFound("PRIVILEGE - affectation privileges",
				"assign_role_privilage");
		PKPrivilege p32 = privilegeService.createPrivilegeIfNotFound("PRIVILEGE - update privileges",
				"update_privilege");
		/*** DEPARTEMENT **/
		PKPrivilege p14 = privilegeService.createPrivilegeIfNotFound(
				"DEPARTEMENT - remplissage automatique TS by departement", "update_gts_departement");
		PKPrivilege p15 = privilegeService.createPrivilegeIfNotFound("DEPARTEMENT - afficher les departements",
				"find_departements");
		PKPrivilege p22 = privilegeService.createPrivilegeIfNotFound("DEPARTEMENT - afficher departement",
				"findOne_departement");
		/*** CLIENT **/
		PKPrivilege p16 = privilegeService.createPrivilegeIfNotFound("CLIENT - remplissage automatique TS by client",
				"update_gts_client");
		PKPrivilege p17 = privilegeService.createPrivilegeIfNotFound("CLIENT - afficher les clients", "find_clients");
		PKPrivilege p23 = privilegeService.createPrivilegeIfNotFound("CLIENT - afficher client", "findOne_client");
		PKPrivilege p46 = privilegeService.createPrivilegeIfNotFound("CLIENT - afficher les clients par departement",
				"clients_by_departement");
		/*** SERVICE **/
		PKPrivilege p18 = privilegeService.createPrivilegeIfNotFound("SERVICE - remplissage automatique TS by service",
				"update_gts_service");
		PKPrivilege p19 = privilegeService.createPrivilegeIfNotFound("SERVICE - afficher les services",
				"find_services");
		PKPrivilege p24 = privilegeService.createPrivilegeIfNotFound("SERVICE - afficher service", "findOne_service");
		PKPrivilege p26 = privilegeService.createPrivilegeIfNotFound("SERVICE - affectation responsable",
				"assign_responsable_services");
		PKPrivilege p27 = privilegeService.createPrivilegeIfNotFound("SERVICE - affectation superviseur",
				"assign_superviseur_services");
		PKPrivilege p41 = privilegeService.createPrivilegeIfNotFound("SERVICE - supprimer responsable dans service",
				"delete_responsable_service");
		PKPrivilege p42 = privilegeService.createPrivilegeIfNotFound("SERVICE - supprimer superviseur dans service",
				"delete_superviseur_service");
		PKPrivilege p43 = privilegeService.createPrivilegeIfNotFound(
				"SERVICE - afficher les services par responsable ou superviseur",
				"find_services_responsable_superviseur");
//		PKPrivilege p44 = privilegeService.createPrivilegeIfNotFound("afficher services par superviseur",
//				"find_services_superviseur");
		PKPrivilege p47 = privilegeService.createPrivilegeIfNotFound("SERVICE - afficher les services par client",
				"services_by_client");
		PKPrivilege p49 = privilegeService.createPrivilegeIfNotFound("SERVICE - afficher les services par client list",
				"services_by_idClient");
		PKPrivilege p58 = privilegeService.createPrivilegeIfNotFound("SERVICE - activation responsable en service",
				"active_resp_service");

		/*** PERSONNEL **/
		PKPrivilege p20 = privilegeService.createPrivilegeIfNotFound(
				"PERSONNEL - afficher les personnels by Filter et non affecter", "find_personnels_cin_nom_prenom");
		PKPrivilege p59 = privilegeService.createPrivilegeIfNotFound(
				"PERSONNEL - afficher les personnels by Filter All", "find_personnels_by_filter");
		PKPrivilege p21 = privilegeService.createPrivilegeIfNotFound("PERSONNEL - afficher les personnels",
				"find_personnels");
		PKPrivilege p25 = privilegeService.createPrivilegeIfNotFound("PERSONNEL - afficher personnel",
				"findOne_personnel");
		PKPrivilege p28 = privilegeService.createPrivilegeIfNotFound(
				"PERSONNEL - afficher les personnels by departement", "find_personnel_by_departement");
		PKPrivilege p29 = privilegeService.createPrivilegeIfNotFound("PERSONNEL - afficher les personnels by client",
				"find_personnel_by_client");
		PKPrivilege p30 = privilegeService.createPrivilegeIfNotFound(
				"PERSONNEL - afficher les personnels les by service", "find_personnel_by_service");
		PKPrivilege p31 = privilegeService.createPrivilegeIfNotFound(
				"PERSONNEL - afficher les personnels les by projet", "find_personnel_by_projet");
		PKPrivilege p40 = privilegeService.createPrivilegeIfNotFound("PERSONNEL - affectation user employee",
				"assign_user_personnel");
		PKPrivilege p45 = privilegeService.createPrivilegeIfNotFound("PERSONNEL - supprimer user employee",
				"delete_user_personnel");
		PKPrivilege p48 = privilegeService.createPrivilegeIfNotFound(
				"PERSONNEL - afficher les personnels par Services et Filter", "find_personnels_by_filter_and_services");
		PKPrivilege p55 = privilegeService
				.createPrivilegeIfNotFound("PERSONNEL - remplissage automatique TS by employee", "update_gts_employee");
		PKPrivilege p76 = privilegeService
				.createPrivilegeIfNotFound("PERSONNEL - affectation superieur hiérarchique", "affectation_superieur");
		PKPrivilege p93 = privilegeService
				.createPrivilegeIfNotFound("PERSONNEL - affectation super employee ", "affectation_super_emp");

		
		/*** TYPE CONGE **/
		PKPrivilege p33 = privilegeService.createPrivilegeIfNotFound("TYPE CONGE - afficher les types de conges",
				"find_typeConges");
		PKPrivilege p34 = privilegeService.createPrivilegeIfNotFound("TYPE CONGE - ajouter type conge",
				"add_typeConges");
		PKPrivilege p36 = privilegeService.createPrivilegeIfNotFound("TYPE CONGE - afficher type conge",
				"findOne_typeConge");
		PKPrivilege p37 = privilegeService.createPrivilegeIfNotFound("TYPE CONGE - modifier type conge",
				"update_typeConge");
		PKPrivilege p38 = privilegeService.createPrivilegeIfNotFound("TYPE CONGE - delete type conge",
				"delete_typeConge");
		/*** IMPORT CSV **/
		PKPrivilege p39 = privilegeService.createPrivilegeIfNotFound("IMPORT - import file csv sage", "importcsv_sage");
		/*** SEMAINE **/
		PKPrivilege p50 = privilegeService.createPrivilegeIfNotFound("SEMAINE - afficher semaine par employee",
				"find_semaine_jour");
		PKPrivilege p60 = privilegeService.createPrivilegeIfNotFound("SEMAINE - affectation semaine des emplyees",
				"semaine_employees");
		/*** PAYS **/
		PKPrivilege p61 = privilegeService.createPrivilegeIfNotFound("PAYS - affectation pays des emplyees",
				"pays_employees");
		PKPrivilege p62 = privilegeService.createPrivilegeIfNotFound("PAYS - afficher des pays", "find_all_pays");

		/*** TIMESHEET **/
		PKPrivilege p53 = privilegeService.createPrivilegeIfNotFound("TIMESHEET - generation timesheet",
				"generation_ts");
		PKPrivilege p54 = privilegeService.createPrivilegeIfNotFound("TIMESHEET - validation timesheets",
				"validation_ts");
		PKPrivilege p56 = privilegeService.createPrivilegeIfNotFound("TIMESHEET - enregistrement timesheets",
				"enregistrement_ts");
		PKPrivilege p51 = privilegeService.createPrivilegeIfNotFound("TIMESHEET - generation timesheet des employees",
				"add_timesheet_journalier");
		PKPrivilege p52 = privilegeService.createPrivilegeIfNotFound("TIMESHEET - modifier timesheet by id",
				"edit_timesheet_journalier");
		PKPrivilege p57 = privilegeService.createPrivilegeIfNotFound("TIMESHEET - generation timesheet by personne",
				"find_timesheet_personnel");
		PKPrivilege p70 = privilegeService.createPrivilegeIfNotFound("TIMESHEET - demande modification TS",
				"add_demande_ts");
		PKPrivilege p71 = privilegeService.createPrivilegeIfNotFound("TIMESHEET - find les modification TS par idUser",
				"find_demande_ts");
		PKPrivilege p72 = privilegeService.createPrivilegeIfNotFound(
				"TIMESHEET - confermation les modification TS par idUser", "update_demande_ts");
		
		/*** TIMESHEET EMP **/
		PKPrivilege p90 = privilegeService.createPrivilegeIfNotFound(
				"TIMESHEET - generation TS par utilisateur", "user_generation_ts");
		PKPrivilege p91 = privilegeService.createPrivilegeIfNotFound(
				"TIMESHEET - confermation TS par utilisateur", "confermation_ts_emp");
		PKPrivilege p92 = privilegeService.createPrivilegeIfNotFound(
				"TIMESHEET - enregistrement TS par utilisateur", "enregistrement_ts_emp");

		
		/*** PROJET **/
		PKPrivilege p63 = privilegeService.createPrivilegeIfNotFound("PROJET - creation un projet", "add_projet");
		PKPrivilege p64 = privilegeService.createPrivilegeIfNotFound("PROJET - affectation des projets to service",
				"assign_service_projets");
		PKPrivilege p65 = privilegeService.createPrivilegeIfNotFound("PROJET - find projets by service and code",
				"find_by_code_service");
		PKPrivilege p66 = privilegeService.createPrivilegeIfNotFound("PROJET - find projets by service",
				"find_projets_service");

		/*** TIMESHEET PROJET **/
		PKPrivilege p67 = privilegeService.createPrivilegeIfNotFound("TIMESHEET PROJET - ajouter timesheet projet",
				"add_ts_projet");
		PKPrivilege p68 = privilegeService.createPrivilegeIfNotFound("TIMESHEET PROJET - modifier timesheet projet",
				"update_ts_projet");
		PKPrivilege p69 = privilegeService.createPrivilegeIfNotFound("TIMESHEET PROJET - suprimer timesheet projet",
				"delete_ts_projet");

		/*** DEMANDE **/
		PKPrivilege p73 = privilegeService.createPrivilegeIfNotFound("DEMANDE - demande conge", "add_demande_conge");
		PKPrivilege p74 = privilegeService.createPrivilegeIfNotFound("DEMANDE - demande conge par user",
				"create_demande_conge_user");
		PKPrivilege p75 = privilegeService.createPrivilegeIfNotFound("DEMANDE - afficher mes demande", "find_mes_demandes");
		PKPrivilege p77 = privilegeService.createPrivilegeIfNotFound("DEMANDE - afficher les demandes affecté", "find_demandes_affecte");
		PKPrivilege p78 = privilegeService.createPrivilegeIfNotFound("DEMANDE - afficher mes demandes enregistré", "mes_demandes_enregistre");
		PKPrivilege p79 = privilegeService.createPrivilegeIfNotFound("DEMANDE - envoye  demande conge ", "envoyer_demande_conge");
		PKPrivilege p80 = privilegeService.createPrivilegeIfNotFound("DEMANDE - solde conge INTRANET ", "solde_conge");
		PKPrivilege p81 = privilegeService.createPrivilegeIfNotFound("DEMANDE - solde conge SAGE ", "solde_conge_sage");
		PKPrivilege p82 = privilegeService.createPrivilegeIfNotFound("DEMANDE - annule demande conge enregistre", "annule_demande_conge");
		PKPrivilege p83 = privilegeService.createPrivilegeIfNotFound("DEMANDE - modifiere demande conge enregistre", "modifier_demande_conge");
		PKPrivilege p85 = privilegeService.createPrivilegeIfNotFound("DEMANDE - demande conge and envoye", "cree_and_envoyer_conge");
		PKPrivilege p86 = privilegeService.createPrivilegeIfNotFound("DEMANDE - demande conge and valide", "cree_and_valider_conge");
		PKPrivilege p87 = privilegeService.createPrivilegeIfNotFound("DEMANDE - decision superieur hiérarchique ", "decision_conge");
		PKPrivilege p88 = privilegeService.createPrivilegeIfNotFound("DEMANDE - demandes superieur hiérarchique by filter ", "find_demand_by_filter");
		PKPrivilege p89 = privilegeService.createPrivilegeIfNotFound("DEMANDE - demande modification conge valider ", "demande_modifier_conge");
		
		/*** CONGE **/
		
		PKPrivilege p84 = privilegeService.createPrivilegeIfNotFound("DEMANDE - affecher infos beneficiare conge", "find_infos_employee");
		
		
		
		
		
		
		// PKPrivilege p45 = privilegeService.createPrivilegeIfNotFound("afficher
		// personnels by projet", "find_personnel_by_projet");

		Set<PKPrivilege> listP = new HashSet<PKPrivilege>();
		Set<PKPrivilege> listPE = new HashSet<PKPrivilege>();

		PKRole roleUser = new PKRole("USER", listP);

		if (!roleRepository.existsByName(roleUser.getName())) {
			roleService.save(roleUser);
		}

		listP.add(p1);
		listP.add(p2);
		listP.add(p3);
		listP.add(p4);
		listP.add(p5);
		listP.add(p6);
		listP.add(p7);
		listP.add(p8);
		listP.add(p9);
		listP.add(p10);
		listP.add(p11);
		listP.add(p12);
		listP.add(p13);
		listP.add(p14);
		listP.add(p15);
		listP.add(p16);
		listP.add(p17);
		listP.add(p18);
		listP.add(p19);
		listP.add(p20);
		listP.add(p21);
		listP.add(p22);
		listP.add(p23);
		listP.add(p24);
		listP.add(p25);
		listP.add(p26);
		listP.add(p27);
		listP.add(p28);
		listP.add(p29);
		listP.add(p30);
		listP.add(p31);
		listP.add(p32);
		listP.add(p33);
		listP.add(p34);
		listP.add(p35);
		listP.add(p36);
		listP.add(p37);
		listP.add(p38);
		listP.add(p39);
		listP.add(p40);
		listP.add(p41);
		listP.add(p42);
		listP.add(p43);
//		listP.add(p44);
		listP.add(p45);
		listP.add(p46);
		listP.add(p47);
		listP.add(p48);
		listP.add(p49);
		listP.add(p50);
		listP.add(p51);
		listP.add(p52);
		listP.add(p53);
		listP.add(p54);
		listP.add(p55);
		listP.add(p56);
		listP.add(p57);
		listP.add(p58);
		listP.add(p59);
		listP.add(p60);
		listP.add(p61);
		listP.add(p62);
		listP.add(p63);
		listP.add(p64);
		listP.add(p65);
		listP.add(p66);
		listP.add(p67);
		listP.add(p68);
		listP.add(p69);
		listP.add(p70);
		listP.add(p71);
		listP.add(p72);
		listP.add(p73);
		listP.add(p74);
		listP.add(p75);
		listP.add(p76);
		listP.add(p77);
		listP.add(p78);
		listP.add(p79);
		listP.add(p80);
		listP.add(p81);
		listP.add(p82);
		listP.add(p83);
		listP.add(p84);
		listP.add(p85);
		listP.add(p86);
		listP.add(p87);
		listP.add(p88);
		listP.add(p89);
		listP.add(p90);
		listP.add(p91);
		listP.add(p92);
		listP.add(p93);
		
		listP.add(p001);
		listP.add(p002);
		listP.add(p003);
		listP.add(p004);
		listP.add(p005);
		
		/**  GTS Employee**/
		
		listPE.add(p90);
		listPE.add(p91);
		listPE.add(p92);
		listPE.add(p67);
		listPE.add(p68);
		listPE.add(p69);
		listPE.add(p70);
		

		PKRole roleAdmin = new PKRole("ADMIN", listP);
		PKRole roleSuper = new PKRole("SUPERVISEUR");
		PKRole roleResp = new PKRole("RESPONSABLE");
		PKRole roleEmp = new PKRole("EMP_GTS"); 

		if (!roleRepository.existsByName(roleAdmin.getName())) {
			roleService.save(roleAdmin);
		} else {
			PKRole role = roleService.findRoleByName("ADMIN");
			role.getPrivileges().addAll(listP);
			roleService.save(role);
		}
		if (!roleRepository.existsByName(roleResp.getName())) {
			roleService.save(roleResp);
		} else {
			PKRole role = roleService.findRoleByName("RESPONSABLE");
			// role.getPrivileges().addAll(listP);
			roleService.save(role);
		}
		if (!roleRepository.existsByName(roleSuper.getName())) {
			roleService.save(roleSuper);
		} else {
			PKRole role = roleService.findRoleByName("SUPERVISEUR");
//			role.getPrivileges().addAll(listP);
			roleService.save(role);
		}
		if (!roleRepository.existsByName(roleEmp.getName())) {
			roleService.save(roleEmp);
		} else {
			PKRole role = roleService.findRoleByName("EMP_GTS");
			role.getPrivileges().addAll(listPE);
			roleService.save(role);
		}

		PKUser userAdmin = new PKUser("admin", "admin@admin.ma", encoder.encode("12345678"));

		Set<PKRole> roles = new HashSet<PKRole>();

		roles.add(roleAdmin);

		userAdmin.setRoles(roles);

		if (!userRepository.existsByUsername(userAdmin.getUsername())) {

			userservice.create(userAdmin);
		}

		if (!userRepository.existsByEmail(userAdmin.getEmail())) {

			PKUser user = userservice.findByEmail(userAdmin.getEmail());

			user.setFirstConx(false);

			userRepository.save(user);
		}
		PKTypeDemande typeDemandeDC = new PKTypeDemande();
		typeDemandeDC.setNameTypeDemande("DEMANDE CONGE");
		typeDemandeDC.setCodeTypeDemande("DC");

		PKTypeDemande typeDemandeDDA = new PKTypeDemande();
		typeDemandeDDA.setNameTypeDemande("DEMANDE DOCUMENT ADMINISTRATIF");
		typeDemandeDDA.setCodeTypeDemande("DDA");

		if (!typeDemandeReporsitory.existsByCodeTypeDemande("DC")) {
			typeDemandeReporsitory.save(typeDemandeDC);
		}
		if (!typeDemandeReporsitory.existsByCodeTypeDemande("DDA")) {
			typeDemandeReporsitory.save(typeDemandeDDA);
		}

		PKTypeConge type1 = new PKTypeConge("Congé payé");
		PKTypeConge type2 = new PKTypeConge("Congé sans solde");
		PKTypeConge type3 = new PKTypeConge("Congé maladie");
		PKTypeConge type4 = new PKTypeConge("Congé maternité");
		PKTypeConge type5 = new PKTypeConge("Congé paternité");
		PKTypeConge type6 = new PKTypeConge("Congé décès");
		PKTypeConge type7 = new PKTypeConge("Allaitement");
		PKTypeConge type8 = new PKTypeConge("Démission");
		PKDetailConge detailConge1 = new PKDetailConge("décès d’un enfant, d’un petits-enfants ou d’un parent");
		PKDetailConge detailConge2 = new PKDetailConge("décès concerne un frère ou un ascendant du conjoint");
		List<PKDetailConge> dts = new ArrayList<PKDetailConge>();
		dts.add(detailConge1);
		dts.add(detailConge2);
		type6.setDetaileConges(dts);
		if (!typeCongeReporsitory.existsByTypeConge(type1.getTypeConge())) {
			typeCongeReporsitory.save(type1);
		}
		if (!typeCongeReporsitory.existsByTypeConge(type2.getTypeConge())) {
			typeCongeReporsitory.save(type2);
		}
		if (!typeCongeReporsitory.existsByTypeConge(type3.getTypeConge())) {
			typeCongeReporsitory.save(type3);
		}
		if (!typeCongeReporsitory.existsByTypeConge(type4.getTypeConge())) {
			typeCongeReporsitory.save(type4);
		}
		if (!typeCongeReporsitory.existsByTypeConge(type5.getTypeConge())) {
			typeCongeReporsitory.save(type5);
		}
		if (!typeCongeReporsitory.existsByTypeConge(type6.getTypeConge())) {
			typeCongeService.save(type6);
		}
		if (!typeCongeReporsitory.existsByTypeConge(type7.getTypeConge())) {
			typeCongeReporsitory.save(type7);
		}
		if (!typeCongeReporsitory.existsByTypeConge(type8.getTypeConge())) {
			typeCongeReporsitory.save(type8);
		}

		System.err.println("******* TEST **********");

//		List<TimsheetByProjet> listTSP = new ArrayList<TimsheetByProjet>();
//		TimsheetByProjet tsp1 = new TimsheetByProjet();
//		tsp1.setTime("01:30");
//		TimsheetByProjet tsp2 = new TimsheetByProjet();
//		tsp2.setTime("02:30");
//		TimsheetByProjet tsp3 = new TimsheetByProjet();
//		tsp3.setTime("03:30");
//		listTSP.add(tsp1);
//		listTSP.add(tsp2);
//		listTSP.add(tsp3);
//
//		System.out.println("totale heur :" + Outils.TotaleHeur(listTSP));
//		
//		Page<PKDemande> demandes=demandeReporsitory.findAll(PageRequest.of(0, 10));
//		
//		System.out.println(demandes.toString());
//		
//		for (PKDemande pkDemande : demandes.getContent()) {
//			
//			System.out.println("demande :"+pkDemande.getId());
//		}

//		List<String> l1=new ArrayList<String>();
//		List<String> l2=new ArrayList<String>();
//		l1.add("MARWANE");
//		l1.add("HAMZA");
//		l2.add("HAMZA");
//		
//		l1.addAll(l2);
//		
//		Set<String> s=new HashSet<String>(l1);
//		for (String string : s) {
//			System.out.println(string);
//		}
//		
//		System.out.println("heur :"+Outils.FormatageNbHeurs(Outils.getNbHeureDemi("00:00", "00:00", "00:00", "00:00")));
//		System.out.printf("%02d:%02d \n",Outils.getNbHeureJour("08:00", "16:30")/60,Outils.getNbHeureJour("08:30", "15:30")%60);
//		System.out.printf("%02d:%02d \n",Outils.getNbHeureDemi("09:30", "12:50", "15:40", "19:30")/60,Outils.getNbHeureDemi("09:30", "12:50", "15:40", "19:30")%60);
	}

}
