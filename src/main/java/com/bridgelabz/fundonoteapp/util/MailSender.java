//package com.bridgelabz.fundonoteapp.util;
//
//import java.util.Optional;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//
//import com.bridgelabz.fundonoteapp.model.UserDetails;
//
//public class MailSender {
//	@RequestMapping(value = "/mail", method = RequestMethod.POST)
//	public ResponseEntity<String> mailForActivation(HttpServletRequest request) {
//		String token = request.getHeader("token");
//		int userId = JwtToken.jwtTokenVerifier(token);
//		Optional<UserDetails> list = userService.findById(userId);
//		if (list == null) {
//			return new ResponseEntity<String>("We didn't find an account for that e-mail address.",
//					HttpStatus.NOT_FOUND);
//		} else {
//			UserDetails userdetails = list.get();
//
//			String appUrl = "http://localhost:8080" + "/active/token=" + token;
//			String subject = "To active your status";
//			return new ResponseEntity<String>(userService.sendmail(subject, userdetails, appUrl), HttpStatus.ACCEPTED);
//		}
//
//	}
//
//	@RequestMapping(value = "/active", method = RequestMethod.PUT)
//	public ResponseEntity<String> activeStatus(HttpServletRequest request) {
//		String token = request.getHeader("token");
//		int id = JwtToken.jwtTokenVerifier(token);
//		if (id >= 0) {
//			Optional<UserDetails> userList = userService.findById(id);
//			userList.get().setActiveStatus(1);
//			userService.save(userList.get());
//			return new ResponseEntity<String>("Changed", HttpStatus.OK);
//		} else
//			return new ResponseEntity<String>("Not changed", HttpStatus.NOT_MODIFIED);
//	}
//
//}