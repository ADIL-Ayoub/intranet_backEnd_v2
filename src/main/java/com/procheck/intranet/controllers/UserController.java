package com.procheck.intranet.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.procheck.intranet.models.PKCheck;
import com.procheck.intranet.models.PKPersonnel;
import com.procheck.intranet.models.PKRole;
import com.procheck.intranet.models.PKUser;
import com.procheck.intranet.outils.Outils;
import com.procheck.intranet.outils.PasswordValidator;
import com.procheck.intranet.payload.request.ChangePassword;
import com.procheck.intranet.payload.request.SignupRequest;
import com.procheck.intranet.payload.request.UserEmailRoles;
import com.procheck.intranet.payload.response.MessageResponse;
import com.procheck.intranet.repository.CheckRepository;
import com.procheck.intranet.repository.PersonnelReporsitory;
import com.procheck.intranet.repository.RoleRepository;
import com.procheck.intranet.repository.ServiceReporsitory;
import com.procheck.intranet.repository.UserRepository;
import com.procheck.intranet.security.services.IRoleService;
import com.procheck.intranet.security.services.IUserDetailsService;
import com.procheck.intranet.services.IPersonnelService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/intranet/user")
public class UserController {

	@Autowired
	IUserDetailsService userService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository rolerepository;

	@Autowired
	IRoleService roleservice;

	@Autowired
	CheckRepository checkRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	IPersonnelService personnelService;

	@Autowired
	PersonnelReporsitory personnelReporsitory;
	
	@Autowired
	ServiceReporsitory serviceReporsotory;

	@GetMapping("/all")
	public String allAccess() {

		return "Public Content.";
	}

