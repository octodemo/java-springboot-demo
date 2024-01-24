package net.codejava;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sales")
public class Sale {
	@Id
	private Long id;
	private String item;
	private int quantity;
	private float amount;

	protected Sale() {
	}

	protected Sale(final String item, final int quantity, final float amount) {
		this.item = item;
		this.quantity = quantity;
		this.amount = amount;
	}

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getItem() {
		return item;
	}

	public void setItem(final String item) {
		this.item = item;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(final int quantity) {
		this.quantity = quantity;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(final float amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "Sale [id=" + id + ", item=" + item + ", quantity=" + quantity + ", amount=" + amount + "]";
	}
}
