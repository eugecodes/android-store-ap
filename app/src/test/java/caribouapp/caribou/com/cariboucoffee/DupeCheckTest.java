package caribouapp.caribou.com.cariboucoffee;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger;
import caribouapp.caribou.com.cariboucoffee.api.AmsApi;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsResponsePreEnrollment;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsResponsePreEnrollmentResult;
import caribouapp.caribou.com.cariboucoffee.common.EnumPreEnrollmentResponse;
import caribouapp.caribou.com.cariboucoffee.messages.ErrorMessageMapper;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.DupeCheckContract;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.model.DupeCheckModel;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.presenter.DupeCheckPresenter;
import caribouapp.caribou.com.cariboucoffee.util.MockitoUtil;
import retrofit2.Call;
import retrofit2.Response;

public class DupeCheckTest {

    private static final String EMAIL = "test@gmail.com";
    private static final String PHONE_NUMBER = "38974383456";

    private AmsApi mAmsApi;
    private EventLogger mEventLogger;
    private ErrorMessageMapper mErrorMessageMapper;
    private Call<AmsResponsePreEnrollment> mPreEnrollmentCall;
    private DupeCheckPresenter mDupeCheckPresenter;
    private DupeCheckContract.View mView;
    private DupeCheckModel mDupeCheckModel;


    @Before
    public void setup() {
        mAmsApi = mock(AmsApi.class);
        mEventLogger = mock(EventLogger.class);
        mDupeCheckModel = new DupeCheckModel();
        mView = mock(DupeCheckContract.View.class);
        when(mView.isActive()).thenReturn(true);

        mErrorMessageMapper = mock(ErrorMessageMapper.class);
        when(mErrorMessageMapper.isSuccessful(any(Response.class))).thenReturn(true);

        mPreEnrollmentCall = mock(Call.class);
        when(mAmsApi.preEnrollment(any())).thenReturn(mPreEnrollmentCall);
        mDupeCheckPresenter = new DupeCheckPresenter(mView, mDupeCheckModel);
        mDupeCheckPresenter.setAmsApi(mAmsApi);
        mDupeCheckPresenter.setEventLogger(mEventLogger);
        mDupeCheckPresenter.setErrorMessageMapper(mErrorMessageMapper);
    }

    @Test
    public void testCaseNotEnrolled() {
        MockitoUtil.mockEnqueueWithObject(mPreEnrollmentCall, createPreEnrollmentResponse(EnumPreEnrollmentResponse.NOT_ENROLLED, true, null));

        mDupeCheckModel.setEmail(EMAIL);
        mDupeCheckModel.setTelephone(PHONE_NUMBER);
        mDupeCheckPresenter.dupeCheck();


        verify(mView,
                times(1))
                .goToPersonalInformation(false, EMAIL, PHONE_NUMBER, true);
    }

    @Test
    public void testCaseAutomaticPasswordReset() {
        MockitoUtil.mockEnqueueWithObject(mPreEnrollmentCall, createPreEnrollmentResponse(EnumPreEnrollmentResponse.AUTOMATIC_PASSWORD_RESET, true, null));

        mDupeCheckModel.setEmail(EMAIL);
        mDupeCheckModel.setTelephone(PHONE_NUMBER);
        mDupeCheckPresenter.dupeCheck();

        verify(mView,
                times(1))
                .showEmailAlreadyRegistered(R.string.you_are_already_registered_email, false);
    }

    @Test
    public void testCaseEnrolledInThisProgram() {
        MockitoUtil.mockEnqueueWithObject(mPreEnrollmentCall, createPreEnrollmentResponse(EnumPreEnrollmentResponse.ENROLLED_IN_THIS_PROGRAM, true, null));

        mDupeCheckModel.setEmail(EMAIL);
        mDupeCheckModel.setTelephone(PHONE_NUMBER);
        mDupeCheckPresenter.dupeCheck();

        verify(mView,
                times(1))
                .showEmailAlreadyRegistered(R.string.you_are_already_registered, false);
    }

    @Test
    public void testCaseEnrolledInDifferentProgram() {
        MockitoUtil.mockEnqueueWithObject(mPreEnrollmentCall,
                createPreEnrollmentResponse(EnumPreEnrollmentResponse.ENROLLED_IN_DIFFERENT_PROGRAM, true, null));

        mDupeCheckModel.setEmail(EMAIL);
        mDupeCheckModel.setTelephone(PHONE_NUMBER);
        mDupeCheckPresenter.dupeCheck();

        verify(mView,
                times(1))
                .showEmailAlreadyRegistered(R.string.you_are_already_registered_different_program, false);
    }

    @Test
    public void testCasePhoneRegisteredDifferentEmail() {
        MockitoUtil.mockEnqueueWithObject(mPreEnrollmentCall,
                createPreEnrollmentResponse(EnumPreEnrollmentResponse.PHONE_REGISTERED_DIFFERENT_EMAIL, true, EMAIL));

        mDupeCheckModel.setEmail(EMAIL);
        mDupeCheckModel.setTelephone(PHONE_NUMBER);
        mDupeCheckPresenter.dupeCheck();

        verify(mView,
                times(1))
                .showPhoneAlreadyRegistered(R.string.phone_number_already_registered_with_other_email, EMAIL);
    }

    private AmsResponsePreEnrollment createPreEnrollmentResponse(EnumPreEnrollmentResponse enumPreEnrollmentResponse, boolean birthdate, String maskedEmail) {
        AmsResponsePreEnrollment amsResponsePreEnrollment = new AmsResponsePreEnrollment();
        AmsResponsePreEnrollmentResult amsResponsePreEnrollmentResult = new AmsResponsePreEnrollmentResult();
        amsResponsePreEnrollmentResult.setStatus(enumPreEnrollmentResponse);
        amsResponsePreEnrollmentResult.setDateOfBirth(birthdate);
        amsResponsePreEnrollmentResult.setMaskedEmailForPhoneNumber(maskedEmail);
        amsResponsePreEnrollment.setResult(amsResponsePreEnrollmentResult);
        return amsResponsePreEnrollment;
    }
}