	@GetMapping("/users")
	@PreAuthorize("hasRole('findAll_users')")
	public ResponseEntity<?> all(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		log.info("[ USER CONTROLLER ] ~ [ GET ALL ]");
		try {

			Page<PKUser> users = userService.findAll(PageRequest.of(page, size));

			return new ResponseEntity<>(users, HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('findOne_user')")
	public ResponseEntity<?> getOne(@PathVariable("id") UUID id) {

		log.info("[ USER CONTROLLER ] ~ [ GET USER BY ID ]");
		try {
			if (!userRepository.existsById(id)) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found user with id  !"));
			}
			PKUser user = userService.findOne(id);

			return new ResponseEntity<>(user, HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.NOT_FOUND);

		}
	}

//	@PutMapping("/modifier/{id}")
//	@PreAuthorize("hasRole('update_user_id')")
	public ResponseEntity<?> modifier(@PathVariable("id") UUID id, @RequestBody SignupRequest user) {
		log.info("[ USER CONTROLLER ] ~ [ UPDATE USER BY ID ]");

		if (!userRepository.existsById(id)) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found user with id !"));
		}

		if (user.getRole().isEmpty() || user.getRole() == null) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found Roles with user  !"));
		}
		if (!PasswordValidator.isValid(user.getPassword())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Password invalid !"));
		}

		List<PKCheck> checks = checkRepository.findTop3ByCodeOrderByVersionDesc(id);

		if (!Outils.checkPassword(checks, user.getPassword())) {

			return ResponseEntity.badRequest().body(new MessageResponse("Error: Password is already in use!"));
		}
		try {

			Set<String> strRoles = user.getRole();
			Set<PKRole> roles = new HashSet<PKRole>();
			strRoles.forEach(role -> {

				PKRole roleD = rolerepository.findByName(role);

				if (rolerepository.existsByName(role)) {

					roles.add(roleD);
				}
			});

			PKUser userp = new PKUser(user.getUsername(), user.getEmail(), user.getPassword(), roles);

			userp = userService.updateById(id, userp);

			checkRepository.save(new PKCheck(userp.getPassword(), id));

			return new ResponseEntity<>(new MessageResponse("User registered successfully!"), HttpStatus.OK);

		} catch (Exception ex) {
			log.error(ex.getMessage());
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PutMapping("/mod/{id}")
	@PreAuthorize("hasRole('update_user')")
	public ResponseEntity<?> modifierEmailRoles(@PathVariable("id") UUID id, @Valid @RequestBody UserEmailRoles user) {
		log.info("[ USER CONTROLLER ] ~ [ UPDATE EMAIL ROLES USER BY ID ]");

		if (!userRepository.existsById(id)) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found user with id !"));
		}

		if (user.getRoles().isEmpty() || user.getRoles() == null) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found Roles with user  !"));
		}
		try {

			Set<String> strRoles = user.getRoles();
			Set<PKRole> roles = new HashSet<PKRole>();
			strRoles.forEach(role -> {

				PKRole roleD = rolerepository.findByName(role);

				if (rolerepository.existsByName(roleD.getName())) {

					roles.add(roleD);
				}
			});
			PKUser u = new PKUser(user.getUsername(), user.getEmail(), roles);

			userService.updateEmailRoles(id, u);

			return new ResponseEntity<>(new MessageResponse("User registered successfully!"), HttpStatus.OK);

		} catch (Exception ex) {
			log.error(ex.getMessage());
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PutMapping("/modPassword/{id}")
	@PreAuthorize("hasRole('change_password')")
	public ResponseEntity<?> modifierPassword(@PathVariable("id") UUID id, @RequestBody ChangePassword user) {
		log.info("[ USER CONTROLLER ] ~ [ UPDATE PASSWORD BY ID ]");

		if (!userRepository.existsById(id)) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found user with id !"));
		}
		if (!user.getPassword().equals(user.getPasswordConfirme())) {

			return ResponseEntity.badRequest().body(new MessageResponse("Error: Password non confirmer  !"));
		}
		PKUser u = userService.findOne(id);

		if (!Outils.checkOldPassword(user.getPasswordOld(), u.getPassword())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Password old incorrect  !"));

		}
		if (!PasswordValidator.isValid(user.getPassword())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Password invalid !"));
		}

		List<PKCheck> checks = checkRepository.findTop3ByCodeOrderByVersionDesc(id);

		if (!Outils.checkPassword(checks, user.getPassword())) {

			return ResponseEntity.badRequest().body(new MessageResponse("Error: Password is already in use!"));
		}
		try {

			userService.changePassword(id, user);

			checkRepository.save(new PKCheck(user.getPassword(), id));

			return new ResponseEntity<>(new MessageResponse("User registered successfully!"), HttpStatus.OK);

		} catch (Exception ex) {
			log.error(ex.getMessage());
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PostMapping("/add")
	@PreAuthorize("hasRole('add_user')")
	public ResponseEntity<?> add(@Valid @RequestBody SignupRequest signUpRequest) {
		log.info("[ USER CONTROLLER ] ~ [ CREATE USER]");
		
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
		}
		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
		}
		if (signUpRequest.getRole().isEmpty() || signUpRequest.getRole() == null) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found Roles with user  !"));
		}
		if (!PasswordValidator.isValid(signUpRequest.getPassword())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Password invalid !"));
		}

		PKUser user = new PKUser(signUpRequest.getUsername(), signUpRequest.getEmail(),
				encoder.encode(signUpRequest.getPassword()));
		

		Set<String> strRoles = signUpRequest.getRole();
		Set<PKRole> roles = new HashSet<>();

		strRoles.forEach(role -> {

			PKRole roleD = rolerepository.findByName(role);

			if (rolerepository.existsByName(role)) {

				roles.add(roleD);
			}

		});

		if (roles.isEmpty() || roles == null) {

			return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found Roles with user !"));

		}

		strRoles.forEach(role -> {

			PKRole roleD = rolerepository.findByName(role);

			if (rolerepository.existsByName(role)) {

				roles.add(roleD);
			}

		});
		user.setAffecte(false);
		user.setRoles(roles);
		userService.create(user);

		PKUser u = userRepository.findByEmail(user.getEmail());
		
		
		if(signUpRequest.getIdPersonne() != null && !signUpRequest.getIdPersonne().isEmpty()) {
		UUID uid=UUID.fromString(signUpRequest.getIdPersonne());
		if (!personnelReporsitory.existsById(uid)) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Personne invalid !"));
		}
		PKPersonnel personnel=personnelService.findPersonnelById(uid);
		if(personnel.getBAffectation() == true) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Personne déja affecté !"));
		}else {
			u.setAffecte(true);
			userRepository.save(u);
			personnelService.assignUser(uid, u);
		}
		
		}else {
			
			log.info("uuid is null :"+signUpRequest.getIdPersonne());
		}
		checkRepository.save(new PKCheck(u.getPassword(), u.getId()));

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));

	}

	@PutMapping("/enabled/{id}")
	@PreAuthorize("hasRole('enabled_user')")
	public ResponseEntity<?> updateisEnabled(@PathVariable("id") UUID id) {
		log.info("[ USER CONTROLLER ] ~ [ ENABLED USER BY ID ]");

		if (!userRepository.existsById(id)) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found user with id !"));
		}

		try {

			userService.updateisEnbled(id);

			return new ResponseEntity<>(new MessageResponse("User is enabled successfully!"), HttpStatus.OK);

		} catch (Exception ex) {
			log.error(ex.getMessage());
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@DeleteMapping("/del/{id}")
	@PreAuthorize("hasRole('delete_user')")
	public ResponseEntity<?> delete(@PathVariable("id") UUID id) {
		log.info("[ USER CONTROLLER ] ~ [ DELETE USER ]");
		if (!userRepository.existsById(id)) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found user with id !"));
		}
		try {
			userService.delete(id);
			return ResponseEntity.ok(new MessageResponse("User delete successfully!"));
		} catch (Exception ex) {
			log.error(ex.getMessage());
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
