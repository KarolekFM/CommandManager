/*
 * This file is part of CommandManager.
 *
 * CommandManager is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CommandManager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CommandManager.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dsh105.command;

import com.dsh105.commodus.StringUtil;
import com.dsh105.powermessage.markup.MarkupBuilder;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandEvent<T extends CommandSender> {

    private String input;
    private ICommandManager manager;
    private String command;
    private T sender;
    private String[] args;

    private VariableMatcher variableMatcher;

    public CommandEvent(ICommandManager manager, String args, T sender) {
        this(manager, sender, args.trim().replaceAll("\\s+", " ").split("\\s"));
    }

    public CommandEvent(ICommandManager manager, T sender, String... args) {
        this(manager, args[0].trim(), sender, StringUtil.combineArray(1, " ", args));
    }

    public CommandEvent(ICommandManager manager, String command, T sender, String... args) {
        this.manager = manager;
        this.command = command;
        this.sender = sender;
        this.args = args;

        ArrayList<String> argsList = new ArrayList<>();
        argsList.add(command);
        argsList.addAll(Arrays.asList(args));
        this.input = StringUtil.combine(" ", argsList).trim();
    }

    public Plugin getPlugin() {
        return manager.getPlugin();
    }

    public ICommandManager getManager() {
        return manager;
    }

    protected void setVariableMatcher(VariableMatcher variableMatcher) {
        this.variableMatcher = variableMatcher;
    }

    public VariableMatcher getVariableMatcher() {
        return variableMatcher;
    }

    public String variable(String variable) {
        return variableMatcher.getMatchedArgumentByVariableName(variable);
    }

    public String command() {
        return command;
    }

    public T sender() {
        return sender;
    }

    public String input() {
        return input;
    }

    public String[] args() {
        return args;
    }

    public int argsLength() {
        return args().length;
    }

    public String arg(int index) {
        return args[index];
    }

    public boolean canPerform(String... permissions) {
        for (String permission : permissions) {
            if (!permission.isEmpty() && !sender.hasPermission(permission)) {
                respond(ResponseLevel.SEVERE, manager.getMessenger().getNoPermissionMessage() + (VariableMatcher.containsVariables(permission) ? " Or maybe a variable was invalid?" : ""));
                return false;
            }
        }
        return true;
    }

    public void respond(String response) {
        respond(response, manager.getMessenger().getFormatColour(), manager.getMessenger().getHighlightColour());
    }

    public void respond(ResponseLevel level, String response) {
        respond(response, level.getFormatColour(), level.getHighlightColour());
    }

    public void respond(String response, ChatColor formatColour, ChatColor highlightColour) {
        String message = formatColour + manager.getMessenger().format(response, formatColour, highlightColour);

        // Take care of any conversions, special formatting, etc.
        new MarkupBuilder().withText((manager.getResponsePrefix() != null && !manager.getResponsePrefix().isEmpty() ? manager.getResponsePrefix() + " " : "") + ChatColor.RESET + message).build().send(sender());
    }

}