package com.projectkorra.items.command;

import java.util.List;

import org.bukkit.command.CommandSender;

public class RecipeCommand extends PKICommand {

	public RecipeCommand() {
		super("recipe", "/bending items recipe <list/add/remove/display>", "Allows the user to modify PKI recipes", new String[] {"recip", "r", "recipes"});
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(CommandSender arg0, List<String> arg1) {
		// TODO Auto-generated method stub

	}

}
