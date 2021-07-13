package net.prosavage.factionsx.cmd;

import com.cryptomorin.xseries.XMaterial;
import io.papermc.lib.PaperLib;
import net.prosavage.factionsx.FWildAddon;
import net.prosavage.factionsx.FactionsX;
import net.prosavage.factionsx.command.engine.CommandInfo;
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder;
import net.prosavage.factionsx.command.engine.FCommand;
import net.prosavage.factionsx.core.PermissionKt;
import net.prosavage.factionsx.manager.PositionManagerKt;
import net.prosavage.factionsx.persist.CooldownData;
import net.prosavage.factionsx.persist.Message;
import net.prosavage.factionsx.persist.WildConfig;
import net.prosavage.factionsx.persist.data.FLocation;
import net.prosavage.factionsx.util.UtilKt;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CmdWild extends FCommand {

    public CmdWild() {
        // Alias for the command, so this would do `/f wild`.
        getAliases().add("wild");
        // We can add multiple
        getAliases().add("wild-tp");

        // The commandRequirements pre-check common things for you, the official way.
        // For example we could add #asFactionMember(true) if we want to make sure they're a faction member.
        // Here we do not want this executed in console, as the console cannot be teleported.
        this.commandRequirements = new CommandRequirementsBuilder()
                .asPlayer(true)
                .withRawPermission("factionsx.player.wild")
                .build();
    }


    public boolean execute(CommandInfo info) {
        Player player = info.getPlayer();

        if (!info.getFPlayer().getInBypass() && (WildConfig.usePermission && !PermissionKt.hasPermission(player, WildConfig.wildPermission))) {
            info.message(String.format(
                    Message.INSTANCE.getCommandRequirementsPlayerDoesNotHavePermission(),
                    WildConfig.wildPermission));
            return false;
        }

        final String world = player.getWorld().getName();
        if (WildConfig.blacklistedWorlds.contains(world)) {
            info.message(String.format(WildConfig.blacklistedWorldMessage, world));
            return false;
        }

        if (WildConfig.useCooldown && !info.getFPlayer().getInBypass() && CooldownData.cooldowns.containsKey(player.getUniqueId()) && (WildConfig.useCoolDownBypassPermission && !PermissionKt.hasPermission(player, WildConfig.cooldownBypassPermission))) {
            Long lastUse = CooldownData.cooldowns.get(player.getUniqueId());
            float timeElapsed = System.currentTimeMillis() - lastUse;
            timeElapsed = timeElapsed / 1000;
            if (timeElapsed <= WildConfig.cooldownSeconds) {
                float cooldown = (WildConfig.cooldownSeconds - timeElapsed);
                info.message(String.format(WildConfig.coolDownDeny, cooldown));
                return false;
            }
        }
        runRandomTp(info, player);
        return true;
    }

    private void runRandomTp(CommandInfo info, Player player) {
        // We need to use old chunk snapshot system
        boolean useNew = XMaterial.getVersion() > 12;
        World world = player.getWorld();

        if (WildConfig.teleportFromWorldToWorld.containsKey(world.getName())) {
            final List<String> worlds = Collections.singletonList(WildConfig.teleportFromWorldToWorld.get(world.getName()));
            final int worldIndex = ThreadLocalRandom.current().nextInt(worlds.size()) % worlds.size();

            World worldFromConfig = Bukkit.getWorld(worlds.get(worldIndex));
            if (worldFromConfig != null) world = worldFromConfig;
            else world = player.getWorld();
        }

        FLocation fLocation = generateValidFLocation(world);
        PaperLib.getChunkAtAsync(Bukkit.getWorld(fLocation.getWorld()), (int) fLocation.getX(), (int) fLocation.getZ())
                .thenAccept(chunk -> {
                    ChunkSnapshot chunkSnapshot = chunk.getChunkSnapshot();
                    for (int x = 0; x < 15; x++) {
                        for (int z = 0; z < 15; z++) {
                            int highestBlockYAt = chunkSnapshot.getHighestBlockYAt(x, z);
                            Material blockType = null;
                            try {
                                blockType = UtilKt.getChunkSnapshotBlockType(useNew, chunkSnapshot, x, highestBlockYAt - 1, z);

                                if (WildConfig.unsafeBlocks.contains(XMaterial.matchXMaterial(blockType))) {
                                    continue;
                                }

                                if (UtilKt.getChunkSnapshotBlockType(useNew, chunkSnapshot, x, highestBlockYAt + 1, z) != Material.AIR || UtilKt.getChunkSnapshotBlockType(useNew, chunkSnapshot, x, highestBlockYAt + 2, z) != Material.AIR) {
                                    continue;
                                }
                            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                                //e.printStackTrace();
                                return;
                            }

                            Location teleportLocation = chunk.getBlock(x, highestBlockYAt, z).getLocation().add(0.5, 0, 0.5);
                            info.message(WildConfig.teleportWarmupMessage);
                            PositionManagerKt.teleportAsyncWithWarmup(player, teleportLocation, WildConfig.teleportWarmupInSeconds * 20L, () -> {
                                info.message(WildConfig.successMessage);
                                CooldownData.cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
                            });
                            return;
                        }
                    }
                    info.message(WildConfig.unsafeLocation);
                    if (WildConfig.automaticRetry) {
                        info.message(WildConfig.automaticRetryMessage, WildConfig.automaticRetryDelay + "");
                        Bukkit.getScheduler().runTaskLater(
                            FactionsX.instance,
                            () -> runRandomTp(info, player), WildConfig.automaticRetryDelay * 20L
                        );
                    }
                });
    }


    private FLocation generateValidFLocation(World world) {
        ThreadLocalRandom current = ThreadLocalRandom.current();
        int radius = WildConfig.radiusInChunks;
        FLocation floc = new FLocation((WildConfig.centerX + WildConfig.minimumXChunkRadius) + current.nextInt(-1 * radius, radius), (WildConfig.centerZ + WildConfig.minimumZChunkRadius) + current.nextInt(-1 * radius, radius), world.getName());
        if (!floc.getFaction().isWilderness() || floc.outsideBorder(-1)) {
            return generateValidFLocation(world);
        }
        return floc;
    }

    /**
     * This is used by the command engine to tell a player what a command does in the help menu
     */
    public String getHelpInfo() {
        return WildConfig.commandWildHelp;
    }
}
