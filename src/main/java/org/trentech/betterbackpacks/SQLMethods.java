package org.trentech.betterbackpacks;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class SQLMethods extends SQLUtils{

	public boolean loaded = false;
    private Object lock = new Object();
	
	public boolean tableExist(String playerName) {
		boolean b = false;
		try {
			Statement statement = getConnection().createStatement();
			DatabaseMetaData md = statement.getConnection().getMetaData();
			ResultSet rs = md.getTables(null, null, playerName , null);
			if (rs.next()){
				b = true;	
			}		
		} catch (SQLException ex) { }
		return b;
	}
	
	public void createTable(String uuid) {
		synchronized (lock) {
			try {
				PreparedStatement statement;	
				statement = prepare("CREATE TABLE `" + uuid + "` ( id INTEGER PRIMARY KEY, Backpack TEXT, Inventory BLOB, Size INTEGER, Date TEXT)");
				statement.executeUpdate();
			} catch (SQLException e) {
				System.out.println("Unable to connect to Database!");
				System.out.println(e.getMessage());
			}
		}			
	}
	
	public boolean backpackExist(String uuid, String backpack) {
		boolean b = false;
		try {
			PreparedStatement statement = prepare("SELECT * FROM `" + uuid + "`");
			ResultSet rs = statement.executeQuery();
			while (rs.next()){
				if (rs.getString("Backpack").equalsIgnoreCase(backpack)) {
					b = true;
					break;
				}
			}
		} catch (SQLException ex) {
			System.out.println("Unable to connect to Database!");
			System.out.println(ex.getMessage());
		}
		return b;
	}
	
	public void createBackpack(String uuid, String backpack, int size, byte[] inv, String date) {
		synchronized (lock) {
			try {
				PreparedStatement statement = prepare("INSERT into `" + uuid + "` (Backpack, Inventory, Size, Date) VALUES (?, ?, ?, ?)");	
				statement.setString(1, backpack);
				statement.setBytes(2, inv);
				statement.setInt(3, size);
				statement.setString(4, date);
				statement.executeUpdate();
			} catch (SQLException e) {
				System.out.println("Unable to connect to Database!");
				System.out.println(e.getMessage());
			}
		}
	}
	
	public void setBackpackSize(String uuid, String backpack, int size){
		synchronized (lock) {
			try {
				PreparedStatement statement = prepare("UPDATE `" + uuid + "` SET Size = ? WHERE Backpack = ?");
				statement.setInt(1, size);
				statement.setString(2, backpack);
				statement.executeUpdate();
			} catch (SQLException e) {
				System.out.println("Unable to connect to Database!");
				System.out.println(e.getMessage());
			}
		}
	}
	
	public void setBackpackInv(String uuid, String backpack, byte[] inv){
		synchronized (lock) {
			try {
				PreparedStatement statement = prepare("UPDATE `" + uuid + "` SET Inventory = ? WHERE Backpack = ?");
				statement.setBytes(1, inv);
				statement.setString(2, backpack);
				statement.executeUpdate();
			} catch (SQLException e) {
				System.out.println("Unable to connect to Database!");
				System.out.println(e.getMessage());
			}
		}
	}
	
	public void setBackpackDate(String uuid, String backpack, String date){
		synchronized (lock) {
			try {
				PreparedStatement statement = prepare("UPDATE `" + uuid + "` SET Date = ? WHERE Backpack = ?");
				statement.setString(1, date);
				statement.setString(2, backpack);
				statement.executeUpdate();
			} catch (SQLException e) {
				System.out.println("Unable to connect to Database!");
				System.out.println(e.getMessage());
			}
		}
	}
	
	public void setBackpackName(String uuid, String backpack, int id){
		synchronized (lock) {
			try {
				PreparedStatement statement = prepare("UPDATE `" + uuid + "` SET Backpack = ? WHERE id = ?");
				statement.setString(1, backpack);
				statement.setInt(2, id);
				statement.executeUpdate();
			} catch (SQLException e) {
				System.out.println("Unable to connect to Database!");
				System.out.println(e.getMessage());
			}
		}
	}
	
	public int getBackpackId(String uuid, String backpack) {
		int id = -1;
		try {
			PreparedStatement statement = prepare("SELECT * FROM `" + uuid + "`");
			ResultSet rs = statement.executeQuery();
			while (rs.next()){
				if (rs.getString("Backpack").equalsIgnoreCase(backpack)) {
					id = rs.getInt("id");
					break;
				}
			}
		} catch (SQLException ex) {
			System.out.println("Unable to connect to Database!");
			System.out.println(ex.getMessage());
		}
		return id;
	}
	
	public byte[] getBackpackInv(String uuid, String backpack) {
		byte[] inv = null;
		try {
			PreparedStatement statement = prepare("SELECT * FROM `" + uuid + "`");
			ResultSet rs = statement.executeQuery();
			while (rs.next()){
				if (rs.getString("Backpack").equalsIgnoreCase(backpack)) {
					inv = rs.getBytes("Inventory");
					break;
				}
			}
		} catch (SQLException ex) {
			System.out.println("Unable to connect to Database!");
			System.out.println(ex.getMessage());
		}
		return inv;
	}
	
	public String getBackpackDate(String uuid, String backpack) {
		String date = null;
		try {
			PreparedStatement statement = prepare("SELECT * FROM `" + uuid + "`");
			ResultSet rs = statement.executeQuery();
			while (rs.next()){
				if (rs.getString("Backpack").equalsIgnoreCase(backpack)) {
					date = rs.getString("Date");
					break;
				}
			}
		} catch (SQLException ex) {
			System.out.println("Unable to connect to Database!");
			System.out.println(ex.getMessage());
		}
		return date;
	}
		
	
	public int getBackpackSize(String uuid, String backpack) {
		int size = 0;
		try {
			PreparedStatement statement = prepare("SELECT * FROM `" + uuid + "`");
			ResultSet rs = statement.executeQuery();
			while (rs.next()){
				if (rs.getString("Backpack").equalsIgnoreCase(backpack)) {
					size = rs.getInt("Size");
					break;
				}
			}
		} catch (SQLException ex) {
			System.out.println("Unable to connect to Database!");
			System.out.println(ex.getMessage());
		}
		return size;
	}
	
	public void deleteBackpack(String uuid, String backpack) {		
		try {
			PreparedStatement statement = prepare("DELETE from `" + uuid + "` WHERE Backpack = ? COLLATE NOCASE");
			statement.setString(1, backpack);
			statement.executeUpdate();
		}catch (SQLException e) {
			System.out.println("Unable to connect to Database!");
			System.out.println(e.getMessage());
		} 
	}
	
	public HashMap<String, String> getBackpackList(){
		List<String> tableList = new ArrayList<String>();
		HashMap<String, String> backpacks = new HashMap<String, String>();
		try{
			Statement statement = getConnection().createStatement();
			DatabaseMetaData md = statement.getConnection().getMetaData();
			ResultSet rs = md.getTables(null, null, "%" , null);
			while(rs.next()){
				tableList.add(rs.getString(3));
			}
		}catch (SQLException e) {
			System.out.println("Unable to connect to Database!");
			System.out.println(e.getMessage());
		}
		try{
			for(String uuid : tableList){
				PreparedStatement statement = prepare("SELECT * FROM `" + uuid + "`");
				ResultSet rs = statement.executeQuery();
				while (rs.next()){
					backpacks.put(uuid, rs.getString("Backpack"));
				}
			}
		}catch (SQLException e) {
			System.out.println("Unable to connect to Database!");
			System.out.println(e.getMessage());
		}
		return backpacks;		
	}
}
