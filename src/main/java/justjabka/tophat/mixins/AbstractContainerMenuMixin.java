package justjabka.tophat.mixins;

import justjabka.tophat.types.VirtualMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin implements VirtualMenu {
    @Unique
    private boolean tophat$isVirtualMenu = false;

    @Override
    public void tophat$setVirtual(boolean isVirtual) {
        this.tophat$isVirtualMenu = isVirtual;
    }

    @Override
    public boolean tophat$isVirtual() {
        return this.tophat$isVirtualMenu;
    }
}
