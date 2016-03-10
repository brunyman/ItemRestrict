package net.craftersland.itemrestrict.utils;

public class MaterialData {
	
	public int typeID;
	public byte data;
	public boolean allDataValues;
	public String description;
	public String reason;
	
	public MaterialData(int typeID, byte data, String description, String reason) {
		this.typeID = typeID;
		this.data = data;
		this.allDataValues = false;
		this.description = description;
		this.reason = reason;
	}
	
	public MaterialData(int typeID, String description, String reason) {
		this.typeID = typeID;
		this.data = 0;
		this.allDataValues = true;
		this.description = description;
		this.reason = reason;
	}
	
	private MaterialData(int typeID, byte data, boolean allDataValues, String description, String reason) {
		this.typeID = typeID;
		this.data = data;
		this.allDataValues = allDataValues;
		this.description = description;
		this.reason = reason;
	}
	
	@Override
	public String toString() {
		String returnValue = String.valueOf(this.typeID) + ":" + (this.allDataValues?"*":String.valueOf(this.data));
		if(this.description != null) returnValue += ":" + this.description + ":" + this.reason;
		
		return returnValue;
	}
	
	public static MaterialData fromString(String string) {
		
		if(string == null || string.isEmpty()) return null;
		
		String [] parts = string.split(":");
		if(parts.length < 2) return null;
		
		try {
			
			int typeID = Integer.parseInt(parts[0]);
			
			byte data;
			boolean allDataValues;
			if(parts[1].equals("*"))
			{
				allDataValues = true;
				data = 0;
			}
			else {
				allDataValues = false;
				data = (byte) Integer.parseInt(parts[1]);
			}
			
			return new MaterialData(typeID, data, allDataValues, parts.length >= 3 ? parts[2] : "", parts.length >= 4 ? parts[3] : "(No reason provided.)");
		}
		catch(NumberFormatException exception) {
			return null;
		}
	}

}
