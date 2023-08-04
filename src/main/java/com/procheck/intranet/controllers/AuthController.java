package com.procheck.intranet.controllers;

import java.util.Calendar;
import java.util.HashSet;

import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.procheck.intranet.models.PKCheck;
import com.procheck.intranet.models.PKPersonnel;
import com.procheck.intranet.models.PKRole;
import com.procheck.intranet.models.PKUser;
import com.procheck.intranet.outils.Outils;
import com.procheck.intranet.outils.PasswordValidator;
import com.procheck.intranet.payload.request.LoginRequest;
import com.procheck.intranet.payload.request.SignupRequest;
import com.procheck.intranet.payload.response.JwtResponse;
import com.procheck.intranet.payload.response.MessageResponse;
import com.procheck.intranet.repository.CheckRepository;
import com.procheck.intranet.repository.RoleRepository;
import com.procheck.intranet.repository.UserRepository;
import com.procheck.intranet.security.jwt.JwtUtils;
import com.procheck.intranet.security.services.IRoleService;
import com.procheck.intranet.security.services.IUserDetailsService;
import com.procheck.intranet.security.services.impl.UserDetailsImpl;
import com.procheck.intranet.services.IPersonnelService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	IUserDetailsService userService;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	IRoleService roleservice;
	
	@Autowired
	CheckRepository checkRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;
	
	@Autowired
	IPersonnelService personnelService;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticatUser(@Valid @RequestBody LoginRequest loginRequest) {

		log.info("AuthControler : signin ");

		if (userRepository.existsByUsername(loginRequest.getUsername())) {
			try {

				Authentication authentication = authenticationManager
						.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
								loginRequest.getPassword()));

				log.info("authentication", authentication.getPrincipal());
				SecurityContextHolder.getContext().setAuthentication(authentication);
				String jwt = jwtUtils.generateJwtToken(authentication);

				userService.UpdateDateConx(loginRequest.getUsername());

				UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
				log.info("AuthControler : signin : userDetails", userDetails);
				
				PKUser user=userService.findOne(userDetails.getId());
				
				PKPersonnel personne=personnelService.findPersonnelByUser(user);

				Set<PKRole> roles = userDetails.getRoles();

				if (!Outils.checkDExperation(userDetails.getdExpiration())) {

					userService.updateisEnbled(userDetails.getId());

					return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(),
							userDetails.getEmail(), userDetails.getdExpiration(), userDetails.getdCreation(),
							userDetails.getdLastCnx(), userDetails.isEnabled(), userDetails.isFirstCnx(), roles,
							new MessageResponse("Error: Account is Expired !"),personne));

				}
				if (userDetails.isFirstCnx()) {

					return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(),
							userDetails.getEmail(), userDetails.getdExpiration(), userDetails.getdCreation(),
							userDetails.getdLastCnx(), userDetails.isEnabled(), userDetails.isFirstCnx(), roles,
							new MessageResponse("Please change the password"),personne));

				}

				return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(),
						userDetails.getEmail(), userDetails.getdExpiration(), userDetails.getdCreation(),
						userDetails.getdLastCnx(), userDetails.isEnabled(), userDetails.isFirstCnx(), roles,
						new MessageResponse("Authentication is  successful"),personne));

			} catch (BadCredentialsException e) {

				PKUser user = userRepository.findByUsername(loginRequest.getUsername()).get();

				if (!Outils.checkFiveMinute(user.getDLastConx())) {

					user.setTentative(user.getTentative() + 1);
					userRepository.save(user);

					if (Outils.checkNTenative(user.getTentative())) {

						Calendar cal = user.getDLastConx();
						cal.add(Calendar.MINUTE, 5);
						user.setDLastConx(cal);
						userRepository.save(user);

						if (user.getTentative() >= 9) {
							userService.updateisEnbled(user.getId());
							return ResponseEntity.badRequest().body(new MessageResponse("Error: Account is locked !"));

						}

						return ResponseEntity.badRequest().body(new MessageResponse("Error: Wait 5 minute !"));
					}
				} else {

					return ResponseEntity.badRequest().body(new MessageResponse("Error: Wait 5 minute !"));
				}

				return ResponseEntity.badRequest().body(new MessageResponse("Error: Password incorrecte !"));
			}
		} else {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Login is not exist !"));
		}
	}

	@PostMapping("/signup")
	@PreAuthorize("hasRole('add_user')")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: login is already taken!"));
		}
		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
		}

		if (signUpRequest.getRole().isEmpty() || signUpRequest.getRole() == null) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Roles is null !"));
		}
		if (!PasswordValidator.isValid(signUpRequest.getPassword())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Password invalid !"));
		}

		// Create new user's account
		PKUser user = new PKUser(signUpRequest.getUsername(), signUpRequest.getEmail(),
				encoder.encode(signUpRequest.getPassword()));

		Set<String> strRoles = signUpRequest.getRole();
		Set<PKRole> roles = new HashSet<>();
//	    Set<PKPrivilege> privileges = new HashSet<PKPrivilege>();
		strRoles.forEach(role -> {

			log.info("user strRole  :", role.toString());

			PKRole roleD = roleRepository.findByName(role);

			if (roleRepository.existsByName(role)) {

				roles.add(roleD);
			}

		});

		if (roles.isEmpty() || roles == null) {

			return ResponseEntity.badRequest().body(new MessageResponse("Error: Roles is not found !"));

		}

		strRoles.forEach(role -> {

			log.info("user strRole  :", role.toString());

			PKRole roleD = roleRepository.findByName(role);

			if (roleRepository.existsByName(role)) {

				roles.add(roleD);
			}

		});

		user.setRoles(roles);
		userService.create(user);

		PKUser u = userRepository.findByEmail(user.getEmail());

		checkRepository.save(new PKCheck(u.getPassword(), u.getId()));

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}

}
