package net.raiver.sbw_combined_perk.client.tooltip;

import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class ClientCombinedPerkIconsComponent implements ClientTooltipComponent {
    private static final int STEP = 18;
    private static final int MAX_PER_ROW = 8;

    private final List<ItemStack> icons;

    public ClientCombinedPerkIconsComponent(CombinedPerkIconsComponent component) {
        this.icons = component.icons();
    }

    @Override
    public int getHeight() {
        if (icons.isEmpty()) {
            return 0;
        }
        int rows = (icons.size() + MAX_PER_ROW - 1) / MAX_PER_ROW;
        return rows * STEP;
    }

    @Override
    public int getWidth(@NotNull Font font) {
        return Math.min(icons.size(), MAX_PER_ROW) * STEP;
    }

    @Override
    public void renderImage(@NotNull Font font, int x, int y, GuiGraphics guiGraphics) {
        for (int i = 0; i < icons.size(); i++) {
            ItemStack icon = icons.get(i);
            int col = i % MAX_PER_ROW;
            int row = i / MAX_PER_ROW;
            int drawX = x + col * STEP;
            int drawY = y + row * STEP;
            guiGraphics.renderItem(icon, drawX, drawY);
            guiGraphics.renderItemDecorations(font, icon, drawX, drawY);
        }
    }
}
