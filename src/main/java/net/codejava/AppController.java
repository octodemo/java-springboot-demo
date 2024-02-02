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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.PageRequest; // Add this import statement
import org.springframework.data.domain.Page; // Add this import statement
// import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.dao.DuplicateKeyException;
import java.util.Date;
import javax.servlet.http.HttpSession;
// import java.util.logging.Logger;
// import java.util.logging.Level;

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
	
	// private static final Logger logger = Logger.getLogger(AppController.class.getName());

	@Value("${enableSearchFeature}")
    private boolean enableSearchFeature;

	public boolean getEnableSearchFeature() {
		return this.enableSearchFeature;
	}
	
	@RequestMapping("/")
	public String viewHomePage(Model model , Principal principal, @RequestParam(defaultValue = "0") int page, HttpSession session) {
		String lastSearchQuery = (String) session.getAttribute("lastSearchQuery");
		if (lastSearchQuery != null && !lastSearchQuery.isEmpty()) {
			session.setAttribute("lastSearchQuery", null); // set lastSearchQuery to null
		}
		
		int pageSize = 20; // number of records per page
		Pageable pageable = PageRequest.of(page, pageSize);
		Page<Sale> salePage = dao.findAll(pageable);

		model.addAttribute("enableSearchFeature", enableSearchFeature);
		model.addAttribute("listSale", salePage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", salePage.getTotalPages());
		return "index";
	}

	@RequestMapping("/new")
	public ModelAndView showNewForm() {
		ModelAndView mav = new ModelAndView("new_form");
		Sale sale = new Sale();
		mav.addObject("sale", sale);
		mav.addObject("currentDate", LocalDate.now());
		mav.addObject("enableSearchFeature", enableSearchFeature);
		return mav;
	}

	@RequestMapping("/edit/{serialNumber}")
	public ModelAndView showEditForm(@PathVariable(name = "serialNumber") String serialNumber) {
		ModelAndView mav = new ModelAndView("edit_form");
		Sale sale = dao.get(serialNumber);
		mav.addObject("sale", sale);
		mav.addObject("enableSearchFeature", enableSearchFeature);
		return mav;
	}

	@RequestMapping("/search")
	public String search(@ModelAttribute("q") String query, Model model, HttpSession session) {
		List<Sale> listSale = dao.search(query);
		model.addAttribute("listSale", listSale);

		boolean enableSearchFeature = true;
		model.addAttribute("enableSearchFeature", enableSearchFeature);
		session.setAttribute("lastSearchQuery", query); // save the last search query in the session
		return "search";
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(@ModelAttribute("sale") Sale sale, Model model, RedirectAttributes redirectAttributes) {
		try {
			if (sale.getDate() == null) {
				sale.setDate(new Date());
			}
			dao.save(sale);
		} catch (DuplicateKeyException e) {
			sale.setSerialNumber(null); // clear the serial number
			model.addAttribute("sale", sale); // add the sale object to the model
			model.addAttribute("errorMessage", e.getMessage());
			model.addAttribute("enableSearchFeature", true); // set enableSearchFeature to true
			return "new_form"; // return the form view
		}

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
	public String update(@ModelAttribute("sale") Sale sale, HttpSession session, RedirectAttributes redirectAttributes) {
		dao.update(sale);
		
		String lastSearchQuery = (String) session.getAttribute("lastSearchQuery");
		if (lastSearchQuery != null && !lastSearchQuery.isEmpty()) {
			redirectAttributes.addAttribute("fromSearch", true);
			return "redirect:/search?q=" + lastSearchQuery;
		} else {
			return "redirect:/";
		}
	}
	
	@RequestMapping("/delete/{serialNumber}")
	public String delete(@PathVariable(name = "serialNumber") String serialNumber, HttpSession session, RedirectAttributes redirectAttributes) {
		dao.delete(serialNumber);
		
		String lastSearchQuery = (String) session.getAttribute("lastSearchQuery");
		if (lastSearchQuery != null && !lastSearchQuery.isEmpty()) {
			redirectAttributes.addAttribute("fromSearch", true);
			return "redirect:/search?q=" + lastSearchQuery;
		} else {
			return "redirect:/";
		}
	}

	@RequestMapping("/clear/{serialNumber}")
	public String clearRecord(@PathVariable(name = "serialNumber") String serialNumber, HttpSession session, RedirectAttributes redirectAttributes) {
		dao.clearRecord(serialNumber);
		
		String lastSearchQuery = (String) session.getAttribute("lastSearchQuery");
		if (lastSearchQuery != null && !lastSearchQuery.isEmpty()) {
			redirectAttributes.addAttribute("fromSearch", true);
			return "redirect:/search?q=" + lastSearchQuery;
		} else {
			return "redirect:/";
		}
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
