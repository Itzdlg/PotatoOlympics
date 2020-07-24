package me.schooltests.potatoolympics.core.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class PacketUtil {
    static Gson gson = new GsonBuilder().create();
    public static class FormattedText {
        String text;
        ChatColor color;
        boolean bold;
        boolean italic;

        public FormattedText(String text, ChatColor color, boolean bold, boolean italic) {
            this.text = text;
            this.color = color;
            this.bold = bold;
            this.italic = italic;
        }

        public FormattedText(String text, ChatColor color, boolean bold) {
            this(text, color, bold, false);
        }

        public FormattedText(String text, ChatColor color) {
            this(text, color, false, false);
        }

        public FormattedText(String text) {
            this(text, ChatColor.WHITE, false, false);
        }
    }

    public static void sendTitle(Player player, @Nullable FormattedText title, @Nullable FormattedText subtitle, int fadeIn, int duration, int fadeOut) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        if (title != null) {
            JsonObject titleObj = new JsonObject();
            titleObj.addProperty("text", title.text);
            titleObj.addProperty("color", title.color.name());
            titleObj.addProperty("bold", title.bold);
            titleObj.addProperty("italic", title.italic);

            PacketPlayOutTitle titlePacket = new PacketPlayOutTitle
                    (PacketPlayOutTitle.EnumTitleAction.TITLE,
                            IChatBaseComponent.ChatSerializer.a(titleObj.toString()),
                            fadeIn,
                            duration,
                            fadeOut);

            connection.sendPacket(titlePacket);
        }

        if (subtitle != null) {
            JsonObject subtitleObj = new JsonObject();
            subtitleObj.addProperty("text", subtitle.text);
            subtitleObj.addProperty("color", subtitle.color.name());
            subtitleObj.addProperty("bold", subtitle.bold);
            subtitleObj.addProperty("italic", subtitle.italic);

            PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle
                    (PacketPlayOutTitle.EnumTitleAction.TITLE,
                            IChatBaseComponent.ChatSerializer.a(subtitleObj.toString()),
                            fadeIn,
                            duration,
                            fadeOut);

            connection.sendPacket(subtitlePacket);
        }
    }
}