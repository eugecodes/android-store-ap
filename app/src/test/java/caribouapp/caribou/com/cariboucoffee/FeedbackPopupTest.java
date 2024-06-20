package caribouapp.caribou.com.cariboucoffee;

import org.junit.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import caribouapp.caribou.com.cariboucoffee.common.ReviewStatusEnum;
import caribouapp.caribou.com.cariboucoffee.util.FeedbackUtils;

/**
 * Created by jmsmuy on 3/1/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class FeedbackPopupTest {

    @Test
    public void testFirstAppearance() {
        String lastReviewedVersion = "0";
        String previousAndroidVersion = "2.0.0";
        String currentAndroidVersion = "2.0.0";
        String installedVersion = "2.0.0";
        int launchesSinceLastReview = 5;
        ReviewStatusEnum reviewResult = ReviewStatusEnum.NOT_REVIEWED;
        int appDormancy = 5;
        int appPrompt = 5;

        Assert.assertTrue(FeedbackUtils.wrappedShouldShowPopup(lastReviewedVersion, previousAndroidVersion,
                currentAndroidVersion, launchesSinceLastReview, reviewResult, appDormancy, appPrompt, installedVersion));
    }

    @Test
    public void testAppearanceAfterDontAskAgain() {
        String lastReviewedVersion = "0";
        String previousAndroidVersion = "2.0.0";
        String currentAndroidVersion = "2.0.0";
        String installedVersion = "2.0.0";
        int launchesSinceLastReview = 5;
        ReviewStatusEnum reviewResult = ReviewStatusEnum.DONT_ASK_ME_AGAIN;
        int appDormancy = 5;
        int appPrompt = 5;

        Assert.assertFalse(FeedbackUtils.wrappedShouldShowPopup(lastReviewedVersion, previousAndroidVersion,
                currentAndroidVersion, launchesSinceLastReview, reviewResult, appDormancy, appPrompt, installedVersion));
    }

    @Test
    public void testAppearanceAfterDontAskAgainButNewVersion() {
        String lastReviewedVersion = "0";
        String previousAndroidVersion = "2.0.0";
        String currentAndroidVersion = "3.0.0";
        String installedVersion = "3.0.0";
        int launchesSinceLastReview = 5;
        ReviewStatusEnum reviewResult = ReviewStatusEnum.DONT_ASK_ME_AGAIN;
        int appDormancy = 5;
        int appPrompt = 5;

        Assert.assertTrue(FeedbackUtils.wrappedShouldShowPopup(lastReviewedVersion, previousAndroidVersion,
                currentAndroidVersion, launchesSinceLastReview, reviewResult, appDormancy, appPrompt, installedVersion));
    }

    @Test
    public void testAppearanceAfterReview() {
        String lastReviewedVersion = "2.0.0";
        String previousAndroidVersion = "2.0.0";
        String currentAndroidVersion = "2.0.0";
        String installedVersion = "2.0.0";
        int launchesSinceLastReview = 5;
        ReviewStatusEnum reviewResult = ReviewStatusEnum.REVIEWED;
        int appDormancy = 5;
        int appPrompt = 5;

        Assert.assertFalse(FeedbackUtils.wrappedShouldShowPopup(lastReviewedVersion, previousAndroidVersion,
                currentAndroidVersion, launchesSinceLastReview, reviewResult, appDormancy, appPrompt, installedVersion));
    }

    @Test
    public void testAppearanceAfterDormancy() {
        String lastReviewedVersion = "0";
        String previousAndroidVersion = "2.0.0";
        String currentAndroidVersion = "2.0.0";
        String installedVersion = "2.0.0";
        int launchesSinceLastReview = 8;
        ReviewStatusEnum reviewResult = ReviewStatusEnum.ASK_ME_LATER;
        int appDormancy = 8;
        int appPrompt = 5;

        Assert.assertTrue(FeedbackUtils.wrappedShouldShowPopup(lastReviewedVersion, previousAndroidVersion,
                currentAndroidVersion, launchesSinceLastReview, reviewResult, appDormancy, appPrompt, installedVersion));
    }

    @Test
    public void testAppearanceNewOSVersionButOldVersionInstalled() {
        String lastReviewedVersion = "2.0.0";
        String previousAndroidVersion = "2.0.0";
        String currentAndroidVersion = "3.0.0";
        String installedVersion = "2.0.0";
        int launchesSinceLastReview = 5;
        ReviewStatusEnum reviewResult = ReviewStatusEnum.REVIEWED;
        int appDormancy = 5;
        int appPrompt = 5;

        Assert.assertFalse(FeedbackUtils.wrappedShouldShowPopup(lastReviewedVersion, previousAndroidVersion,
                currentAndroidVersion, launchesSinceLastReview, reviewResult, appDormancy, appPrompt, installedVersion));
    }
}
