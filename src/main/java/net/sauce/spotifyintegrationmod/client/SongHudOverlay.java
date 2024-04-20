package net.sauce.spotifyintegrationmod.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.Identifier;
import net.sauce.spotifyintegrationmod.SpotifyIntegrationMod;
import net.sauce.spotifyintegrationmod.spotify.SpotifyServer;

public class SongHudOverlay implements HudRenderCallback {
    public static Identifier SHADOW = new Identifier(SpotifyIntegrationMod.MOD_ID, "textures/shadow.png");

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();

        // I don't know what this does.
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // Renders the song's image, title, and artist name if a song is currently playing. Otherwise, renders "No song playing."
        if(SpotifyServer.showSong == 1) {
            if(SpotifyServer.currentId == null) {
                drawContext.drawText(client.textRenderer, "No song playing", 10, 10, 16777215, true);
            } else {
                drawContext.drawTexture(SHADOW, 6, 6, 0, 0, 30, 30, 30, 30);
                drawContext.drawTexture(SpotifyServer.currentId, 5, 5, 0, 0, 30, 30, 30, 30);
                String alteredSongName = SpotifyServer.songData.get("songName").length() > 25 ? SpotifyServer.songData.get("songName").substring(0, 25) + "..." : SpotifyServer.songData.get("songName");
                drawContext.drawText(client.textRenderer, alteredSongName, 40, 11, 16776960, true);
                drawContext.drawText(client.textRenderer, SpotifyServer.songData.get("artistName"), 40, 21, 16777215, true);
            }
        }
    }
}
