package com.direwolf20.buildinggadgets.common.items.gadgets.renderers;

import com.direwolf20.buildinggadgets.common.items.gadgets.AbstractGadget;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetDestruction;
import com.direwolf20.buildinggadgets.common.registry.OurBlocks;
import com.direwolf20.buildinggadgets.common.util.helpers.VectorHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

public class DestructionRender extends BaseRenderer {
//    private static Cache<Triple<UniqueItemStack, BlockPos, Integer>, Integer> cacheDestructionOverlay = CacheBuilder.newBuilder().maximumSize(1).
//            expireAfterWrite(1, TimeUnit.SECONDS).removalListener(removal -> GLAllocation.deleteDisplayLists((int) removal.getValue())).build();

    @Override
    public void render(RenderWorldLastEvent evt, PlayerEntity player, ItemStack heldItem) {
        if (!GadgetDestruction.getOverlay(heldItem))
            return;

        BlockRayTraceResult lookingAt = VectorHelper.getLookingAt(player, heldItem);
        World world = player.world;
        BlockPos anchor = ((AbstractGadget) heldItem.getItem()).getAnchor(heldItem);
        if ((lookingAt == null || (world.getBlockState(VectorHelper.getLookingAt(player, heldItem).getPos()) == AIR)) && anchor == null)
            return;

        BlockPos startBlock = (anchor == null) ? lookingAt.getPos() : anchor;
        Direction facing = (GadgetDestruction.getAnchorSide(heldItem) == null) ? lookingAt.getFace() : GadgetDestruction.getAnchorSide(heldItem);

        if (world.getBlockState(startBlock) == OurBlocks.effectBlock.getDefaultState())
            return;

        Vec3d playerPos = getMc().gameRenderer.getActiveRenderInfo().getProjectedView();

        MatrixStack stack = evt.getMatrixStack();
        stack.push();
        stack.translate(-playerPos.getX(), -playerPos.getY(), -playerPos.getZ());

        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(stack.getLast().getMatrix());
        stack.pop();

//        try {
//            RenderSystem.callList(cacheDestructionOverlay.get(new ImmutableTriple<>(new UniqueItemStack(heldItem), startBlock, facing.ordinal()), () -> {
//                int displayList = GLAllocation.generateDisplayLists(1);
//                RenderSystem.newList(displayList, GL11.GL_COMPILE);
                this.renderOverlay(player, world, startBlock, facing, heldItem);
//                RenderSystem.endList();
//                return displayList;
//            }));
//        } catch (ExecutionException e) {
//            BuildingGadgets.LOG.error("Error encountered while rendering destruction gadget overlay", e);
//        }

        RenderSystem.enableLighting();
        RenderSystem.popMatrix();
    }

    private void renderOverlay(PlayerEntity player, World world, BlockPos startBlock, Direction facing, ItemStack heldItem) {
        //Save the current position that is being rendered (I think)
        RenderSystem.pushMatrix();
        //Enable Blending (So we can have transparent effect)
        RenderSystem.enableBlend();
        //This blend function allows you to use a constant alpha, which is defined later
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        Tessellator t = Tessellator.getInstance();
        BufferBuilder bufferBuilder = t.getBuffer();
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        RenderSystem.alphaFunc(GL11.GL_GREATER, 0.0001F);
        RenderSystem.disableTexture();
        RenderSystem.depthMask(false);
        GL14.glBlendColor(1F, 1F, 1F, 0.3f); //Set the alpha of the blocks we are rendering
        for (BlockPos coordinate : GadgetDestruction.getClearingPositionsForRendering(world, startBlock, facing, player, heldItem)) {
            //ToDo re-implement
            //renderMissingBlock(bufferBuilder, coordinate);
        }
        Tessellator.getInstance().draw();
        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        GL14.glBlendColor(1F, 1F, 1F, 1f); //Set the alpha of the blocks we are rendering
        //Set blending back to the default mode
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        ForgeHooksClient.setRenderLayer(MinecraftForgeClient.getRenderLayer());
        //Disable blend
        RenderSystem.disableBlend();
        //Pop from the original push in this method
        RenderSystem.popMatrix();
    }

}