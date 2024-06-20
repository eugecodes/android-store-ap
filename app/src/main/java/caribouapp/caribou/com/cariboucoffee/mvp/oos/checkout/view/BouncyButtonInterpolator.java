package caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.view;

import android.view.animation.Interpolator;

/**
 * https://github.com/MasayukiSuda/EasingInterpolator BOUNCE_OUT animation
 */
public class BouncyButtonInterpolator implements Interpolator {

    private static final double FIRST_STEP_TOP_BOUNDARY = 1 / 2.75;
    private static final double SECOND_STEP_TOP_BOUNDARY = 2 / 2.75;
    private static final double THIRD_STEP_TOP_BOUNDARY = 2.5 / 2.75;

    private static final double FIRST_STEP_BOTTOM_BOUNDARY = 1.5 / 2.75;
    private static final double SECOND_STEP_BOTTOM_BOUNDARY = 2.25 / 2.75;
    private static final double THIRD_STEP_BOTTOM_BOUNDARY = 2.625 / 2.75;

    private static final double ANIMATION_FIRST_STEP_INDEPENDENT_COMPONENT = 0.75;
    private static final double ANIMATION_SECOND_STEP_INDEPENDENT_COMPONENT = 0.9375;
    private static final double ANIMATION_THIRD_STEP_INDEPENDENT_COMPONENT = 0.984375;

    private static final double ANIMATION_RATIO = 7.5625;


    @Override
    public float getInterpolation(float elapsedTimeRate) {
        if (elapsedTimeRate < FIRST_STEP_TOP_BOUNDARY) {
            return (float) (ANIMATION_RATIO * elapsedTimeRate * elapsedTimeRate);
        } else if (elapsedTimeRate < SECOND_STEP_TOP_BOUNDARY) {
            elapsedTimeRate -= FIRST_STEP_BOTTOM_BOUNDARY;
            return (float) (ANIMATION_RATIO * elapsedTimeRate * elapsedTimeRate + ANIMATION_FIRST_STEP_INDEPENDENT_COMPONENT);
        } else if (elapsedTimeRate < THIRD_STEP_TOP_BOUNDARY) {
            elapsedTimeRate -= SECOND_STEP_BOTTOM_BOUNDARY;
            return (float) (ANIMATION_RATIO * elapsedTimeRate * elapsedTimeRate + ANIMATION_SECOND_STEP_INDEPENDENT_COMPONENT);
        } else {
            elapsedTimeRate -= THIRD_STEP_BOTTOM_BOUNDARY;
            return (float) (ANIMATION_RATIO * elapsedTimeRate * elapsedTimeRate + ANIMATION_THIRD_STEP_INDEPENDENT_COMPONENT);
        }
    }
}
