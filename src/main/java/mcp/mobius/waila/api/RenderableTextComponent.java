package mcp.mobius.waila.api;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcp.mobius.waila.api.impl.WailaRegistrar;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

import java.util.List;

public class RenderableTextComponent extends LiteralText {

    public RenderableTextComponent(Identifier id, NbtCompound data) {
        super(getRenderString(id, data));
    }

    public RenderableTextComponent(RenderableTextComponent... components) {
        super(getRenderString(components));
    }

    public List<RenderContainer> getRenderers() {
        List<RenderContainer> renderers = Lists.newArrayList();
        NbtCompound data = getData();
        if (data.contains("renders")) {
            NbtList list = data.getList("renders", NbtType.STRING);
            list.forEach(t -> {
                NbtString stringTag = (NbtString) t;
                try {
                    NbtCompound tag = StringNbtReader.parse(stringTag.asString());
                    Identifier id = new Identifier(tag.getString("id"));
                    NbtCompound dataTag = tag.getCompound("data");
                    renderers.add(new RenderContainer(id, dataTag));
                } catch (CommandSyntaxException e) {
                    // no-op
                }
            });
        } else {
            Identifier id = new Identifier(data.getString("id"));
            NbtCompound dataTag = data.getCompound("data");
            renderers.add(new RenderContainer(id, dataTag));
        }

        return renderers;
    }

    private NbtCompound getData() {
        try {
            return StringNbtReader.parse(getString());
        } catch (CommandSyntaxException e) {
            return new NbtCompound();
        }
    }

    private static String getRenderString(Identifier id, NbtCompound data) {
        NbtCompound renderData = new NbtCompound();
        renderData.putString("id", id.toString());
        renderData.put("data", data);
        return renderData.toString();
    }

    private static String getRenderString(RenderableTextComponent... components) {
        NbtCompound container = new NbtCompound();
        NbtList renderData = new NbtList();
        for (RenderableTextComponent component : components)
            renderData.add(NbtString.of(component.getString()));
        container.put("renders", renderData);
        return container.toString();
    }

    public static class RenderContainer {
        private final Identifier id;
        private final NbtCompound data;
        private final ITooltipRenderer renderer;

        public RenderContainer(Identifier id, NbtCompound data) {
            this.id = id;
            this.data = data;
            this.renderer = WailaRegistrar.INSTANCE.getTooltipRenderer(id);
        }

        public Identifier getId() {
            return id;
        }

        public NbtCompound getData() {
            return data;
        }

        public ITooltipRenderer getRenderer() {
            return renderer;
        }
    }
}
