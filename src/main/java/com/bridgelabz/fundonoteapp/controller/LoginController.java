package com.bridgelabz.fundonoteapp.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bridgelabz.fundonoteapp.model.Login;
import com.bridgelabz.fundonoteapp.model.UserDetails;
import com.bridgelabz.fundonoteapp.service.UserService;
import com.bridgelabz.fundonoteapp.util.JwtToken;
import com.bridgelabz.fundonoteapp.util.PasswordEncryption;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*") 
@RequestMapping("/")
public class LoginController {

	@Autowired
	private UserService userService;
	
	@PostMapping(value = "/login")
	public ResponseEntity<?> userLogin(@RequestBody Login login, HttpServletRequest request, HttpServletResponse response) {
		List<UserDetails> userList = userService.login(login);
		if (userList.size() != 0) {
			String token = JwtToken.jwtTokenGenerator(userList.get(0).getUserId());
			response.setHeader("token", token);
			response.addHeader("Access-Control-Allow-Headers","*");
			response.addHeader("Access-Control-Expose-Headers","*");
			
			return new ResponseEntity<>(
					/*"Welcome "+ userList.get(0).getUserName() + "   Jwt Token--->" + response.getHeader("token")*/HttpStatus.OK);
		} else
			return new ResponseEntity<String>("{Invalid Credentials}",HttpStatus.BAD_REQUEST);

	}

	// UPDATE
	@PutMapping(value = "/update")
	public ResponseEntity<String> userUpdate(HttpServletRequest request, @RequestBody UserDetails user) {
		String token = request.getHeader("token");
		System.out.println(token);
		userService.update(token, user);
			return new ResponseEntity<String>("Updated",HttpStatus.ACCEPTED);
		
	}

	// DELETE
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public ResponseEntity<String> userDelete(HttpServletRequest request) {
		String token = request.getHeader("token");
		System.out.println(token);
		userService.deleteUser(token);
		return new ResponseEntity<String>("Deleted",HttpStatus.OK);

	}
	@RequestMapping(value = "/forgot", method = RequestMethod.POST)
	public ResponseEntity<String> forgotPassword(@RequestBody UserDetails user, HttpServletRequest request,
			HttpServletResponse response) {
		Optional<UserDetails> list = userService.findByEmailId(user.getEmail());
		if (list.isPresent()) {
			return new ResponseEntity<String>("We didn't find an account for that e-mail address.",
					HttpStatus.NOT_FOUND);
		} else {
			UserDetails userdetails = list.get();
			String token = JwtToken.jwtTokenGenerator(userdetails.getUserId());
			response.setHeader("token", token);
			String subject = "Password Reset Request";
			String appUrl = "request.getScheme() " + "://" + request.getServerName() + "/reset?token=" + token;
			return new ResponseEntity<String>(userService.sendmail(subject, userdetails, appUrl), HttpStatus.ACCEPTED);
		}
	}

	@RequestMapping(value = "/reset", method = RequestMethod.PUT)
	public ResponseEntity<String> changePassword(HttpServletRequest request, @RequestBody String password) {
		String token = request.getHeader("token");

		int id = JwtToken.jwtTokenVerifier(token);
		if (id >= 0) {
			Optional<UserDetails> userList = userService.findById(id);
			userList.get().setPassword(PasswordEncryption.PasswordEncoder(password));
			userService.save(userList.get());
			return new ResponseEntity<String>("Changed", HttpStatus.RESET_CONTENT);
		} else
			return new ResponseEntity<String>("Not changed", HttpStatus.NOT_MODIFIED);

	}
}
