package com.projectkorra.items.attribute;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.Element.SubElement;

public class Requirements {
	
	private Set<String> permissions = new HashSet<String>();;
	private Set<String> worlds = new HashSet<String>();
	private Set<Element> elements = new HashSet<Element>();
	
	/**
	 * Whether a player meets the requirements or not
	 * @param player The player
	 * @return
	 */
	public boolean meets(Player player) {
		for (String perm : permissions) {
			if (!player.hasPermission(perm)) {
				return false;
			}
		}
		boolean isInWorld = worlds.size() == 0;
		for (String world : worlds) {
			if (!player.getLocation().getWorld().getName().equalsIgnoreCase(world)) {
				isInWorld = true;
				break;
			}
		}
		if (!isInWorld) return false;
		
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
		if (bPlayer == null && elements.size() > 0) return false;
		
		for (Element element : elements) {
			if (element instanceof SubElement) {
				if (!bPlayer.hasSubElement((SubElement) element)) {
					return false;
				} else if (!bPlayer.hasElement(element)) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	public void setElements(Collection<Element> elements) {
		this.elements.clear();
		this.elements.addAll(elements);
	}
	
	public void setPermissions(Collection<String> permissions) {
		this.permissions.clear();
		this.permissions .addAll(permissions);
	}
	
	public void setWorlds(Collection<String> worlds) {
		this.worlds.clear();
		this.worlds.addAll(worlds);
	}
	
	public void setElement(Element element) {
		this.elements.clear();
		this.elements.add(element);
	}
	
	public void setPermission(String permission) {
		this.permissions.clear();
		this.permissions.add(permission);
	}
	
	public void setWorld(String world) {
		this.worlds.clear();
		this.worlds.add(world);
	}
}
