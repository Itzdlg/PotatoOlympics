package me.schooltests.potatoolympics.bedwars.events;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import me.schooltests.potatoolympics.bedwars.POBedwars;
import me.schooltests.potatoolympics.bedwars.util.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class InvisEvent implements Listener {
    private static final Map<Player, String> names = new WeakHashMap<>();
    private static final Map<Player, Boolean> foot = new HashMap<>();
    public InvisEvent() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(POBedwars.getInstance(), PacketType.Play.Server.PLAYER_INFO) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (event.getPacket().getPlayerInfoAction().read(0) != EnumWrappers.PlayerInfoAction.ADD_PLAYER) return;
                List<PlayerInfoData> newPlayerInfoDataList = new ArrayList<>();
                List<PlayerInfoData> playerInfoDataList = event.getPacket().getPlayerInfoDataLists().read(0);
                for (PlayerInfoData playerInfoData : playerInfoDataList) {
                    if (playerInfoData == null || playerInfoData.getProfile() == null || Bukkit.getPlayer(playerInfoData.getProfile().getUUID()) == null) { //Unknown Player
                        newPlayerInfoDataList.add(playerInfoData);
                        continue;
                    }
                    WrappedGameProfile profile = playerInfoData.getProfile();
                    profile = profile.withName(getNameToSend(profile.getUUID()));
                    PlayerInfoData newPlayerInfoData = new PlayerInfoData(profile, playerInfoData.getPing(), playerInfoData.getGameMode(), playerInfoData.getDisplayName());
                    newPlayerInfoDataList.add(newPlayerInfoData);
                }
                event.getPacket().getPlayerInfoDataLists().write(0, newPlayerInfoDataList);
            }
        });
    }

    @EventHandler
    public void onInvis(PlayerItemConsumeEvent e) {
        if (POBedwars.getInstance().activeGame) {
            if (e.getItem().getType() == Material.POTION && e.getItem().hasItemMeta()) {
                ItemStack itemStack;
                PotionMeta meta = (PotionMeta) e.getItem().getItemMeta();
                if (meta.hasCustomEffect(PotionEffectType.INVISIBILITY)) {
                    Player p = e.getPlayer();
                    names.put(p, "");
                    POBedwars.getInstance().getBedwarsGame().getGameTasks().add(Bukkit.getScheduler().runTaskLater(POBedwars.getInstance(), () -> names.remove(p), 30 * 20));

                    BukkitTask stepTask = Bukkit.getScheduler().runTaskTimer(POBedwars.getInstance(), () -> {
                        Location l = p.getLocation(); //Get the player's location
                        l.setY(Math.floor(l.getY())); //Make sure the location's y is an integer

                        if (!l.clone().subtract(0, 1, 0).getBlock().isEmpty()) //Get the block under the player's feet and make sure it exists (This prevents footprints from spawning in the air)
                        {
                            double x = Math.cos(Math.toRadians(p.getLocation().getYaw())) * 0.25d; //If you don't understand trigonometry, just think of it as rotating the footprints to the direction the player is looking.
                            double y = Math.sin(Math.toRadians(p.getLocation().getYaw())) * 0.25d;

                            if (foot.get(p)) //This code just modifies the location with the rotation and the current foot
                                l.add(x, 0.025D, y);
                            else
                                l.subtract(x, -0.025D, y);

                            ParticleEffect.FOOTSTEP.display(0, 0, 0, 0, 2, l, 100); //And finally spawn the footprints in, increase the "2" argument for how dark/visible the footprints should be
                            foot.put(p, foot.get(p) == null || !foot.get(p)); //Switch to the other foot
                        }
                    }, 5, 5);

                    POBedwars.getInstance().getBedwarsGame().getGameTasks().add(stepTask);
                    POBedwars.getInstance().getBedwarsGame().getGameTasks().add(Bukkit.getScheduler().runTaskLater(POBedwars.getInstance(), stepTask::cancel, 30 * 20));


                }
            }
        }
    }

    private String getNameToSend(UUID id) {
        Player p = Bukkit.getPlayer(id);
        if (!names.containsKey(p) || !POBedwars.getInstance().activeGame) return p.getName();
        return names.get(p);
    }

    public static Map<Player, String> getNames() {
        return names;
    }
}