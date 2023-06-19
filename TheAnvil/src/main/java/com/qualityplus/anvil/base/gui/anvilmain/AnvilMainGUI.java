package com.qualityplus.anvil.base.gui.anvilmain;

import com.qualityplus.anvil.api.box.Box;
import com.qualityplus.anvil.api.session.AnvilSession;
import com.qualityplus.anvil.api.session.AnvilSession.SessionResult;
import com.qualityplus.anvil.base.gui.AnvilGUI;
import com.qualityplus.anvil.base.gui.anvilmain.handler.ClickHandler;
import com.qualityplus.anvil.base.gui.anvilmain.handler.MainClickHandler;
import com.qualityplus.anvil.base.gui.anvilmain.handler.NormalClickHandler;
import com.qualityplus.anvil.base.gui.anvilmain.handler.ShiftClickHandler;
import com.qualityplus.anvil.util.AnvilFinderUtil;
import com.qualityplus.assistant.api.util.BukkitItemUtil;
import com.qualityplus.assistant.api.util.IPlaceholder;
import com.qualityplus.assistant.util.StringUtils;
import com.qualityplus.assistant.util.inventory.InventoryUtils;
import com.qualityplus.assistant.util.itemstack.ItemStackUtils;
import com.qualityplus.assistant.util.map.MapUtils;
import com.qualityplus.assistant.util.placeholder.Placeholder;
import com.qualityplus.assistant.util.placeholder.PlaceholderBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class AnvilMainGUI extends AnvilGUI {
    private @Getter final AnvilMainGUIConfig config;
    private @Setter boolean giveItem = true;
    private final ClickHandler handler;
    private final AnvilSession session;

    public AnvilMainGUI(Box box, AnvilSession session) {
        super(box.files().inventories().enchantMainGUI, box);

        this.handler = new MainClickHandler(new NormalClickHandler(box, this, session), new ShiftClickHandler(box, this, session));
        this.config = box.files().inventories().enchantMainGUI;
        this.session = session;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if(getTarget(event).equals(ClickTarget.INSIDE)){
            int slot = event.getSlot();


            if(isItem(slot, config.getCloseGUI())) {
                event.setCancelled(true);
                player.closeInventory();
            }else if(slot == config.getToUpgradeSlot() || slot == config.getToSacrificeSlot() || slot == config.getCombineFilledItem().slot || slot == config.getCombinedFilledItem().slot) {
                handler.handle(event);
            }else {
                event.setCancelled(true);

            }
        }else{

            handler.handleOutSide(event);

        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        InventoryUtils.fillInventory(inventory, config.getBackground());

        SessionResult answer = AnvilFinderUtil.getAnswer(session);

        //Izquierda
        if(answer.equals(SessionResult.BOTH_ITEMS_SET) || answer.equals(SessionResult.ONLY_ITEM_TO_UPGRADE))
            config.getToUpgradeFilledSlots().forEach(slot -> inventory.setItem(slot, ItemStackUtils.makeItem(config.getToUpgradeFilledItem())));

        //Derecha
        if(answer.equals(SessionResult.BOTH_ITEMS_SET) || answer.equals(SessionResult.ONLY_ITEM_TO_SACRIFICE))
            config.getToSacrificeFilledSlots().forEach(slot -> inventory.setItem(slot, ItemStackUtils.makeItem(config.getToSacrificeFilledItem())));

        //Items de abajo
        if(answer.equals(SessionResult.BOTH_ITEMS_SET)) {
            ItemStack newItem = AnvilFinderUtil.getFinalItem(session);
            //Final Item

            ItemStack newItemInInv = ItemStackUtils.makeItem(config.getCombinedFilledItem(), getPlaceholders(newItem), newItem, false, false);


            inventory.setItem(config.getCombinedFilledItem().slot, newItemInInv);

            config.getReadyToCombineSlots().forEach(slot -> inventory.setItem(slot, ItemStackUtils.makeItem(config.getReadyToCombineItem())));
            //Anvil
            setItem(config.getCombineFilledItem(), Arrays.asList(
                    new Placeholder("anvil_enchant_exp_cost", getParsed(box.files().messages().anvilPlaceholders.experienceCost)),
                    new Placeholder("anvil_enchant_money_cost", getParsed(box.files().messages().anvilPlaceholders.moneyCost))
            ));
        }

        if(!BukkitItemUtil.isNull(session.getResult()))
            inventory.setItem(config.getCombinedFilledItem().slot, ItemStackUtils.makeItem(config.getCombinedFilledItem(), getPlaceholders(session.getResult()), session.getResult()));


        if(answer.isError())
            setItem(config.getCombinedErrorItem(), Collections.singletonList(new Placeholder("anvil_error", getErrorPlaceholder(answer))));


        if(!BukkitItemUtil.isNull(session.getItemToSacrifice()))
            inventory.setItem(config.getToSacrificeSlot(), session.getItemToSacrifice());

        if(!BukkitItemUtil.isNull(session.getItemToUpgrade()))
            inventory.setItem(config.getToUpgradeSlot(), session.getItemToUpgrade());


        setItem(config.getCloseGUI());

        return inventory;
    }
    protected Map<Enchantment, Integer> getEnchantments(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();

        return meta instanceof EnchantmentStorageMeta ? new HashMap<>(((EnchantmentStorageMeta) meta).getStoredEnchants()): MapUtils.check(new HashMap<>(meta.getEnchants()));
    }
    private String getParsed(String msg){
        List<IPlaceholder> placeholders = PlaceholderBuilder.create(
                        new Placeholder("enchanting_enchant_exp_cost_amount", AnvilFinderUtil.getLevelsCost(session)),
                        new Placeholder("enchanting_enchant_money_cost_amount", AnvilFinderUtil.getMoneyCost(session))
        ).get();

        return StringUtils.processMulti(msg, placeholders);
    }

    private List<IPlaceholder> getPlaceholders(ItemStack itemStack){
        return Arrays.asList(
                new Placeholder("anvil_result_item_displayname", BukkitItemUtil.getName(itemStack)),
                new Placeholder("anvil_result_item_lore", BukkitItemUtil.getItemLore(itemStack))
        );
    }

    private List<String> getErrorPlaceholder(SessionResult answer){
        return answer.equals(SessionResult.ERROR_CANNOT_COMBINE_THOSE_ITEMS) ? box.files().messages().anvilPlaceholders.youCannotCombineThoseItems : box.files().messages().anvilPlaceholders.youCannotAddThatEnchantment;
    }

    @Override
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if(!giveItem) return;

        Optional.ofNullable(session.getItemToSacrifice()).ifPresent(itemStack -> player.getInventory().addItem(itemStack));
        Optional.ofNullable(session.getItemToUpgrade()).ifPresent(itemStack -> player.getInventory().addItem(itemStack));
        Optional.ofNullable(session.getResult()).ifPresent(itemStack -> player.getInventory().addItem(itemStack));

    }
}
