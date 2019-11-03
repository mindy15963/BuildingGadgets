package com.direwolf20.buildinggadgets.common.building.view;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;

/**
 * Simple implementation of {@link BuildContext} providing a {@link Builder} for creation.
 */
@Immutable
public final class BuildContext {
    /**
     * @return A new {@link Builder}.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * @param context The context to copy. If null, then this Method acts as if calling {@link #builder()}.
     * @return A new {@link Builder} with all values copied from the specified {@link BuildContext}.
     */
    public static Builder builderOf(@Nullable BuildContext context) {
        Builder builder = builder();
        if (context == null)
            return builder;
        return builder
                .world(context.getWorld())
                .buildingPlayer(context.getBuildingPlayer())
                .usedStack(context.getUsedStack());
    }

    public static BuildContext copyOf(@Nullable BuildContext context) {
        return builderOf(context).build();
    }

    @Nonnull
    private final IWorld world;
    @Nullable
    private final PlayerEntity buildingPlayer;
    @Nonnull
    private final ItemStack stack;

    private BuildContext(@Nonnull IWorld world, @Nullable PlayerEntity buildingPlayer, @Nonnull ItemStack stack) {
        this.world = world;
        this.buildingPlayer = buildingPlayer;
        this.stack = stack;
    }

    /**
     * @return The {@link IWorld} of this {@code BuildContext}. Will not be null.
     */
    public IWorld getWorld() {
        return world;
    }

    /**
     * @return The {@link PlayerEntity} performing the build. May be null if unknown.
     */
    @Nullable
    public PlayerEntity getBuildingPlayer() {
        return buildingPlayer;
    }

    public ItemStack getUsedStack() {
        return stack;
    }

    /**
     * {@code SimpleBuilder} for creating new instances of {@link BuildContext}
     */
    public static final class Builder {
        @Nullable
        private IWorld world;
        @Nullable
        private PlayerEntity buildingPlayer;
        @Nonnull
        private ItemStack stack;

        private Builder() {
            this.world = null;
            this.buildingPlayer = null;
            this.stack = ItemStack.EMPTY;
        }

        /**
         * Sets the {@link IWorld} of the resulting {@link BuildContext}.
         * @param world The {@link IWorld} of the resulting {@link BuildContext}.
         * @return The {@code Builder} itself
         * @see BuildContext#getWorld()
         */
        public Builder world(@Nonnull IWorld world) {
            this.world = world;
            return this;
        }

        /**
         * Sets the {@link PlayerEntity} of the resulting {@link BuildContext}. Notice that this also set's the world
         * for the resulting {@code BuildContext} if the player is non-null and a world hasn't been set yet.
         * <p>
         * This defaults to null.
         * @param buildingPlayer The {@link PlayerEntity} of the resulting {@link BuildContext}.
         * @return The {@code Builder} itself
         * @see BuildContext#getBuildingPlayer()
         */
        public Builder buildingPlayer(@Nullable PlayerEntity buildingPlayer) {
            this.buildingPlayer = buildingPlayer;
            if (world == null && buildingPlayer != null)
                this.world = buildingPlayer.world;
            return this;
        }

        /**
         * Sets the {@link ItemStack} of the resulting {@link BuildContext}.
         * <p>
         * Defaults to {@link ItemStack#EMPTY}.
         *
         * @param stack The {@link ItemStack} of the resulting {@code BuildContext}
         * @return The {@code Builder} itself
         * @see BuildContext#getUsedStack()
         */
        public Builder usedStack(@Nonnull ItemStack stack) {
            this.stack = stack;
            return this;
        }

        /**
         * Creates a new {@link BuildContext} using the world previously set on this {@code Builder}.
         * @return A new {@link BuildContext} with the values specified in this {@code Builder}.
         * @see #build(IWorld)
         */
        public BuildContext build() {
            return build(null);
        }

        /**
         * Creates a new {@link BuildContext} using the specified world. If the given world is null, the world in this {@code Builder} will be used.
         * @param world The {@link IWorld} to use. If null this {@code SimpleBuilder}'s world will be used.
         * @return A new {@link BuildContext} with the values specified in this {@code SimpleBuilder}.
         * @throws NullPointerException if both the {@link IWorld} passed in and the {@link IWorld} of this {@code Builder} are null.
         */
        public BuildContext build(@Nullable IWorld world) {
            return new BuildContext(world != null ? world : Objects.requireNonNull(this.world), buildingPlayer, stack);
        }
    }
}
