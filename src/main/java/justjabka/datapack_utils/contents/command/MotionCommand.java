package justjabka.datapack_utils.contents.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class MotionCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("motion")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.argument("target", EntityArgument.entity())
                        .then(Commands.argument("momentum", Vec3Argument.vec3(false))
                                .executes(context -> {
                                    Entity entity = EntityArgument.getEntity(context, "target");
                                    Vec3 momentum = getMomentum(context);
                                    return applyMomentum(context.getSource(), entity, momentum, true);
                                })
                                .then(Commands.argument("override", BoolArgumentType.bool())
                                        .executes(context -> {
                                            Entity entity = EntityArgument.getEntity(context, "target");
                                            Vec3 momentum = getMomentum(context);
                                            boolean override = BoolArgumentType.getBool(context, "override");

                                            return applyMomentum(context.getSource(), entity, momentum, override);
                                        })
                                )
                        )
                );
    }

    private static int applyMomentum(CommandSourceStack source, Entity entity, Vec3 momentum, boolean override) {
        if (override) {
            entity.setDeltaMovement(momentum);
        } else {
            entity.addDeltaMovement(momentum);
        }
        entity.hurtMarked = true;

        source.sendSuccess(() -> Component.literal("Successfully applied motion for %s".formatted(entity.getName().getString())), true);
        return Command.SINGLE_SUCCESS;
    }

    private static Vec3 getMomentum(CommandContext<CommandSourceStack> context) {
        Vec3 absolutePos = Vec3Argument.getVec3(context, "momentum");
        return absolutePos.subtract(context.getSource().getPosition());
    }
}
