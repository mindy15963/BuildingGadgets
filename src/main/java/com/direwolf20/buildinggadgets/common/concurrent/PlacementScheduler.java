package com.direwolf20.buildinggadgets.common.concurrent;

import com.direwolf20.buildinggadgets.common.blocks.EffectBlock;
import com.direwolf20.buildinggadgets.common.blocks.EffectBlock.Mode;
import com.direwolf20.buildinggadgets.common.building.PlacementTarget;
import com.direwolf20.buildinggadgets.common.building.placement.PlacementChecker;
import com.direwolf20.buildinggadgets.common.building.placement.PlacementChecker.CheckResult;
import com.direwolf20.buildinggadgets.common.building.view.BuildContext;
import com.direwolf20.buildinggadgets.common.building.view.IBuildView;
import com.direwolf20.buildinggadgets.common.save.Undo;
import com.direwolf20.buildinggadgets.common.save.Undo.Builder;
import com.google.common.base.Preconditions;

import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;

public final class PlacementScheduler extends SteppedScheduler {
    public static PlacementScheduler schedulePlacement(IBuildView view, BuildContext context, PlacementChecker checker, int steps) {
        Preconditions.checkArgument(steps > 0);
        PlacementScheduler res = new PlacementScheduler(
                Objects.requireNonNull(view),
                Objects.requireNonNull(context),
                Objects.requireNonNull(checker),
                steps);
        ServerTickingScheduler.runTicked(res);
        return res;
    }

    private final BuildContext context;
    private final Spliterator<PlacementTarget> spliterator;
    private final PlacementChecker checker;
    private boolean lastWasSuccess;
    private Consumer<PlacementScheduler> finisher;
    private Undo.Builder undoBuilder;

    private PlacementScheduler(IBuildView view, BuildContext context, PlacementChecker checker, int steps) {
        super(steps);
        this.checker = checker;
        this.context = context;
        this.spliterator = view.spliterator();
        this.undoBuilder = Undo.builder();
        this.finisher = p -> {};
    }

    @Override
    protected void onFinish() {
        finisher.accept(this);
    }

    @Override
    protected StepResult advance() {
        if (! spliterator.tryAdvance(this::checkTarget))
            return StepResult.END;
        return lastWasSuccess ? StepResult.SUCCESS : StepResult.FAILURE;
    }

    public Builder getUndoBuilder() {
        return undoBuilder;
    }

    public PlacementScheduler withFinisher(Consumer<PlacementScheduler> runnable) {
        this.finisher = Objects.requireNonNull(runnable);
        return this;
    }

    private void checkTarget(PlacementTarget target) {
        CheckResult res = checker.checkPositionWithResult(context, target, false);
        lastWasSuccess = res.isSuccess();
        if (lastWasSuccess) {
            undoBuilder.record(context.getWorld(), target.getPos(), target.getData(), res.getMatch().getChosenOption(), res.getInsertedItems());
            EffectBlock.spawnEffectBlock(context, target, Mode.PLACE, res.isUsingPaste());
        }
    }
}
