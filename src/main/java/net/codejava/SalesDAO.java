package net.codejava;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.data.domain.Page; // Import the Page class from the correct package
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable; // Import the Pageable class from the correct package


@Repository
public class SalesDAO {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public List<Sale> list() {
		String sql = "SELECT * FROM SALES ORDER BY id ASC";

		List<Sale> listSale = jdbcTemplate.query(sql,
				BeanPropertyRowMapper.newInstance(Sale.class));

		return listSale;
	}

	public void save(Sale sale) {
		if (sale == null) {
			throw new IllegalArgumentException("Sale object cannot be null");
		}

		if (jdbcTemplate == null) {
			throw new IllegalStateException("JdbcTemplate cannot be null");
		}

		SimpleJdbcInsert insertActor = 
			new SimpleJdbcInsert(jdbcTemplate != null ? jdbcTemplate : new JdbcTemplate());
		insertActor.withTableName("sales").usingColumns("item", "quantity", "amount");
		BeanPropertySqlParameterSource param = new BeanPropertySqlParameterSource(sale);

		insertActor.execute(param);
	}

	public Sale get(int id) {
		String sql = "SELECT * FROM SALES WHERE id = ?";
		Object[] args = {id};
		Sale sale = jdbcTemplate.queryForObject(sql, args, BeanPropertyRowMapper.newInstance(Sale.class));
		return sale;
	}

	public void update(Sale sale) {
		if (sale == null) {
			throw new IllegalArgumentException("Sale object cannot be null");
		}

		String sql = "UPDATE SALES SET item=:item, quantity=:quantity, amount=:amount WHERE id=:id";
		BeanPropertySqlParameterSource param = new BeanPropertySqlParameterSource(sale);

		if (jdbcTemplate == null) {
			throw new IllegalStateException("JdbcTemplate cannot be null");
		}

		NamedParameterJdbcTemplate template = 
			new NamedParameterJdbcTemplate(jdbcTemplate != null ? jdbcTemplate : new JdbcTemplate());
		template.update(sql, param);
	}

	public void delete(int id) {
		String sql = "DELETE FROM SALES WHERE id = ?";
		jdbcTemplate.update(sql, id);
	}

	public void clearRecord(int id) {
		// clear the amount and quantity of the record
		String sql = "UPDATE SALES SET quantity=0, amount=0 WHERE id=?";
		jdbcTemplate.update(sql, id);
	}

	public List<Sale> search(String query) {
		String sql = "SELECT * FROM sales WHERE LOWER(item) LIKE LOWER(?)";
		List<Sale> listSale = jdbcTemplate.query(sql, new Object[]{"%" + query.toLowerCase() + "%"}, new BeanPropertyRowMapper<>(Sale.class));
		return listSale;
	}

	public Page<Sale> findAll(Pageable pageable) {
		String countQuery = "SELECT count(*) FROM sales";
		Integer totalInteger = jdbcTemplate.queryForObject(countQuery, Integer.class);

		// Check if totalInteger is null
		int total = (totalInteger != null) ? totalInteger : 0;

		String query = "SELECT * FROM sales ORDER BY id ASC LIMIT ? OFFSET ?";
		List<Sale> sales = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Sale.class), pageable.getPageSize(), pageable.getOffset());

		return new PageImpl<>(sales, pageable, total);
	}

	@RequestMapping("/export")
	public void exportCSV(String query, String filename) throws IOException {
		// create an instance of FileWriter
		FileWriter writer = new FileWriter(filename);
		// create an instance of BufferedWriter
		BufferedWriter bw = new BufferedWriter(writer);
		// write the csv header
		bw.write("id,item,quantity,amount");
		bw.newLine();
		// write the csv body
		List<Sale> listSale = search(query);
		for (Sale sale : listSale) {
			bw.write(sale.getId() + "," + sale.getItem() + "," + sale.getQuantity() + "," + sale.getAmount());
			bw.newLine();
		}
		// print a message
		System.out.println("CSV file was created successfully.");
		// close the BufferedWriter and FileWriter
		bw.close();
		writer.close();
	}
}
