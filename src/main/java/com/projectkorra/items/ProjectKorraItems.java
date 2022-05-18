package com.projectkorra.items;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.projectkorra.items.loot.LootManager;
import com.projectkorra.items.menu.MenuListener;
import com.strangeone101.holoitemsapi.HoloItemsAPI;
import com.strangeone101.holoitemsapi.itemevent.EventCache;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.projectkorra.items.attribute.AttributeBuilder;
import com.projectkorra.items.command.BaseCommand;
import com.projectkorra.items.command.GiveCommand;
import com.projectkorra.items.command.ListCommand;
import com.projectkorra.items.command.RecipeCommand;
import com.projectkorra.items.command.ReloadCommand;
import com.projectkorra.items.command.TestCommand;
import com.projectkorra.items.configuration.ConfigManager;

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
		
		//AttributeOld.registerDefaultAttributes();
		new BukkitRunnable() { //Register once the server is finished setting up. Runnables will do this.
			@Override
			public void run() {
				reload();
			}
		}.runTaskLater(this, 1L);
		
		copyAllResources();

		PluginDescriptionFile pdfFile = this.getDescription();
		log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " has been enabled!");
		
		new BaseCommand();
		//new EquipCommand();
		new GiveCommand();
		new ListCommand();
		new ReloadCommand();
		new RecipeCommand();
		new TestCommand();

		HoloItemsAPI.setup(this);
		

		
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(new PKIListener(), this);
		pm.registerEvents(new MenuListener(), this);

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
		

	}

	public void reload() {
		AttributeBuilder.setupAttributes();

		new ConfigManager(); //Load config after attributes are loaded

		LootManager.loadAllLoot(); //Needs to be loaded after custom items are

		for (Player p : Bukkit.getOnlinePlayers()) {
			for (String e : errors) {
				p.sendMessage(ChatColor.RED + "[PKItems] " + e);
			}
		}
	}
	
	/**Logs an error and makes sure all admins are notified on login.*/
	public static void createError(String error) {
		if (error == null || error.equals("")) return;

		if (!errors.contains(error)) {
			errors.add(error);
		}
		
		try {
			throw new Exception(error);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private void copyAllResources() {
		//Don't copy resources unless we are booting first time
		if (new File(getDataFolder(), "config.yml").exists()) return;

		InputStream in = getResource("index.txt"); //The file containing all resources to copy

		if (in == null) {
			createError("Failed to copy all default resources outside the jar!");
			return;
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));

		try {
			String s = reader.readLine();
			int copied = 0;

			while (s != null) {
				File file = new File(getDataFolder(), s.replace('/', File.separatorChar));

				if (!file.exists()) {
					copy(getResource(s), file); //Copy each file from the jar
					copied++;
				}
				s = reader.readLine();
			}

			if (copied > 0) {
				log.info("Copied " + copied + " files");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void copy(InputStream in, File file) {
		try {
			if (!file.exists()) {
				File parent = file.getParentFile();
				if (!parent.exists()) parent.mkdirs();

				ProjectKorraItems.log.info("'" + file.getAbsolutePath() + "'");
				ProjectKorraItems.log.info("'" + file.getPath() + "'");
				ProjectKorraItems.log.info("'" + file.getName() + "'");
				ProjectKorraItems.log.info("'" + file.getCanonicalPath() + "'");
			}
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while((len=in.read(buf))>0){
				out.write(buf,0,len);
			}
			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
