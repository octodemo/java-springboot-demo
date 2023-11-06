package net.codejava;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

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
		String sql = "INSERT INTO SALES (item, quantity, amount) VALUES ('" + sale.getItem() + "', " + sale.getQuantity() + ", " + sale.getAmount() + ")";
		jdbcTemplate.update(sql);
	}
	
	public Sale get(int id) {
		String sql = "SELECT * FROM SALES WHERE id = ?";
		Object[] args = {id};
		Sale sale = jdbcTemplate.queryForObject(sql, args, BeanPropertyRowMapper.newInstance(Sale.class));
		return sale;
	}

	public void update(Sale sale) {
		String sql = "UPDATE SALES SET item=:item, quantity=:quantity, amount=:amount WHERE id=:id";
		BeanPropertySqlParameterSource param = new BeanPropertySqlParameterSource(sale);
		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
		template.update(sql, param);
	}

	public void delete(int id) {
		String sql = "DELETE FROM SALES WHERE id = ?";
		jdbcTemplate.update(sql, id);
	}

	// method to clear the amount column in the database given an id
	public void clearAmount(int id) {
		String sql = "UPDATE SALES SET amount=0 WHERE id=?";
		jdbcTemplate.update(sql, id);
	}

	public List<Sale> search(String query) {
		String sql = "SELECT * FROM sales WHERE LOWER(item) LIKE LOWER(?)";
		List<Sale> listSale = jdbcTemplate.query(sql, new Object[]{"%" + query.toLowerCase() + "%"}, new BeanPropertyRowMapper<>(Sale.class));
		return listSale;
	}
}
