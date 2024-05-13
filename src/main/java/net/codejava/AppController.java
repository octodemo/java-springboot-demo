package net.codejava;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.Principal;
import java.time.LocalDate;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.dao.DuplicateKeyException;
import java.util.Date;
import javax.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.fasterxml.jackson.databind.ObjectMapper;

@EnableJpaRepositories(basePackages = "net.codejava")
@Controller
@Transactional
public class AppController {

	@Autowired
	private SalesDAO dao;

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Value("${enableSearchFeature}")
    private boolean enableSearchFeature;

	public boolean getEnableSearchFeature() {
		return this.enableSearchFeature;
	}
	
	private String handleSale(Sale sale, HttpSession session, RedirectAttributes redirectAttributes, Runnable action) {
		sale.setEditing(true);
		action.run();
		sale.setEditing(false);
	
		String lastSearchQuery = (String) session.getAttribute("lastSearchQuery");
		if (lastSearchQuery != null && !lastSearchQuery.isEmpty()) {
			redirectAttributes.addAttribute("fromSearch", true);
			return "redirect:/search?q=" + lastSearchQuery;
		} else {
			return "redirect:/";
		}
	}
	
	@RequestMapping("/")
	public String viewHomePage(Model model , Principal principal, @RequestParam(defaultValue = "0") int page, HttpSession session) {
		String lastSearchQuery = (String) session.getAttribute("lastSearchQuery");
		if (lastSearchQuery != null && !lastSearchQuery.isEmpty()) {
			session.setAttribute("lastSearchQuery", null);
		}
		
		int pageSize = 20;
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
		sale.setEditing(true);
		mav.addObject("sale", sale);
		mav.addObject("enableSearchFeature", enableSearchFeature);
		return mav;
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public String search(@RequestParam("q") String query, Model model, HttpSession session) {
		List<Sale> listSale = dao.search(query);
		model.addAttribute("listSale", listSale);

		boolean enableSearchFeature = true;
		model.addAttribute("enableSearchFeature", enableSearchFeature);
		session.setAttribute("lastSearchQuery", query);
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(listSale);
			return json;
		} catch (IOException e) {
			return "[]";
		}
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(@ModelAttribute("sale") Sale sale, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
		boolean originalEnableSearchFeature = this.enableSearchFeature;

		try {
			if (sale.getDate() == null) {
				sale.setDate(new Date());
			}
			dao.save(sale);
			sale.setEditing(false);
		} catch (DuplicateKeyException e) {
			sale.setSerialNumber(null);
			model.addAttribute("sale", sale);
			model.addAttribute("errorMessage", e.getMessage());

			model.addAttribute("enableSearchFeature", originalEnableSearchFeature);

			return "new_form";
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

		Authentication auth = new UsernamePasswordAuthenticationToken(username, password);
		try {
			auth = authenticationManager.authenticate(auth);
			SecurityContextHolder.getContext().setAuthentication(auth);
		} catch (BadCredentialsException e) {
			model.addAttribute("error", "Invalid username or password.");
			return "login";
		}

		return "redirect:/";
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(@ModelAttribute("sale") Sale sale, HttpSession session, RedirectAttributes redirectAttributes) {
		return handleSale(sale, session, redirectAttributes, () -> dao.update(sale));
	}
	
	@RequestMapping("/delete/{serialNumber}")
	public String delete(@PathVariable(name = "serialNumber") String serialNumber, HttpSession session, RedirectAttributes redirectAttributes) {
		Sale sale = dao.get(serialNumber);
		return handleSale(sale, session, redirectAttributes, () -> dao.delete(serialNumber));
	}

	@RequestMapping("/clear/{serialNumber}")
	public String clearRecord(@PathVariable(name = "serialNumber") String serialNumber, HttpSession session, RedirectAttributes redirectAttributes) {
		Sale sale = dao.get(serialNumber);
		return handleSale(sale, session, redirectAttributes, () -> dao.clearRecord(serialNumber));
	}

	@RequestMapping("/export")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", "attachment; filename=sales.csv");
		List<Sale> listSale = dao.listAll();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));
		writer.write("Serial Number, Date, Amount, Item Name");
		writer.newLine();
		for (Sale sale : listSale) {
			String line = String.format("%s, %s, %s, %s", sale.getSerialNumber(), sale.getDate(), sale.getAmount(), sale.getItem());
			writer.write(line);
			writer.newLine();
		}
		writer.flush();
	}

	@PostMapping("/import")
	public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
			
			String line = reader.readLine();
			if (line == null || !line.equals("Serial Number,Item Name,Amount,Quantity,Date")) {
				throw new IllegalArgumentException("Invalid header. Expected 'Serial Number,Item Name,Amount,Quantity,Date'");
			}

			while ((line = reader.readLine()) != null) {
				String[] fields = line.split(",");
				if (fields.length != 5) {
					throw new IllegalArgumentException("Invalid line. Each line should contain exactly 5 fields separated by commas.");
				}
			}

			List<Sale> sales = new ArrayList<>();
			reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
			reader.readLine();
			while ((line = reader.readLine()) != null) {
				String[] fields = line.split(",");
				Sale sale = new Sale();
				sale.setSerialNumber(fields[0].trim());
				sale.setItem(fields[1].trim());

				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				LocalDate date = LocalDate.parse(fields[4].trim(), formatter);
				Date sqlDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
				sale.setDate(sqlDate);

				sale.setAmount((float) Double.parseDouble(fields[2].trim()));
				sale.setQuantity(Integer.parseInt(fields[3].trim()));
				sales.add(sale);
			}

			dao.saveAll(sales);
			redirectAttributes.addFlashAttribute("message", "Successfully saved the list of Sale objects to the database");
			System.out.println("Successfully saved the list of Sale objects to the database");
		} catch (Exception e) {
			System.out.println("Error calling dao.saveAll(sales): " + e.getMessage());
		}
		return "redirect:/";
	}
}
