package com.example.claudeclient.autosell;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

import java.util.*;

public class SellMenuScreen extends Screen {
    static final int COLS = 12, ROWS = 5, CELL = 20, PER_PAGE = COLS * ROWS;
    static List<Item> ALL;

    List<Item> filtered = new ArrayList<>();
    int page = 0;
    String query = "";
    int left, top;
    TextFieldWidget search;
    ButtonWidget loopBtn, powerBtn;

    final Screen parent;
    public SellMenuScreen() { this(null); }
    public SellMenuScreen(Screen parent) { super(Text.literal("Auto Sell")); this.parent = parent; }

    @Override
    public void close() { client.setScreen(parent); }

    static List<Item> buildAll() {
        List<Item> list = new ArrayList<>();
        Set<String> skip = Set.of("air", "barrier", "light", "debug_stick", "command_block",
            "chain_command_block", "repeating_command_block", "command_block_minecart", "structure_block",
            "structure_void", "jigsaw", "knowledge_book", "test_block", "test_instance_block");
        for (Item it : Registries.ITEM) {
            var id = Registries.ITEM.getId(it);
            String p = id.getPath();
            if (!id.getNamespace().equals("minecraft")) continue;
            if (skip.contains(p) || p.endsWith("_spawn_egg") || p.startsWith("infested_")) continue;
            list.add(it);
        }
        list.sort(Comparator.comparing(i -> i.getName().getString()));
        return list;
    }

    void refilter() {
        String q = query.toLowerCase(Locale.ROOT).trim();
        filtered = new ArrayList<>();
        for (Item it : ALL) {
            if (q.isEmpty() || it.getName().getString().toLowerCase(Locale.ROOT).contains(q)
                    || Registries.ITEM.getId(it).getPath().contains(q.replace(' ', '_'))) filtered.add(it);
        }
        int maxPage = Math.max(0, (filtered.size() - 1) / PER_PAGE);
        if (page > maxPage) page = maxPage;
    }

