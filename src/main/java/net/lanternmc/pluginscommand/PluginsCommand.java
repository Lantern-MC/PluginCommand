package net.lanternmc.pluginscommand;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public final class PluginsCommand extends JavaPlugin {
    public static PluginsCommand me;

    public static Plugin getMe() {
        return me;
    }

    public SimpleCommandMap getCommandMap() {
        try {
            SimplePluginManager simplePluginManager = (SimplePluginManager)this.getServer().getPluginManager();
            Field commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            return (SimpleCommandMap)commandMapField.get(simplePluginManager);
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }

    private static Object getPrivateField(Object object, String field) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Class clazz = object.getClass();
        Field objectField = clazz.getDeclaredField(field);
        objectField.setAccessible(true);
        Object result = objectField.get(object);
        objectField.setAccessible(false);
        return result;
    }

    public static void unRegisterBukkitCommand(Command cmd) {
        try {
            Object result = getPrivateField(Bukkit.getServer().getPluginManager(), "commandMap");
            SimpleCommandMap commandMap = (SimpleCommandMap)result;
            Object map = getPrivateField(commandMap, "knownCommands");
            HashMap knownCommands = (HashMap)map;
            knownCommands.remove(cmd.getName());
            knownCommands.remove("bukkit:" + cmd.getName());
            Iterator var6 = cmd.getAliases().iterator();

            while(var6.hasNext()) {
                String alias = (String)var6.next();
                if (knownCommands.containsKey(alias) && ((Command)knownCommands.get(alias)).toString().contains(cmd.getName())) {
                    knownCommands.remove(alias);
                    knownCommands.remove("bukkit:" + alias);
                }
            }
        } catch (Exception var7) {
            var7.printStackTrace();
        }

    }

    @Override
    public void onEnable() {
        // Plugin startup logic


        List cl = new ArrayList();
        Iterator var3 = this.getCommandMap().getCommands().iterator();
        Command c;
        while(var3.hasNext()) {
            c = (Command)var3.next();
            if (c.getName().equalsIgnoreCase("plugins")) {
                this.getLogger().info("Unregistering /plugins command...");
                cl.add(c);
            }
            if (c.getName().equalsIgnoreCase("pl")) {
                this.getLogger().info("Unregistering /pl command...");
                cl.add(c);
            }
            if (c.getName().equalsIgnoreCase("ver")) {
                this.getLogger().info("Unregistering /ver command...");
                cl.add(c);
            }
            if (c.getName().equalsIgnoreCase("about")) {
                this.getLogger().info("Unregistering /about command...");
                cl.add(c);
            }
            if (c.getName().equalsIgnoreCase("version")) {
                this.getLogger().info("Unregistering /version command...");
//                getServer().getPluginCommand("version").setExecutor(new plugin());
                cl.add(c);
            }
            if (c.getName().equalsIgnoreCase("help")) {
                this.getLogger().info("Unregistering /help command...");
                cl.add(c);
            }
            if (c.getName().equalsIgnoreCase("?")) {
                this.getLogger().info("Unregistering /? command...");
                cl.add(c);
            }



        }

        var3 = cl.iterator();
        while(var3.hasNext()) {
            c = (Command)var3.next();
            try {
                unRegisterBukkitCommand(c);
            } catch (Exception var5) {
            }
        }

    }
}
