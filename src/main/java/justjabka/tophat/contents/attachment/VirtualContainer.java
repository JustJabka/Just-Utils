package justjabka.tophat.contents.attachment;

import justjabka.tophat.Tophat;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class VirtualContainer {
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemStackWithSlot> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ItemStackWithSlot::slot,
            ItemStack.STREAM_CODEC, ItemStackWithSlot::stack,
            ItemStackWithSlot::new
    );

    private static final AttachmentType<List<ItemStackWithSlot>> ITEMS = AttachmentRegistry.create(
            Tophat.id("virtual_items"),
            builder -> builder
                    .initializer(ArrayList::new)
                    .persistent(ItemStackWithSlot.CODEC.listOf())
                    .syncWith(STREAM_CODEC.apply(ByteBufCodecs.list()), AttachmentSyncPredicate.all())
    );


    public static VirtualContainerData get(AttachmentTarget target) {
        return new VirtualContainerData(target);
    }

    public record VirtualContainerData(AttachmentTarget target) {
        public List<ItemStackWithSlot> get() {
            return this.target.getAttachedOrCreate(ITEMS);
        }

        public void set(List<ItemStackWithSlot> list) {
            this.target.setAttached(ITEMS, new ArrayList<>(list));
        }

        public void clear() {
            VirtualContainer.get(this.target).set(List.of());
        }
    }
}
