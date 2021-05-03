package spring.blog.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import spring.blog.models.ContactMessage;
import spring.blog.models.Post;
import spring.blog.repositories.ContactMessageRepository;
import spring.blog.services.PostService;

@Controller
public class HomeController {
	
	@Autowired
	private PostService postService;
	
	@Autowired
	private ContactMessageRepository contactMessageRepo;
	
	@RequestMapping( value= { "/", "/home" } )
	public String index(Model model){
		List<Post> latest5Posts = this.postService.findLatest5();
		model.addAttribute("latest5Posts", latest5Posts);
		List<Post> latest3Posts = latest5Posts.stream().limit(3).collect(Collectors.toList());
		
		model.addAttribute("latest3Posts", latest3Posts);
		
		return "new-index"; 
	}
	
	@RequestMapping( value= "about")
	public String renderingAboutView(Model model){
		return "about"; 
	}
	
	@RequestMapping( value= "contact",method = RequestMethod.GET)
	public String renderingContactView(Model model){
		return "contact"; 
	}
	
	
	@RequestMapping( value= "contact/post",method = RequestMethod.GET)
	@ResponseBody 
	public String submitContactForm(
			    @RequestParam String firstName,
			    @RequestParam String lastName,
			    @RequestParam(required = false) String phone,
			    @RequestParam String email,
			    @RequestParam String message,
			    @RequestParam(required = false) String countryISO
			    ){
		
		ContactMessage msg = new ContactMessage();
		msg.setFirstName(firstName);
		msg.setLastName(lastName);
		msg.setEmail(email);
		msg.setPhone(phone);
		msg.setMessage(message);
		msg.setCountryISO(countryISO);
		
		this.contactMessageRepo.save(msg);
	    HttpHeaders headers = new HttpHeaders();
	    headers.add("Content-Type", "application/json; charset=utf-8");
	    return "RESULT";
	}	
	
}
