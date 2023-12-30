package net.codejava;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AppController {

	/**
	 *
	 */
	@Autowired
	private SalesDAO dao;
	
	@Value("${enableSearchFeature}")
    private boolean enableSearchFeature;

	public boolean getEnableSearchFeature() {
		return this.enableSearchFeature;
	}
	
	@RequestMapping("/")
	public String viewHomePage(Model model) {
		List<Sale> listSale = dao.list();
		model.addAttribute("enableSearchFeature", enableSearchFeature);
		model.addAttribute("listSale", listSale);
	    return "index";
	}
	
	@RequestMapping("/new")
	public String showNewForm(Model model) {
	    Sale sale = new Sale();
	    model.addAttribute("sale", sale);
	     
	    return "new_form";
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(@ModelAttribute("sale") Sale sale) {
	    dao.save(sale);
	     
	    return "redirect:/";
	}
	
	@RequestMapping("/edit/{id}")
	public ModelAndView showEditForm(@PathVariable(name = "id") int id) {
	    ModelAndView mav = new ModelAndView("edit_form");
	    Sale sale = dao.get(id);
	    mav.addObject("sale", sale);
	     
	    return mav;
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

	// method getmapping for search functionality
	@RequestMapping("/search")
	public String search(@ModelAttribute("q") String query, Model model) {
		List<Sale> listSale = dao.search(query);
		model.addAttribute("listSale", listSale);
		return "search";
	}
}
