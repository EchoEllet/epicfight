package yesman.epicfight.client.input;

import javax.annotation.Nullable;

public interface IEpicFightController {
    boolean inControllerMode();

    boolean isActionJustPressed(EpicFightInputAction action);

    boolean isActionTriggered(EpicFightInputAction action);

    @Nullable
    InputState getInputState();
}
