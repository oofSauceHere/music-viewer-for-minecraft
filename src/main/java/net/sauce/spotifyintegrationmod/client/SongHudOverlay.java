package net.sauce.spotifyintegrationmod.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.Identifier;
import net.sauce.spotifyintegrationmod.SpotifyIntegrationMod;
import net.sauce.spotifyintegrationmod.spotify.SpotifyAPI;

import java.awt.font.FontRenderContext;

public class SongHudOverlay implements HudRenderCallback {
    public static Identifier SHADOW = new Identifier(SpotifyIntegrationMod.MOD_ID, "textures/shadow.png");
    public static Identifier SPOTIFY_ICON = new Identifier(SpotifyIntegrationMod.MOD_ID, "textures/icon.png");
    public static Identifier SPOTIFY_ICON_SHADOW = new Identifier(SpotifyIntegrationMod.MOD_ID, "textures/icon_shadow.png");
    MinecraftClient client = MinecraftClient.getInstance();
    public static int showSong = -1;

    // Perhaps add an animation to the text showing up because showing up abruptly doesn't look good
    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        // Renders the song's image, title, and artist name if a song is currently playing. Otherwise, renders "No song playing."
        if(showSong == 1) {
            // I don't know what this does.
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            if(SpotifyAPI.currentId == null) {
                drawContext.drawText(client.textRenderer, "No song playing", 10, 10, 16777215, true);
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

                // I'm not really sure how the int-to-rgb conversion works yet.
                drawContext.drawText(client.textRenderer, alteredSongName, 40, 11, 16776960, true);
                drawContext.drawText(client.textRenderer, alteredArtistName, 40, 21, 16777215, true);

                int textWidth = Math.max(client.textRenderer.getWidth(alteredSongName), client.textRenderer.getWidth(alteredArtistName));
                drawContext.drawTexture(SPOTIFY_ICON_SHADOW, 53+textWidth, 13, 0, 0, 15, 15, 15, 15);
                drawContext.drawTexture(SPOTIFY_ICON, 52+textWidth, 12, 0, 0, 15, 15, 15, 15);
            }
        }
    }
}
