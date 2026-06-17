package net.dragonmounts.compat;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.cap.BaubleItem;
import baubles.api.cap.BaublesCapabilities;
import net.dragonmounts.item.DragonAmuletItem;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public abstract class BaublesCompat {
    public static void load() {
        DragonAmuletItem.registerCapability(
                BaublesCapabilities.CAPABILITY_ITEM_BAUBLE,
                hackBaublesEX(new BaubleItem(BaubleType.AMULET))
        );
    }

    /**
     * There are 2 obvious ways to hack BaublesEX by reflection.
     * However, the one calling `ItemData::registerBauble` involves too many classes and methods to be reflected.
     * As a contrast, following implementation is fragile in some degree but only involve one class and one field.
     */
    static IBauble hackBaublesEX(IBauble impl) {
        try {
            Class<?> clazz = Class.forName("baubles.api.BaublesWrapper");
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true); // just in case
            Field bauble = clazz.getDeclaredField("bauble");
            bauble.setAccessible(true);

            Object wrapper = constructor.newInstance();
            bauble.set(wrapper, impl);
            return (IBauble) wrapper;
        } catch (Exception ignored) {}
        return impl;
    }

    private BaublesCompat() {}
}
