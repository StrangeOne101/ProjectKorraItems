package com.projectkorra.items.items;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;

import com.projectkorra.items.CustomItemListener;
import com.projectkorra.items.PKItem;

public class Glider extends PKItem implements CustomItemListener {

	public static Set<Player> gliding = new HashSet<Player>();
	
	public Glider(String name, byte ID) {
		super(name, ID);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onChange(ChangeEvent event) {
		
		if (event.isActive()) {
			gliding.add(event.getPlayer());
		} else if (gliding.contains(event.getPlayer())) {
			gliding.remove(event.getPlayer());
		}

	}

}
