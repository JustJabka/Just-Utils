package justjabka.tophat.mixins;

import justjabka.tophat.contents.attachment.VirtualContainer;
import justjabka.tophat.types.VirtualMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void onDataLoad(ValueInput input, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        AbstractContainerMenu menu = player.containerMenu;

        if (menu == player.inventoryMenu) return;
        if (!(menu instanceof VirtualMenu vMenu && vMenu.tophat$isVirtual())) return;
        syncToMenu(player, menu);
    }

    @Inject(method = "doCloseContainer", at = @At(value = "TAIL"))
    private void doCloseContainer(CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        VirtualContainer.get(player).clear();
    }

    @Unique
    private void syncToMenu(ServerPlayer player, AbstractContainerMenu menu) {
        VirtualContainer.VirtualContainerData containerData = VirtualContainer.get(player);
        List<ItemStackWithSlot> attachedItems = containerData.get();

        for (int i = 0; i < menu.slots.size(); i++) {
            Slot slot = menu.getSlot(i);
            if (slot.container == player.getInventory()) continue;
            slot.set(ItemStack.EMPTY);
        }

        for (ItemStackWithSlot entry : attachedItems) {
            int slotId = entry.slot();
            if (slotId >= 0 && slotId < menu.slots.size()) {
                Slot slot = menu.getSlot(slotId);
                if (slot.container == player.getInventory()) continue;
                slot.set(entry.stack().copy());
            }
        }

        menu.broadcastChanges();
    }
}
