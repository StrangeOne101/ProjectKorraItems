package com.projectkorra.items;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.projectkorra.items.ARCHIVE.AbilityUpdater;
import com.projectkorra.items.ARCHIVE.AttributeListener;
import com.projectkorra.items.attribute.Attribute;
import com.projectkorra.items.command.BaseCommand;
import com.projectkorra.items.command.GiveCommand;
import com.projectkorra.items.command.ListCommand;
import com.projectkorra.items.command.RecipeCommand;
import com.projectkorra.items.command.ReloadCommand;
import com.projectkorra.items.command.StatsCommand;
import com.projectkorra.items.command.TestCommand;
import com.projectkorra.items.configuration.ConfigManager;
import com.projectkorra.items.customs.PKIDisplay;

import net.md_5.bungee.api.ChatColor;

public class ProjectKorraItems extends JavaPlugin {
	public static ProjectKorraItems plugin;
	public static Logger log;
	
	public static List<String> errors;

	@Override
	public void onEnable() {
		plugin = this;
		ProjectKorraItems.log = this.getLogger();
		ProjectKorraItems.errors = new ArrayList<String>();
		
		Attribute.registerDefaultAttributes();
		
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " has been enabled!");
		
		new BaseCommand();
		//new EquipCommand();
		new GiveCommand();
		new ListCommand();
		new ReloadCommand();
		new StatsCommand();
		new RecipeCommand();
		new TestCommand();
		
		new ConfigManager();
		PKIDisplay.displays = new ConcurrentHashMap<Player, PKIDisplay>();
		
		PluginManager pm = this.getServer().getPluginManager();
		
		
		
		pm.registerEvents(new PKIListener(), this);
		pm.registerEvents(new AttributeListener(), this);
		pm.registerEvents(new AbilityUpdater(), this);
		
		/*if (errors.size() >= 0 && Bukkit.getOnlinePlayers().size() > 0) {
			for (String e : errors) {
				for (Player p : Bukkit.getOnlinePlayers()) {
					p.sendMessage(ChatColor.RED + "[PKItems] " + e);
				}
			}
		}*/
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			for (String e : errors) {
				p.sendMessage(ChatColor.RED + "[PKItems] " + e);
			}
			PKIListener.updateItems(p.getInventory(), p);
		}

		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info(pdfFile.getName() + " Has Been Disabled!");
		
		if (PKIDisplay.displays != null && !PKIDisplay.displays.isEmpty()) {
			PKIDisplay.cleanup();
		}
	}
	
	/**Logs an error and makes sure all admins are notified on login.*/
	public static void createError(String error) {
		if (!errors.contains(error)) {
			errors.add(error);
		}
		
		try {
			throw new Exception(error);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
