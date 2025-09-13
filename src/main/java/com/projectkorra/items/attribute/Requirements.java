package com.projectkorra.items.attribute;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import com.projectkorra.items.configuration.ConfigManager;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.Element.SubElement;

public class Requirements {

	//Generic Requirements
	private Set<String> permissions = new HashSet<String>();;
	private Set<String> worlds = new HashSet<String>();
	private Set<Element> elements = new HashSet<Element>();

	//Entity Requirements
	private Set<AttributeTarget> killedBy = new HashSet<>();
	private Set<String> customNames = new HashSet<>();
	private Optional<Boolean> isBaby = Optional.empty();
	
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
			if (player.getLocation().getWorld().getName().equalsIgnoreCase(world)) {
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
				} 
			} else if (!bPlayer.hasElement(element)) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * If a user does not meet the requirements, give them a reason why
	 * @param player The player
	 * @return The reason
	 */
	public String getReason(Player player) {
		for (String perm : permissions) {
			if (!player.hasPermission(perm)) {
				return ConfigManager.languageConfig.get().getString("Item.Use.Deny.Permission");
			}
		}
		
		boolean isInWorld = worlds.size() == 0;
		for (String world : worlds) {
			if (player.getLocation().getWorld().getName().equalsIgnoreCase(world)) {
				isInWorld = true;
				break;
			}
		}
		if (!isInWorld) ConfigManager.languageConfig.get().getString("Item.Use.Deny.World");
		
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
		if (bPlayer == null && elements.size() > 0) return ConfigManager.languageConfig.get().getString("Item.Use.Deny.Element").replace("{element}", elements.iterator().next().getName());
		
		for (Element element : elements) {
			if (element instanceof SubElement) {
				if (!bPlayer.hasSubElement((SubElement) element)) {
					return ConfigManager.languageConfig.get().getString("Item.Use.Deny.Element").replace("{element}", element.getName());
				} 
			} else if (!bPlayer.hasElement(element)) {
				return ConfigManager.languageConfig.get().getString("Item.Use.Deny.Element").replace("{element}", element.getName());
			}
		}
		
		return ConfigManager.languageConfig.get().getString("Item.Use.Deny.None");
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
	
	@Override
	public String toString() {
		String s = "";
		if (permissions.size() == 0 && worlds.size() == 0 && elements.size() == 0) {
			return "None";
		}
		if (worlds.size() > 0) {
			s = "Worlds=[" + String.join(",", worlds) + "], ";
		}
		if (permissions.size() > 0) {
			s = s + "Permissions=[" + String.join(",", permissions) + "],";
		}
		if (elements.size() > 0) {
			s = s + "Elements=[" + String.join(",", elements.stream().map(e -> e.getName()).collect(Collectors.joining(","))) + "]";
		}
		return s;
	}
}
