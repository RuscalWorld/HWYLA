package mcp.mobius.waila.overlay;

public enum HeartVariant {
    EMPTY(0),
    HALF(2),
    FULL(2);

    private final int textureIndex;

    HeartVariant(int textureIndex) {
        this.textureIndex = textureIndex;
    }

    public int getU() {
        int l;
        if (this == EMPTY) l = 0;
        else l = this == HALF ? 1 : 0;
        return 16 + (this.textureIndex * 2 + l) * 9;
    }

    public int getTextureIndex() {
        return textureIndex;
    }
}
