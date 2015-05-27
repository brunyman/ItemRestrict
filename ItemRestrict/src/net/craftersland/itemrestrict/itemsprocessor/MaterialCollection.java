package net.craftersland.itemrestrict.itemsprocessor;

import java.util.ArrayList;

public class MaterialCollection {
	
	ArrayList<MaterialData> materials = new ArrayList<MaterialData>();
	
	public void Add(MaterialData material)
	{
		int i;
		for(i = 0; i < this.materials.size() && this.materials.get(i).typeID <= material.typeID; i++);
		this.materials.add(i, material);
	}
	
	//returns a MaterialInfo complete with the friendly material name from the config file
	public MaterialData Contains(MaterialData material)
	{
		for(int i = 0; i < this.materials.size(); i++)
		{
			MaterialData thisMaterial = this.materials.get(i);
			if(material.typeID == thisMaterial.typeID && (thisMaterial.allDataValues || material.data == thisMaterial.data))
			{
				return thisMaterial;
			}
			else if(thisMaterial.typeID > material.typeID)
			{
				return null;				
			}
		}
			
		return null;
	}
	
	@Override
	public String toString()
	{
		StringBuilder stringBuilder = new StringBuilder();
		for(int i = 0; i < this.materials.size(); i++)
		{
			stringBuilder.append(this.materials.get(i).toString() + " ");
		}
		
		return stringBuilder.toString();
	}
	
	public int size()
	{
		return this.materials.size();
	}

	public void clear() 
	{
		this.materials.clear();
	}

}
