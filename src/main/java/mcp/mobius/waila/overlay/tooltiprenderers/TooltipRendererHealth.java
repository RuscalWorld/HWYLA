package mcp.mobius.waila.overlay.tooltiprenderers;

import com.mojang.blaze3d.systems.RenderSystem;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.ITooltipRenderer;
import mcp.mobius.waila.overlay.DisplayUtil;
import mcp.mobius.waila.overlay.HeartVariant;
import mcp.mobius.waila.overlay.IconUI;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.Dimension;

public class TooltipRendererHealth implements ITooltipRenderer {

    @Override
    public Dimension getSize(NbtCompound tag, ICommonAccessor accessor) {
        float maxHearts = Waila.CONFIG.get().getGeneral().getMaxHeartsPerLine();
        float maxHealth = tag.getFloat("max");

        int heartsPerLine = (int) (Math.min(maxHearts, Math.ceil(maxHealth)));
        int lineCount = (int) (Math.ceil(maxHealth / maxHearts));

        return new Dimension(8 * heartsPerLine, 10 * lineCount);
    }

    @Override
    public void draw(MatrixStack matrices, NbtCompound tag, ICommonAccessor accessor, int x, int y) {
        float maxHearts = Waila.CONFIG.get().getGeneral().getMaxHeartsPerLine();
        float health = tag.getFloat("health");
        float maxHealth = tag.getFloat("max");

        int heartCount = MathHelper.ceil(maxHealth);
        int heartsPerLine = (int) (Math.min(maxHearts, Math.ceil(maxHealth)));

        int xOffset = 0;
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, DrawableHelper.GUI_ICONS_TEXTURE);
        for (int i = 1; i <= heartCount; i++) {
            if (i <= MathHelper.floor(health)) {
                DisplayUtil.renderHeart(matrices, x + xOffset, y, HeartVariant.FULL);
                xOffset += 8;
            }

            if ((i > health) && (i < health + 1)) {
                DisplayUtil.renderHeart(matrices, x + xOffset, y, HeartVariant.HALF);
                xOffset += 8;
            }

            if (i >= health + 1) {
                DisplayUtil.renderHeart(matrices, x + xOffset, y, HeartVariant.EMPTY);
                xOffset += 8;
            }

            if (i % heartsPerLine == 0) {
                y += 10;
                xOffset = 0;
            }
        }
    }
}
