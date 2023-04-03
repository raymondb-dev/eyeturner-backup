package raymondbdev.eyeturner.Model;

/**
 * Allows us to observe the state of the activity.
 */
public class FinishedState {
    private boolean finished = false;

    public boolean getFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
