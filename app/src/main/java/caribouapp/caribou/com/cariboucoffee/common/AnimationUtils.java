package caribouapp.caribou.com.cariboucoffee.common;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.view.BouncyButtonInterpolator;

public final class AnimationUtils {

    private AnimationUtils() {
    }

    public static void bounceView(View view) {
        Context context = view.getContext();

        if (view.getTag(R.id.bounceAnimator) != null) {
            return;
        }

        ObjectAnimator animator = ObjectAnimator.ofFloat(
                view,
                "translationY",
                0, context.getResources().getInteger(R.integer.bounce_animation_translation), 0
        );
        animator.setInterpolator(new BouncyButtonInterpolator());
        animator.setStartDelay(context.getResources().getInteger(R.integer.bounce_animation_delay_time));
        animator.setDuration(context.getResources().getInteger(R.integer.bounce_animation_time));

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setTag(R.id.bounceAnimator, null);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                view.setTag(R.id.bounceAnimator, null);
            }
        });
        view.setTag(R.id.bounceAnimator, animator);
        animator.start();
    }
}
