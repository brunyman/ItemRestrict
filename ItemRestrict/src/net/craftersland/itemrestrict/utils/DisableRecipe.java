package net.craftersland.itemrestrict.utils;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import net.craftersland.itemrestrict.ItemRestrict;

public class DisableRecipe {
	
	private ItemRestrict ir;
	
	public DisableRecipe(ItemRestrict ir) {
		this.ir = ir;
		
		disableRecipesTask(5);
	}
	
	private void removeRecipe(ItemStack is) {
        Iterator<Recipe> it = Bukkit.getServer().recipeIterator();
        Recipe recipe = null;
        while (it.hasNext()) {
            recipe = it.next();
            if (recipe != null && recipe.getResult().isSimilar(is)) {
            	ir.disabledRecipes.add(recipe);
                it.remove();
            }
        }
    }
	
	public void disableRecipesTask(int delay) {
		Bukkit.getScheduler().runTaskLaterAsynchronously(ir, new Runnable() {

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				if (ir.craftingDisabled.isEmpty() == false) {
					for (String s : ir.craftingDisabled) {
						String[] s1 = s.split(":");
						try {
							int id = Integer.parseInt(s1[0]);
							Material m = Material.getMaterial(id);
							ItemStack is = null;
							if (s1[1].contains("*") == true) {
								is = new ItemStack(m);
							} else {
								short b = Short.parseShort(s1[1]);
								is = new ItemStack(m, 1, b);
							}
							
							removeRecipe(is);
						} catch (Exception e) {
							ItemRestrict.log.warning("Failed to disable crafting for item: " + s1[0] + ":" + s1[1] + " . Error: " + e.getMessage());
						}
					}
				}
			}
			
		}, delay * 20L);
	}
	
	public void restoreRecipes() {
		Bukkit.getScheduler().runTaskAsynchronously(ir, new Runnable() {

			@Override
			public void run() {
				if (ir.disabledRecipes.isEmpty() == false) {
					for (Recipe r : ir.disabledRecipes) {
						try {
							Bukkit.addRecipe(r);
						} catch (Exception e) {
							ItemRestrict.log.warning("Failed to restore disabled recipe for: " + r.getResult().getType().toString() + " .Error: " + e.getMessage());
						}
					}
					ir.disabledRecipes.clear();
				}
			}
			
		});
	}

}
