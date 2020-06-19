package xyz.przemyk.gutech.blocks.generator;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import xyz.przemyk.gutech.PrzemekTechMod;

public class FurnaceGeneratorScreen extends ContainerScreen<FurnaceGeneratorContainer> {
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(PrzemekTechMod.MODID, "textures/gui/furnace_generator.png");

    public FurnaceGeneratorScreen(FurnaceGeneratorContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
        if (isPointInRegion(153, 7, 16, 72, mouseX, mouseY)) {
            renderTooltip("Energy: " + container.getEnergy() + " RF", mouseX, mouseY);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String s = this.title.getFormattedText();
        this.font.drawString(s, (float)(this.xSize / 2 - this.font.getStringWidth(s) / 2), 6.0F, 4210752);
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bindTexture(GUI_TEXTURE);
        int relX = (width - xSize) / 2;
        int relY = (height - ySize) / 2;
        blit(relX, relY, 0, 0, xSize, ySize);

        if (container.isBurning()) {
            int k = container.getBurnTime() * 13 / 200;
            this.blit(guiLeft + 80, guiTop + 57 - k, 176, 12 - k, 14, k + 1);
        }

        int energy = container.getEnergy();
        if (energy > 0) {
            int k = energy * 71 / 10000;
            this.blit(guiLeft + 153, guiTop + 78 - k, 176, 85 - k, 16, k + 1);
        }
    }
}
