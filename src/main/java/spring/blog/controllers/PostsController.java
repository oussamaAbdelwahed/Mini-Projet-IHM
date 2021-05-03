package spring.blog.controllers;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import spring.blog.models.Post;
import spring.blog.models.User;
import spring.blog.services.NotificationService;
import spring.blog.services.PostService;
import spring.blog.services.UserService;


@Controller
public class PostsController {

	@Autowired
	private PostService postService;
	@Autowired
	private NotificationService notifyService;
	@Autowired
	private UserService userService;
	
	
	@RequestMapping(value = "/posts/find", method = RequestMethod.POST,consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String findPost(Model model,@RequestBody MultiValueMap<String,String> formData, BindingResult bindingResult){
		String searchQuery = formData.getFirst("search");
		List<Post> result = this.postService.searchForPosts(searchQuery);
		if(result.size()==0 ){
			model.addAttribute("errorMessage","aucun poste ne correspond a votre recherche '"+searchQuery+"'");
		   //notifyService.addErrorMessage();
		}
		model.addAttribute("posts", result);
		return "posts/show-search-posts";		
	}
	

	@RequestMapping("/posts/view/{id}")
	public String view(@PathVariable("id") Long id, Model model){
		Post post = this.postService.findById(id);
		if( post == null ){
			notifyService.addErrorMessage("Le post n'est pas trouvable avec l' id #" + id);
			return "redirect:/";
		}		
		model.addAttribute("p", post);
		// To have something like src/main/resources/templates/<CONTROLLER-NAME>/<Mapping-Name-view>
		return "posts/view";		
	}
	
	@RequestMapping("/posts/create")
	public ModelAndView create(){
		ModelAndView modelAndView = new ModelAndView();
		Post post = new Post();
		modelAndView.addObject(post);
		modelAndView.setViewName("posts/create");
		return modelAndView;		
	}
	
	@RequestMapping(value = "/posts/create", method = RequestMethod.POST)
	public ModelAndView create(@Valid Post post, BindingResult bindingResult){
		ModelAndView modelAndView = new ModelAndView();		
		modelAndView.setViewName("posts/create");
		// Perform validation
		if( post.getTitle().isEmpty() ){
			bindingResult.rejectValue("title", "error.post", "Title cannot be empty");
		}
		if( post.getBody().isEmpty() ){
			bindingResult.rejectValue("body", "error.post", "Content cannot be empty");
		}
		// Get author
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = this.userService.findByUserName(auth.getName());
		if( user==null ){
			bindingResult.rejectValue("author", "error.post", "Author cannot be null");
		}
		if( !bindingResult.hasErrors() ){
			post.setAuthor(user);
			this.postService.create(post);
			modelAndView.addObject("successMessage", "Post has been created");
			modelAndView.addObject("post", new Post());
		}
		return modelAndView;		
	}
	/**
	 * Remove a post from the database, notify user if post does not exist
	 * @param id
	 * @return
	 */
	@RequestMapping("/posts/delete/{id}")
	public String delete(@PathVariable("id") Long id){
		Post post = this.postService.findById(id);
		if( post==null ){
			notifyService.addErrorMessage("Cannot find post #" + id);			
		} else {
			this.postService.deleteById(id);
		}
		return "redirect:/posts/";
	}
	/**
	 * Display for to edit a post
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping( "/posts/edit/{id}" )
	public String edit(@PathVariable("id") Long id, Model model){
		Post post = this.postService.findById(id);
		if( post == null ){
			notifyService.addErrorMessage("Cannot find post #" + id);
			return "redirect:/posts/";
		}
		model.addAttribute("post", post);
		return "posts/edit";
	}
	/**
	 * Proceeds to update a post
	 * @param post
	 * @return
	 */
	@RequestMapping(value = "/posts/edit", method = RequestMethod.POST )
	public ModelAndView edit(@Valid Post post, BindingResult bindingResult){		
		ModelAndView modelAndView = new ModelAndView();		
		modelAndView.setViewName("posts/edit");
		// Perform validation
		if( post.getTitle().isEmpty() ){
			bindingResult.rejectValue("title", "error.post", "Title cannot be empty");
		}
		if( post.getBody().isEmpty() ){
			bindingResult.rejectValue("body", "error.post", "Content cannot be empty");
		}
		// Get author		
		User user = this.userService.findById(post.getAuthor().getId());
		if( user==null ){
			bindingResult.rejectValue("author", "error.post", "Author cannot be null");
		}
		if( !bindingResult.hasErrors() ){
			post.setAuthor(user);
			this.postService.create(post);
			modelAndView.addObject("successMessage", "Post has been updated");
			modelAndView.addObject("post", post);
		}
		return modelAndView;
	}
	/**
	 * Get a list of all posts in the database, it should be able to paginate and sort
	 * http://localhost:8090/posts?page=0&size=3&sort=id
	 *  
	 * @param model
	 * @return
	 */
	@RequestMapping("/posts")
	public String index(Model model, @PageableDefault(sort = {"id"}, value = 3) Pageable pageable){		
		// Get the content of the table, TODO. find a way to paginate
		Page<Post> posts = this.postService.findAll(pageable);
		
		// Define variables to be passed to view
		model.addAttribute("posts", posts);
		// Return the view model itself
		return "posts/index";
	}

}
