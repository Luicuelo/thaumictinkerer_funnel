package com.nekokittygames.thaumictinkerer_funnel.common.commands;

import com.nekokittygames.thaumictinkerer_funnel.common.config.TTConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandThaumicTinkererClient extends CommandBase {
    private List<String> aliases = new ArrayList<String>();

    public CommandThaumicTinkererClient() {
        this.aliases.add("thaumictinkerer_funnelc");
        this.aliases.add("ttc");
    }

    @Override
    public String getName() {
        return "thaumictinkerer_funnelc";
    }

    public List<String> getAliases() {
        return this.aliases;
    }

    public String getUsage(ICommandSender icommandsender) {
        return "/thaumictinkerer_funnel <action> [<player> [<params>]]";
    }

    public int getRequiredPermissionLevel() {
        return 2;
    }

    public boolean isUsernameIndex(String[] astring, int i) {
        return i == 1;
    }

    @Override
    public void execute(MinecraftServer minecraftServer, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sender.sendMessage(new TextComponentString("§cInvalid arguments"));
            sender.sendMessage(new TextComponentString("§cUse /tt help to get help"));
        } else {
            if (args[0].equalsIgnoreCase("listunknownenchants")) {
                for (Enchantment enchantment : Enchantment.REGISTRY) {
                    //if (ArrayUtils.contains(TTConfig.blacklistedEnchants, Enchantment.REGISTRY.getIDForObject(enchantment)))
                   //     continue;
                    ResourceLocation object = Enchantment.REGISTRY.getNameForObject(enchantment);
                    ResourceLocation iconLoc = new ResourceLocation(object.getResourceDomain(), "textures/enchant_icons/" + object.getResourcePath() + ".png");

                    try {
                        IResource res = Minecraft.getMinecraft().getResourceManager().getResource(iconLoc);
                        if (res == null)
                            sender.sendMessage(new TextComponentString("Enchantment name: " + iconLoc + " has no icon"));
                    } catch (IOException e) {
                        sender.sendMessage(new TextComponentString("Enchantment name: " + iconLoc + " has no icon"));
                    }
                }

            }
        }
    }
}
