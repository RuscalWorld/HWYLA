package mcp.mobius.waila.overlay;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.overlay.tooltiprenderers.TooltipRendererHealth;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DisplayUtil {

    private static final String[] NUM_SUFFIXES = new String[]{"", "k", "m", "b", "t"};
    private static final int MAX_LENGTH = 4;
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public static void renderStack(MatrixStack matrices, int x, int y, ItemStack stack) {
        enable3DRender();
        try {
            CLIENT.getItemRenderer().renderGuiItemIcon(stack, x, y);
            ItemStack overlayRender = stack.copy();
            overlayRender.setCount(1);
            CLIENT.getItemRenderer().renderGuiItemOverlay(CLIENT.textRenderer, overlayRender, x, y);
            renderStackSize(matrices, CLIENT.textRenderer, stack, x, y);
        } catch (Exception e) {
            String stackStr = stack != null ? stack.toString() : "NullStack";
            WailaExceptionHandler.handleErr(e, "renderStack | " + stackStr, null);
        }
        enable2DRender();
    }

    public static void renderStackSize(MatrixStack matrices, TextRenderer fr, ItemStack stack, int xPosition, int yPosition) {
        if (!stack.isEmpty() && stack.getCount() != 1) {
            String s = shortHandNumber(stack.getCount());

            if (stack.getCount() < 1)
                s = Formatting.RED + String.valueOf(stack.getCount());

            RenderSystem.disableDepthTest();
            RenderSystem.disableBlend();
            fr.drawWithShadow(matrices, s, (float) (xPosition + 19 - 2 - fr.getWidth(s)), (float) (yPosition + 6 + 3), 16777215);
            RenderSystem.enableDepthTest();
            RenderSystem.enableBlend();
        }
    }

    private static String shortHandNumber(Number number) {
        String shorthand = new DecimalFormat("##0E0").format(number);
        shorthand = shorthand.replaceAll("E[0-9]", NUM_SUFFIXES[Character.getNumericValue(shorthand.charAt(shorthand.length() - 1)) / 3]);
        while (shorthand.length() > MAX_LENGTH || shorthand.matches("[0-9]+\\.[a-z]"))
            shorthand = shorthand.substring(0, shorthand.length() - 2) + shorthand.substring(shorthand.length() - 1);

        return shorthand;
    }

    public static void enable3DRender() {
        RenderSystem.enableDepthTest();
    }

    public static void enable2DRender() {
        RenderSystem.disableDepthTest();
    }

    public static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        float zLevel = 0.0F;

        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;

        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableTexture();

        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(left + right, top, zLevel).color(f1, f2, f3, f).next();
        buffer.vertex(left, top, zLevel).color(f1, f2, f3, f).next();
        buffer.vertex(left, top + bottom, zLevel).color(f5, f6, f7, f4).next();
        buffer.vertex(left + right, top + bottom, zLevel).color(f5, f6, f7, f4).next();
        tessellator.draw();

        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    public static void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height, int tw, int th) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        float zLevel = 0.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        buffer.vertex(x, y + height, zLevel).texture((float) (textureX) * f, (float) (textureY + th) * f1).next();
        buffer.vertex(x + width, y + height, zLevel).texture((float) (textureX + tw) * f, (float) (textureY + th) * f1).next();
        buffer.vertex(x + width, y, zLevel).texture((float) (textureX + tw) * f, (float) (textureY) * f1).next();
        buffer.vertex(x, y, zLevel).texture((float) (textureX) * f, (float) (textureY) * f1).next();
        tessellator.draw();
    }

    public static List<Text> itemDisplayNameMultiline(ItemStack itemstack) {
        List<Text> namelist = null;
        try {
            namelist = itemstack.getTooltip(CLIENT.player, TooltipContext.Default.NORMAL);
        } catch (Throwable ignored) {
        }

        if (namelist == null)
            namelist = new ArrayList<>();

        if (namelist.isEmpty())
            namelist.add(new LiteralText("Unnamed"));

        namelist.set(0, new LiteralText(itemstack.getRarity().formatting.toString() + namelist.get(0)));
        for (int i = 1; i < namelist.size(); i++)
            namelist.set(i, namelist.get(i));

        return namelist;
    }

    public static String itemDisplayNameShort(ItemStack itemstack) {
        List<Text> list = itemDisplayNameMultiline(itemstack);
        return String.format(Waila.CONFIG.get().getFormatting().getBlockName(), list.get(0).getString());
    }

    public static void renderHeart(MatrixStack matrices, int x, int y, HeartVariant variant) {
        if (variant == HeartVariant.HALF) renderHeart(matrices, x, y, HeartVariant.EMPTY);
        DrawableHelper.drawTexture(matrices, x, y, 0, variant.getU(), 0, 9, 9, 256, 256);
    }

    public static void renderIcon(int x, int y, int sx, int sy, IconUI icon) {
        RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
        CLIENT.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);

        if (icon == null)
            return;

        if (icon.bu != -1)
            DisplayUtil.drawTexturedModalRect(x, y, icon.bu, icon.bv, sx, sy, icon.bsu, icon.bsv);
        DisplayUtil.drawTexturedModalRect(x, y, icon.u, icon.v, sx, sy, icon.su, icon.sv);
    }
}