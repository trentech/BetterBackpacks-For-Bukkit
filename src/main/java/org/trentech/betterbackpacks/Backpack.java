package org.trentech.betterbackpacks;

import java.util.Date;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;

public class Backpack {

	private UUID id;
	private ItemStack[] inventory;
	private Date date;
	private int size;

	public Backpack(UUID id, ItemStack[] inventory, Date date, int size) {
		this.id = id;
		this.inventory = inventory;
		this.date = date;
		this.size = size;
	}
	
	public Backpack(ItemStack[] inventory, Date date, int size) {
		this.id = UUID.randomUUID();
		this.inventory = inventory;
		this.date = date;
		this.size = size;
	}
	
	public UUID getId() {
		return id;
	}
	
	public void setId(UUID id) {
		this.id = id;
	}

	public ItemStack[] getInventory() {
		return inventory;
	}
	
	public void setInventory(ItemStack[] inventory) {
		this.inventory = inventory;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public int getSize() {
		return size;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
}
