package net.codejava;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page; // Import the Page class from the correct package
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable; // Import the Pageable class from the correct package
import org.springframework.dao.DuplicateKeyException;

@Repository
public class SalesDAO {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public List<Sale> list(int limit, int offset) {
		String sql = "SELECT * FROM sales ORDER BY serial_number ASC LIMIT ? OFFSET ?";

		List<Sale> listSale = jdbcTemplate.query(
			sql,
			new Object[] {limit, offset},
			BeanPropertyRowMapper.newInstance(Sale.class)
		);

		return listSale;
	}

	public void save(Sale sale) {
		String sql = "INSERT INTO SALES (item, quantity, amount) VALUES ('" + sale.getItem() + "', " + sale.getQuantity() + ", " + sale.getAmount() + ")";
		jdbcTemplate.update(sql);
	}

	public Sale get(String serialNumber) {
		String sql = "SELECT * FROM SALES WHERE serial_number = ?";
		Object[] args = {serialNumber};
		Sale sale = jdbcTemplate.queryForObject(sql, args, BeanPropertyRowMapper.newInstance(Sale.class));
		return sale;
	}

	public void update(Sale sale) {
		if (sale == null) {
			throw new IllegalArgumentException("Sale object cannot be null");
		}

		String sql = "UPDATE SALES SET item=:item, quantity=:quantity, amount=:amount WHERE serial_number=:serialNumber";
		BeanPropertySqlParameterSource param = new BeanPropertySqlParameterSource(sale);

		if (jdbcTemplate == null) {
			throw new IllegalStateException("JdbcTemplate cannot be null");
		}

		NamedParameterJdbcTemplate template = 
			new NamedParameterJdbcTemplate(jdbcTemplate != null ? jdbcTemplate : new JdbcTemplate());
		template.update(sql, param);
	}

	public void delete(String serialNumber) {
		String sql = "DELETE FROM SALES WHERE serial_number = ?";
		jdbcTemplate.update(sql, serialNumber);
	}

	public void clearRecord(String serialNumber) {
		// clear the amount and quantity of the record
		String sql = "UPDATE SALES SET quantity=0, amount=0 WHERE serial_number=?";
		jdbcTemplate.update(sql, serialNumber);
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

		String query = "SELECT * FROM sales ORDER BY item ASC LIMIT ? OFFSET ?";
		List<Sale> sales = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Sale.class), pageable.getPageSize(), pageable.getOffset());

		return new PageImpl<>(sales, pageable, total);
	}

	// a method to returns a list of all sales in a jdbctemplate query to use as a csv output 
	public List<Sale> listAll() {
		String sql = "SELECT * FROM sales ORDER BY serial_number ASC";
		List<Sale> listSale = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Sale.class));
		return listSale;
	}

	// save all sales in a list
	public void saveAll(List<Sale> sales) {
		if (sales == null) {
			throw new IllegalArgumentException("List of sales cannot be null");
		}

		if (jdbcTemplate == null) {
			throw new IllegalStateException("JdbcTemplate cannot be null");
		}

		SimpleJdbcInsert insertActor = new SimpleJdbcInsert(jdbcTemplate);
		insertActor.withTableName("sales").usingColumns("serial_number", "item", "quantity", "amount", "date");

		for (Sale sale : sales) {
			BeanPropertySqlParameterSource param = new BeanPropertySqlParameterSource(sale);
			insertActor.execute(param);
		}
	}
}
