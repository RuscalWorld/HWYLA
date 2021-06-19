package mcp.mobius.waila.overlay;

import com.google.common.collect.Maps;

import java.util.Map;

public enum IconUI {
    EXPERIENCE_BUBBLE(25, 18, 9, 9, "x");

    private final static Map<String, IconUI> ELEMENTS = Maps.newHashMap();

    static {
        for (IconUI icon : IconUI.values()) {
            ELEMENTS.put(icon.symbol, icon);
        }
    }

    public final int u, v, su, sv;
    public final int bu, bv, bsu, bsv;
    public final String symbol;

    IconUI(int u, int v, int su, int sv, String symbol) {
        this(u, v, su, sv, -1, -1, -1, -1, symbol);
    }

    IconUI(int u, int v, int su, int sv, int bu, int bv, int bsu, int bsv, String symbol) {
        this.u = u;
        this.v = v;
        this.su = su;
        this.sv = sv;
        this.bu = bu;
        this.bv = bv;
        this.bsu = bsu;
        this.bsv = bsv;
        this.symbol = symbol;
    }

    public static IconUI bySymbol(String s) {
        return ELEMENTS.getOrDefault(s, IconUI.EXPERIENCE_BUBBLE);
    }
}
