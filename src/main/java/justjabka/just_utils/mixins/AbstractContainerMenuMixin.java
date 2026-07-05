package justjabka.just_utils.mixins;

import justjabka.just_utils.contents.attachment.OpenedContainer;
import justjabka.just_utils.types.VirtualMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin implements VirtualMenu {
    @Unique
    private boolean just_utils$isVirtualMenu = false;

    @Override
    public void just_utils$setVirtual(boolean isVirtual) {
        this.just_utils$isVirtualMenu = isVirtual;
    }

    @Override
    public boolean just_utils$isVirtual() {
        return this.just_utils$isVirtualMenu;
    }

    @Inject(method = "clicked", at = @At("TAIL"))
    private void onMenuUpdate(int slotIndex, int buttonNum, ContainerInput containerInput, Player player, CallbackInfo ci) {
        AbstractContainerMenu menu = (AbstractContainerMenu) (Object) this;
        if (!((VirtualMenu) menu).just_utils$isVirtual()) return;

        if (!(player instanceof ServerPlayer serverPlayer)) return;

        List<ItemStackWithSlot> currentItems = new ArrayList<>();

        for (int i = 0; i < menu.slots.size(); i++) {
            Slot slot = menu.getSlot(i);

            if (slot.container == player.getInventory()) continue;

            if (!slot.hasItem()) continue;
            currentItems.add(new ItemStackWithSlot(i, slot.getItem().copy()));
        }

        OpenedContainer.get(serverPlayer).set(currentItems);
    }
}
