package net.codejava;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import java.security.Principal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.PageRequest; // Add this import statement
import org.springframework.data.domain.Page; // Add this import statement
// import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "net.codejava")
@Controller
@Transactional
public class AppController {

	/**
	 *
	 */
	@Autowired
	private SalesDAO dao;

	@Autowired
	private AuthenticationManager authenticationManager;
	
	// @Autowired
	// private final SalesRecordRepository salesRecordRepository;

	// public AppController(SalesRecordRepository salesRecordRepository) {
    //     this.salesRecordRepository = salesRecordRepository;
    // }

	@Value("${enableSearchFeature}")
    private boolean enableSearchFeature;

	public boolean getEnableSearchFeature() {
		return this.enableSearchFeature;
	}
	
	@RequestMapping("/")
	public String viewHomePage(Model model , Principal principal, @RequestParam(defaultValue = "0") int page) {
		int pageSize = 20; // number of records per page
		Pageable pageable = PageRequest.of(page, pageSize);
		Page<Sale> salePage = dao.findAll(pageable);

		model.addAttribute("enableSearchFeature", enableSearchFeature);
		model.addAttribute("listSale", salePage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", salePage.getTotalPages());

		return "index";
	}

	// @GetMapping("/")
	// public String showPage(Model model, @RequestParam(defaultValue="0") int page) {
	// 	Pageable pageable = PageRequest.of(page, 5);
	// 	Page<Sale> data = salesRecordRepository.findAll(pageable);
	// 	data = data != null ? data : Page.empty(); // Assign an empty Page if data is null
	// 	model.addAttribute("data", data);
	// 	return "index";
	// }

	@RequestMapping("/new")
	public ModelAndView showNewForm() {
		ModelAndView mav = new ModelAndView("new_form");
		Sale sale = new Sale();
		mav.addObject("sale", sale);
		mav.addObject("enableSearchFeature", enableSearchFeature);
		return mav;
	}

	@RequestMapping("/edit/{id}")
	public ModelAndView showEditForm(@PathVariable(name = "id") int id) {
		ModelAndView mav = new ModelAndView("edit_form");
		Sale sale = dao.get(id);
		mav.addObject("sale", sale);
		mav.addObject("enableSearchFeature", enableSearchFeature);
		return mav;
	}

	@RequestMapping("/search")
	public String search(@ModelAttribute("q") String query, Model model) {
		List<Sale> listSale = dao.search(query);
		model.addAttribute("listSale", listSale);
		
		boolean enableSearchFeature = true; // or get this value from somewhere else
		model.addAttribute("enableSearchFeature", enableSearchFeature);
		
		return "search";
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(@ModelAttribute("sale") Sale sale) {
	    dao.save(sale);
	     
	    return "redirect:/";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String loginGet(Model model) {
		return "login";
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String loginPost(HttpServletRequest request, Model model) {
		String username = request.getParameter("username");
		String password = request.getParameter("password");

		// Authenticate the user
		Authentication auth = new UsernamePasswordAuthenticationToken(username, password);
		try {
			auth = authenticationManager.authenticate(auth);
			SecurityContextHolder.getContext().setAuthentication(auth);
		} catch (BadCredentialsException e) {
			model.addAttribute("error", "Invalid username or password.");
			return "login";
		}

		// User is authenticated, redirect to landing page
		return "redirect:/";
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(@ModelAttribute("sale") Sale sale) {
	    dao.update(sale);
	     
	    return "redirect:/";
	}
	
	@RequestMapping("/delete/{id}")
	public String delete(@PathVariable(name = "id") int id) {
	    dao.delete(id);
	    return "redirect:/";       
	}	

	@RequestMapping("/clear/{id}")
	public String clearRecord(@PathVariable(name = "id") int id) {
		dao.clearRecord(id);
		return "redirect:/";
	}

	@RequestMapping("/export")
	public String exportCSV(@ModelAttribute("q") String query, Model model) {
		try {
			dao.exportCSV(query, "export.csv");
			return "redirect:/";
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
			return "error";
		}
	}
}