    @Override
    protected void init() {
        if (ALL == null) ALL = buildAll();
        int gridW = COLS * CELL;
        left = (width - gridW) / 2;
        top = 40;

        search = new TextFieldWidget(textRenderer, left, 16, 150, 18, Text.literal("search"));
        search.setText(query);
        search.setChangedListener(s -> { query = s; page = 0; refilter(); });
        addDrawableChild(search);
        setInitialFocus(search);

        // ปุ่มโปร่งใสวางทับแต่ละช่อง (ไอคอนวาดใน render)
        for (int i = 0; i < PER_PAGE; i++) {
            final int cell = i;
            int x = left + (i % COLS) * CELL, y = top + (i / COLS) * CELL;
            ButtonWidget b = ButtonWidget.builder(Text.empty(), btn -> onCell(page * PER_PAGE + cell))
                    .dimensions(x, y, CELL, CELL).build();
            b.setAlpha(0f);
            addDrawableChild(b);
        }

        int by = top + ROWS * CELL + 6;
        // แถว 1: เลื่อนหน้า / ล้าง / ขายตอนนี้ / ปิดเมนู (ไม่ทับกัน)
        addDrawableChild(ButtonWidget.builder(Text.literal("<"), b -> { if (page > 0) page--; })
                .dimensions(left, by, 30, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal(">"), b -> {
            if ((page + 1) * PER_PAGE < filtered.size()) page++; }).dimensions(left + 34, by, 30, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Clear"), b -> {
            SellConfig.I.selected.clear(); SellConfig.save(); }).dimensions(left + 68, by, 50, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Sell now"), b -> {
            client.setScreen(null); Seller.start(client); }).dimensions(left + 122, by, 56, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close())
                .dimensions(left + 182, by, gridW - 182, 20).build());

        // แถว 2: สวิตช์ loop / สวิตช์หลัก
        loopBtn = ButtonWidget.builder(loopLabel(), b -> {
            SellConfig.I.loop = !SellConfig.I.loop; SellConfig.save(); b.setMessage(loopLabel());
        }).dimensions(left, by + 24, 118, 20).build();
        powerBtn = ButtonWidget.builder(powerLabel(), b -> {
            SellConfig.I.enabled = !SellConfig.I.enabled; SellConfig.save(); b.setMessage(powerLabel());
        }).dimensions(left + 122, by + 24, gridW - 122, 20).build();
        addDrawableChild(loopBtn);
        addDrawableChild(powerBtn);

        // แถว 3: ความเร็วการย้ายของ
        addDrawableChild(new SpeedSlider(left, by + 48, gridW));
        refilter();
    }

    static Text loopLabel() { return Text.literal("Loop: " + (SellConfig.I.loop ? "ON" : "OFF")); }
    static Text powerLabel() { return Text.literal("Power: " + (SellConfig.I.enabled ? "ON" : "OFF")); }

    class SpeedSlider extends SliderWidget {
        SpeedSlider(int x, int y, int w) {
            super(x, y, w, 20, Text.empty(), (SellConfig.I.clickDelay - 1) / 9.0);
            updateMessage();
        }
        int delay() { return 1 + (int) Math.round(value * 9); }
        @Override protected void updateMessage() {
            setMessage(Text.literal("Move delay: " + delay() + " tick (ต่ำ = เร็ว)"));
        }
        @Override protected void applyValue() { SellConfig.I.clickDelay = delay(); }
    }

    @Override
    public void removed() { SellConfig.save(); }

    void onCell(int idx) {
        if (idx < 0 || idx >= filtered.size()) return;
        Item it = filtered.get(idx);
        String id = Registries.ITEM.getId(it).toString();
        if (SellConfig.I.selected.contains(id)) {
            SellConfig.I.selected.remove(id);
            SellConfig.save();
        } else if (SellConfig.isRare(id)) {
            client.setScreen(new ConfirmScreen(yes -> {
                if (yes) { SellConfig.I.selected.add(id); SellConfig.save(); }
                client.setScreen(this);
            }, Text.literal("ของหายาก!"),
               Text.literal("จะเพิ่ม " + it.getName().getString() + " เข้ารายการขายจริงหรือ?")));
        } else {
            SellConfig.I.selected.add(id);
            SellConfig.save();
        }
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx, mx, my, delta);
        ctx.drawTextWithShadow(textRenderer, "Selected: " + SellConfig.I.selected.size()
                + "   Page " + (page + 1) + "/" + (Math.max(0, (filtered.size() - 1) / PER_PAGE) + 1),
                left + 160, 21, 0xFFFFFFFF);

        Text tip = null;
        for (int i = 0; i < PER_PAGE; i++) {
            int idx = page * PER_PAGE + i;
            if (idx >= filtered.size()) break;
            Item it = filtered.get(idx);
            String id = Registries.ITEM.getId(it).toString();
            int x = left + (i % COLS) * CELL, y = top + (i / COLS) * CELL;

            boolean sel = SellConfig.I.selected.contains(id);
            boolean rare = SellConfig.isRare(id);
            ctx.fill(x, y, x + CELL - 1, y + CELL - 1, sel ? 0x6600FF66 : 0x33FFFFFF);
            if (rare) {
                int w = CELL - 1, c = 0xFFFF4444;
                ctx.fill(x, y, x + w, y + 1, c);
                ctx.fill(x, y + w - 1, x + w, y + w, c);
                ctx.fill(x, y, x + 1, y + w, c);
                ctx.fill(x + w - 1, y, x + w, y + w, c);
            }
            ctx.drawItem(new ItemStack(it), x + 1, y + 1);

            if (mx >= x && mx < x + CELL && my >= y && my < y + CELL)
                tip = Text.literal(it.getName().getString() + (rare ? " (RARE)" : "") + (sel ? " [x]" : ""));
        }
        if (tip != null) ctx.drawTooltip(textRenderer, tip, mx, my);
    }
}
