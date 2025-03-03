package net.sauce.spotifyintegrationmod.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.sauce.spotifyintegrationmod.SpotifyIntegrationMod;
import net.sauce.spotifyintegrationmod.spotify.SpotifyAPI;

import java.awt.font.FontRenderContext;

public class SongHudOverlay implements HudRenderCallback {
    public static final Identifier SPOTIFY_LOGO = new Identifier(SpotifyIntegrationMod.MOD_ID, "textures/logo.png");
    // public static Identifier SPOTIFY_ICON = new Identifier(SpotifyIntegrationMod.MOD_ID, "textures/icon.png");
    // public static Identifier SPOTIFY_ICON_SHADOW = new Identifier(SpotifyIntegrationMod.MOD_ID, "textures/icon_shadow.png");

    // The reason these 3 exist, despite being just rectangles, is that I really couldn't get DrawContext.fill() to work.
    public static final Identifier SHADOW = new Identifier(SpotifyIntegrationMod.MOD_ID, "textures/shadow.png");
    public static final Identifier PROGRESS_BAR_EMPTY = SHADOW;
    public static final Identifier PROGRESS_BAR = new Identifier(SpotifyIntegrationMod.MOD_ID, "textures/progress.png");

    MinecraftClient client = MinecraftClient.getInstance();
    public static boolean showSong = false; // It makes more sense for this to be here than in KeyInputHandler

    // Perhaps add an animation to the text showing up because showing up abruptly doesn't look good
    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        // Should stay the same size despite gui size changes

        // Renders the song's image, title, and artist name if a song is currently playing. Otherwise, renders "No song playing."
        if(showSong) {
            if(SpotifyAPI.currentId == null) {
                drawContext.drawText(client.textRenderer, "No song playing", 10, 10, 0xffffff, true);
            } else {
                // Responsible for showing the image. If the image isn't registered with the game yet, just show an empty square.
                drawContext.drawTexture(SHADOW, 6, 6, 0, 0,30, 30, 30, 30);
                if(MinecraftClient.getInstance().getTextureManager().getOrDefault(SpotifyAPI.currentId, null) == null) {
                    drawContext.drawTexture(SHADOW, 5, 5, 0, 0, 30, 30, 30, 30);
                }
                else drawContext.drawTexture(SpotifyAPI.currentId, 5, 5, 0, 0, 30, 30, 30, 30);

                // If the song name is too long, we shorted it with an ellipsis.
                String alteredSongName = SpotifyAPI.songData.get("songName").length() > 25 ? SpotifyAPI.songData.get("songName").substring(0, 25) + "..."
                        : SpotifyAPI.songData.get("songName");
                String alteredArtistName = SpotifyAPI.songData.get("artistName").length() > 25 ? SpotifyAPI.songData.get("artistName").substring(0, 25) + "..."
                        : SpotifyAPI.songData.get("artistName");

                drawContext.drawText(client.textRenderer, alteredSongName, 40, 11, 0xffff00, true);
                drawContext.drawText(client.textRenderer, alteredArtistName, 40, 21, 0xffffff, true);
                drawContext.drawTexture(SPOTIFY_LOGO, 5, 38, 0, 0, 30, 9, 30, 9);

                int textWidth = Math.max(client.textRenderer.getWidth(alteredSongName), client.textRenderer.getWidth(alteredArtistName));
                int duration = Integer.parseInt(SpotifyAPI.songData.get("songDuration"));
                int progress = Integer.parseInt(SpotifyAPI.songData.get("progress"));
                int barWidth = Math.max(textWidth, 75);
                drawContext.drawTexture(PROGRESS_BAR_EMPTY, 40, 40, 0, 0, barWidth, 5, 15, 15);
                drawContext.drawTexture(PROGRESS_BAR, 40, 40, 0, 0, (int) Math.floor(((double) progress/duration)*barWidth), 5, 15, 15);
            }
        }
    }
}
