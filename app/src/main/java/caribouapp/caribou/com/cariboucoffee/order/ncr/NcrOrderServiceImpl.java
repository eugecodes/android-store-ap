package caribouapp.caribou.com.cariboucoffee.order.ncr;

import androidx.collection.ArraySet;
import androidx.core.util.Pair;
import androidx.databinding.library.baseAdapters.BR;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.analytics.Tracer;
import caribouapp.caribou.com.cariboucoffee.analytics.TracerFactory;
import caribouapp.caribou.com.cariboucoffee.api.NcrWrapperApi;
import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsReward;
import caribouapp.caribou.com.cariboucoffee.api.model.content.ncr.NcrLinkGroup;
import caribouapp.caribou.com.cariboucoffee.api.model.content.ncr.NcrLinkItem;
import caribouapp.caribou.com.cariboucoffee.api.model.content.ncr.NcrSaleItem;
import caribouapp.caribou.com.cariboucoffee.api.model.order.NcrCurbsideIamHere;
import caribouapp.caribou.com.cariboucoffee.api.model.order.OmsMobileEligibleReward;
import caribouapp.caribou.com.cariboucoffee.api.model.order.OmsPOSAgentStatus;
import caribouapp.caribou.com.cariboucoffee.api.model.order.ServerAppliedReward;
import caribouapp.caribou.com.cariboucoffee.api.model.order.ncr.NcrOrderLine;
import caribouapp.caribou.com.cariboucoffee.api.model.order.ncr.NcrOrderStatus;
import caribouapp.caribou.com.cariboucoffee.api.model.order.ncr.NcrOrderWrappedData;
import caribouapp.caribou.com.cariboucoffee.api.model.order.ncr.NcrReorderErrors;
import caribouapp.caribou.com.cariboucoffee.api.model.order.ncr.NcrRewardError;
import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextPickupType;
import caribouapp.caribou.com.cariboucoffee.common.BaseResultCallback;
import caribouapp.caribou.com.cariboucoffee.common.Clock;
import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;
import caribouapp.caribou.com.cariboucoffee.common.RewardsData;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseRetrofitCallback;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.ResultCallbackWrapper;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.RetrofitCallbackWrapper;
import caribouapp.caribou.com.cariboucoffee.fiserv.model.MoveLoyaltyRequest;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.CurbsideOrderData;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemModifier;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemOption;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ModifierGroup;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ncr.NcrOrderItem;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.SizeEnum;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.service.MenuData;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.service.NcrMenuDataServiceImpl;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.PickUpTimeModel;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.RewardBannerModel;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.picklocation.model.EnumPOSAgentStatus;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.model.RecentOrderItem;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.model.RecentOrderModel;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.model.RecentOrderStore;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.model.ncr.NcrRecentOrderModel;
import caribouapp.caribou.com.cariboucoffee.order.BaseOrderServiceImpl;
import caribouapp.caribou.com.cariboucoffee.order.Order;
import caribouapp.caribou.com.cariboucoffee.order.OrderFailedException;
import caribouapp.caribou.com.cariboucoffee.order.ProductCustomizationData;
import caribouapp.caribou.com.cariboucoffee.util.DateUtil;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class NcrOrderServiceImpl extends BaseOrderServiceImpl<NcrOrderItem, NcrOrder> {

    private static final String TAG = NcrOrderServiceImpl.class.getSimpleName();
    private final NcrMenuDataServiceImpl mNcrMenuDataService;
    private final NcrWrapperApi mNcrOrderApi;
    private final TracerFactory mTracerFactory;
    private final UserServices mUserServices;
    private final Executor mOrderExecutor = Executors.newSingleThreadExecutor();

    public NcrOrderServiceImpl(Clock clock, UserServices userServices,
                               NcrMenuDataServiceImpl ncrMenuDataService,
                               NcrWrapperApi ncrOrderApi, SettingsServices settingsServices,
                               TracerFactory tracerFactory) {
        super(clock, ncrMenuDataService, settingsServices);
        mNcrMenuDataService = ncrMenuDataService;
        mNcrOrderApi = ncrOrderApi;
        mTracerFactory = tracerFactory;
        mUserServices = userServices;
    }

    @Override
    public void createOrderWithLocation(StoreLocation storeLocation, ResultCallback<Order> callback) {
        if (getOrder() != null) {
            discard(new BaseResultCallback<Order>() {
                @Override
                public void onSuccess(Order data) {
                    startNewOrder(storeLocation, callback);
                }
            });
        } else {
            startNewOrder(storeLocation, callback);
        }
    }

    private void startNewOrder(StoreLocation storeLocation, ResultCallback<Order> callback) {
        try {
            mNcrMenuDataService.clearCache();
            NcrOrder ncrOrder = new NcrOrder(storeLocation);
            setOrder(ncrOrder);
            updateLastActivity();
            callback.onSuccess(ncrOrder);
        } catch (RuntimeException e) {
            callback.onError(e);
        }
    }

    @Override
    public boolean isPickupTimeSupported() {
        return true;
    }

    @Override
    public void getRecentOrder(int maxDaysAgo, ResultCallback<List<RecentOrderModel>> callback) {
        mNcrOrderApi.getRecentOrders(maxDaysAgo).enqueue(new RetrofitCallbackWrapper<List<NcrOrderWrappedData>, List<RecentOrderModel>>(callback) {
            @Override
            protected void onSuccess(Response<List<NcrOrderWrappedData>> response) {
                List<RecentOrderModel> recentOrderModelList = new ArrayList<>();
                for (NcrOrderWrappedData ncrOrderWrappedData : Objects.requireNonNull(response.body())) {
                    recentOrderModelList.add(generateFromOrder(ncrOrderWrappedData));
                }
                callback.onSuccess(recentOrderModelList);
            }
        });
    }

    @Override
    public void loadEligibleRewards(RewardsData rewardsData, ResultCallback<List<OmsMobileEligibleReward>> callback) {
        List<OmsMobileEligibleReward> omsMobileEligibleRewards = new ArrayList<>();
        for (CmsReward cmsReward : rewardsData.getRewardsContent().values()) {
            if (cmsReward.getRuleId() != null) {
                OmsMobileEligibleReward omsMobileEligibleReward = new OmsMobileEligibleReward();
                omsMobileEligibleReward.setWalletId(cmsReward.getRewardId() + "");
                omsMobileEligibleRewards.add(omsMobileEligibleReward);
            }
        }
        callback.onSuccess(omsMobileEligibleRewards);
    }

    @Override
    public boolean shouldDisplayCurbsideIamHere() {
        CurbsideOrderData curbsideOrderData = mUserServices.getCurbsideOrderData();
        if (curbsideOrderData == null) {
            return false;
        }

        final DateTime pickupTime = curbsideOrderData.getPickupTime();
        return shouldDisplayCurbsideIamHere(pickupTime);
    }

    @Override
    public boolean shouldDisplayCurbsideSuccessOrError() {
        DateTime pickupTime = mUserServices.getCurbsideLatestPickupTime();
        if (pickupTime == null) {
            return false;
        }
        return shouldDisplayCurbsideIamHere(pickupTime);
    }

    private boolean shouldDisplayCurbsideIamHere(DateTime pickupTime) {
        final DateTime start = pickupTime.minusMinutes(getSettingsServices().getCurbsideImHereMinutesToStart());
        final DateTime end = pickupTime.plusMinutes(getSettingsServices().getCurbsideImHereMinutesToEnd());
        final DateTime now = getClock().getCurrentDateTime();
        return now.compareTo(start) >= 0 && now.compareTo(end) <= 0;
    }

    @Override
    public void sendCurbsideIamHereSignal(ResultCallback<NcrCurbsideIamHere> resultCallback) {
        CurbsideOrderData curbsideOrderData = mUserServices.getCurbsideOrderData();

        NcrCurbsideIamHere ncrCurbsideIamHere = new NcrCurbsideIamHere();
        ncrCurbsideIamHere.setIamHereTime(DateUtil.formatLocalTimezone(getClock().getCurrentDateTime()));

        mNcrOrderApi.sendCurbsideIamHere(curbsideOrderData.getOrderId(), ncrCurbsideIamHere)
                .enqueue(new RetrofitCallbackWrapper<ResponseBody, NcrCurbsideIamHere>(resultCallback) {
                    @Override
                    protected void onSuccess(Response<ResponseBody> response) {
                        eraseCurbsideData();
                        mUserServices.saveCurbsideLocationPhone(null);
                        resultCallback.onSuccess(ncrCurbsideIamHere);
                    }

                    @Override
                    protected void onError(Throwable throwable) {
                        eraseCurbsideData();
                        super.onError(throwable);
                    }

                    @Override
                    protected void onFail(Response<ResponseBody> response) {
                        eraseCurbsideData();
                        super.onFail(response);
                    }

                    @Override
                    protected void onNetworkFail(IOException throwable) {
                        eraseCurbsideData();
                        super.onNetworkFail(throwable);
                    }
                });
    }

    @Override
    public void eraseCurbsideData() {
        mUserServices.saveCurbsideOrderData(null);
        mUserServices.saveCurbsideLocationMessage(null);
    }

    @Override
    public void waitForCurbsideFinished(Order order, ResultCallback<Order> resultCallback) {
        mOrderExecutor.execute(() -> {
            boolean keepWaiting = true;
            NcrOrderWrappedData orderServerData = null;
            int attempts = 0;

            Set<NcrOrderStatus> successfulFinishStatus = new ArraySet<>();
            successfulFinishStatus.add(NcrOrderStatus.Finished);
            Set<NcrOrderStatus> stopWaitStatusSet = new HashSet<>(successfulFinishStatus);
            stopWaitStatusSet.add(NcrOrderStatus.Failed);

            while (keepWaiting) {
                try {
                    Thread.sleep(1000 * getSettingsServices().getCurbsideCheckFinishedDelay());
                } catch (InterruptedException e) {
                    return;
                }

                try {
                    Response<NcrOrderWrappedData> response = mNcrOrderApi.getOrder(((NcrOrder) order).getNcrOrderId()).execute();
                    if (response.isSuccessful()) {
                        orderServerData = response.body();
                        keepWaiting = orderServerData == null
                                || orderServerData.getNcrOrderStatus() == null
                                || !stopWaitStatusSet.contains(orderServerData.getNcrOrderStatus());
                        if (orderServerData != null && orderServerData.getNcrOrderStatus() != null) {
                            mUserServices.updateCurbsideDataStatus(orderServerData.getNcrOrderStatus());
                        }
                    } else {
                        if (orderServerData != null && orderServerData.getNcrOrderStatus() != null) {
                            Log.e(TAG, new LogErrorException("Order server error for check status request."
                                    + " responseCode:" + response.code()
                                    + " responseMessage: " + response.message()
                                    + printOrderData((NcrOrder) order, orderServerData != null ? orderServerData.getNcrOrderStatus() : null)));
                        }
                        resultCallback.onFail(response.code(), response.message());
                        return;
                    }
                } catch (Exception e) {
                    if (e instanceof SocketTimeoutException) {
                        Log.e(TAG, new LogErrorException("Order timeout for check status request."
                                + printOrderData((NcrOrder) order, orderServerData != null ? orderServerData.getNcrOrderStatus() : null)));
                    } else if (e instanceof InterruptedException) {
                        Log.e(TAG, new LogErrorException("Order exception for check status request."
                                + " message:" + e.getMessage()));
                    } else {
                        Log.e(TAG, new LogErrorException("Order exception for check status request."
                                + " message:" + e.getMessage()
                                + printOrderData((NcrOrder) order, orderServerData != null ? orderServerData.getNcrOrderStatus() : null)));
                    }
                    resultCallback.onError(e);
                    return;
                }
                attempts++;
                if (attempts > getSettingsServices().getCurbsideCheckFinishedAttempts()) {
                    Log.e(TAG, new LogErrorException("Order timeout."
                            + printOrderData((NcrOrder) order, orderServerData != null ? orderServerData.getNcrOrderStatus() : null)));
                    resultCallback.onError(new SocketTimeoutException("Read timeout"));
                    return;
                }
            }

            if (successfulFinishStatus.contains(orderServerData.getNcrOrderStatus())) {
                // We have a final status on the order data
                resultCallback.onSuccess(order);
            } else {
                // Order failed
                Log.e(TAG, new LogErrorException("Order failed."
                        + printOrderData((NcrOrder) order, orderServerData != null ? orderServerData.getNcrOrderStatus() : null)));
                resultCallback.onError(new OrderFailedException());
            }
        });
    }

    @Override
    public void moveOrderToLoyaltyAccount(String ncrOrderId,
                                          MoveLoyaltyRequest moveLoyaltyRequest,
                                          ResultCallback<NcrOrderWrappedData> callback) {
        mNcrOrderApi.moveOrderToLoyaltyAccount(ncrOrderId, moveLoyaltyRequest)
                .enqueue(new BaseRetrofitCallback<NcrOrderWrappedData>() {

                    @Override
                    protected void onSuccess(Response<NcrOrderWrappedData> response) {
                        callback.onSuccess(response.body());
                    }

                    @Override
                    protected void onFail(Response<NcrOrderWrappedData> response) {
                        super.onFail(response);
                        callback.onFail(response.code(), "");
                    }

                    @Override
                    protected void onNetworkFail(IOException throwable) {
                        super.onNetworkFail(throwable);
                        callback.onError(throwable);
                    }

                });

    }

    @Override
    public DateTime getCurbsidePickupTime() {
        CurbsideOrderData curbsideOrderData = mUserServices.getCurbsideOrderData();
        if (curbsideOrderData == null) {
            return null;
        }
        return curbsideOrderData.getPickupTime();
    }

    @Override
    public void getPOSAgentStatus(String storeId, ResultCallback<OmsPOSAgentStatus> callback) {
        // We always return ONLINE for NCR POS
        OmsPOSAgentStatus omsPOSAgentStatus = new OmsPOSAgentStatus();
        omsPOSAgentStatus.setStatus(EnumPOSAgentStatus.ONLINE);
        callback.onSuccess(omsPOSAgentStatus);
    }

    @Override
    public void applyReward(int rewardId, ResultCallback<Order> callback) {
        bounce(callback, rewardId);
    }

    @Override
    public void clearReward(ResultCallback<Order> callback) {
        // No need to do anything special here. The reward to be removed will be sent as part of a normal checkout action.
        setPreSelectedReward(null);
        getOrder().setRewardErrorMessage(null);
        bounce(callback, null);
    }

    @Override
    public void loadOrderBanner(ResultCallback<RewardBannerModel> callback) {
        mNcrOrderApi.loadApplicableRewards(getOrder().getNcrOrderId())
                .enqueue(new RetrofitCallbackWrapper<List<CmsReward>, RewardBannerModel>(callback) {
                    @Override
                    protected void onSuccess(Response<List<CmsReward>> response) {
                        List<CmsReward> applicableRewards = response.body();

                        if (applicableRewards == null || applicableRewards.isEmpty()) {
                            callback.onSuccess(null);
                            return;
                        }

                        CmsReward firstApplicableReward = applicableRewards.get(0);
                        if (firstApplicableReward == null) {
                            callback.onSuccess(null);
                            return;
                        }

                        RewardBannerModel rewardBannerModel = new RewardBannerModel();
                        rewardBannerModel.setDescription(firstApplicableReward.getHeading());
                        rewardBannerModel.setRewardId(firstApplicableReward.getRewardId());
                        callback.onSuccess(rewardBannerModel);
                    }
                });
    }

    @Override
    public boolean isContinueOrderSupported() {
        return true;
    }


    @Override
    public void addItem(NcrOrderItem orderItem, ResultCallback<NcrOrderItem> callback) {
        try {
            updateLastActivity();
            getOrder().setEdited(true);
            orderItem.setId(UUID.randomUUID().toString());
            getOrder().addItem(orderItem);
            fireItemAdded(orderItem);
            fireSubtotalChanged();
            callback.onSuccess(orderItem);
        } catch (RuntimeException e) {
            callback.onError(e);
        }
    }


    @Override
    public void updateItem(NcrOrderItem orderItem, ResultCallback<NcrOrderItem> callback) {
        updateLastActivity();
        Order order = getOrder();
        int indexOf = order.getItems().indexOf(orderItem);
        if (indexOf == -1) {
            callback.onError(new IllegalStateException("There is no item with id: " + orderItem.getId()));
            return;
        }

        order.getItems().remove(indexOf);
        order.getItems().add(indexOf, orderItem);
        order.setEdited(true);
        try {
            fireItemChanged(orderItem);
            fireSubtotalChanged();
            callback.onSuccess(orderItem);
        } catch (Exception e) {
            callback.onError(e);
        }
    }

    @Override
    public void removeItem(NcrOrderItem orderItem, ResultCallback<NcrOrderItem> callback) {
        updateLastActivity();
        Order order = getOrder();
        int indexOf = order.getItems().indexOf(orderItem);
        if (indexOf == -1) {
            callback.onError(new IllegalStateException("There is no item with id: " + orderItem.getId()));
            return;
        }

        order.setEdited(true);
        try {
            order.getItems().remove(orderItem);
            order.notifyPropertyChanged(BR.items);
            order.notifyPropertyChanged(BR.totalItemsInCart);
            fireItemRemoved(orderItem);
            fireSubtotalChanged();
            callback.onSuccess(orderItem);
        } catch (Exception e) {
            callback.onError(e);
        }
    }

    @Override
    public void updateQuantity(NcrOrderItem orderItem, int newQuantity,
                               ResultCallback<NcrOrderItem> callback) {
        updateLastActivity();
        Order<NcrOrderItem> order = getOrder();
        int indexOf = order.getItems().indexOf(orderItem);
        if (indexOf == -1) {
            callback.onError(new IllegalStateException("There is no item with id: " + orderItem.getId()));
            return;
        }
        order.setEdited(true);
        try {
            NcrOrderItem savedOrderItem = order.getItems().get(indexOf);
            savedOrderItem.setQuantity(newQuantity);
            order.notifyPropertyChanged(BR.totalItemsInCart);
            fireItemChanged(savedOrderItem);
            fireSubtotalChanged();
            callback.onSuccess(orderItem);
        } catch (Exception e) {
            callback.onError(e);
        }
    }

    @Override
    public void checkout(ResultCallback<Order> callback) {
        Integer rewardToApply = getOrder().getPreSelectedReward() != null ? getOrder().getPreSelectedReward().getRewardId() : null;
        bounce(callback, rewardToApply);
    }

    private void bounce(ResultCallback<Order> callback, Integer applyRewardId) {
        try {
            NcrOrder ncrOrder = getOrder();
            NcrOrderWrappedData ncrOrderWrappedData = ncrOrder.toServerData(true);
            ncrOrderWrappedData.getNcrOrderApiData().setStatus(NcrOrderStatus.ReadyForValidation);

            if (applyRewardId != null) {
                ncrOrderWrappedData.getRewardsToApply().add(applyRewardId);
            }

            Tracer tracer =
                    mTracerFactory
                            .createTracer(AppConstants.CUSTOM_METRIC_ID_NCR_ORDER_BOUNCE)
                            .start();
            RetrofitCallbackWrapper<NcrOrderWrappedData, Order> callbackWrapper =
                    new RetrofitCallbackWrapper<NcrOrderWrappedData, Order>(callback) {
                        @Override
                        protected void onSuccess(Response<NcrOrderWrappedData> response) {
                            updateBasicOrderData(response.body(), true);
                            waitForOrderCommandFinish(new ResultCallbackWrapper<Order>(callback) {
                                @Override
                                public void onSuccess(Order data) {
                                    tracer.end();
                                    callback.onSuccess(data);
                                }
                            }, NcrOrderStatus.Validated);
                        }
                    };

            String nepEnterpriseUnit = mNcrMenuDataService.getNepEnterpriseUnit();
            if (ncrOrder.getNcrOrderId() == null) {
                mNcrOrderApi.postOrder(nepEnterpriseUnit, ncrOrderWrappedData).enqueue(callbackWrapper);
            } else {
                mNcrOrderApi.putOrder(nepEnterpriseUnit, ncrOrder.getNcrOrderId(), ncrOrderWrappedData).enqueue(callbackWrapper);
            }
        } catch (RuntimeException e) {
            callback.onError(e);
        }
    }

    @Override
    public void placeOrder(ResultCallback<Order> callback) {
        NcrOrder ncrOrder = getOrder();
        try {
            NcrOrderWrappedData ncrOrderWrappedData = ncrOrder.toServerData(false);
            boolean iGuestOrder = (!mUserServices.isUserLoggedIn()) && mUserServices.isGuestUserFlowStarted();
            if (iGuestOrder) {
                ncrOrderWrappedData.getNcrOrderApiData().setStatus(NcrOrderStatus.InProgress);
            } else {
                ncrOrderWrappedData.getNcrOrderApiData().setStatus(NcrOrderStatus.OrderPlaced);
            }

            String nepEnterpriseUnit = mNcrMenuDataService.getNepEnterpriseUnit();
            ncrOrderWrappedData.setPickup(ncrOrder.buildPickupData(getClock()));

            if (!ncrOrder.getChosenPickUpTime().isAsap()) {
                ncrOrderWrappedData.setScheduleIn(
                        DateUtil.minutesUntil(getClock(), ncrOrder.getChosenPickUpTime().getPickUpTime()));
            }

            Tracer tracer =
                    mTracerFactory.createTracer(AppConstants.CUSTOM_METRIC_ID_NCR_ORDER_PLACE).start();
            mNcrOrderApi.putOrder(nepEnterpriseUnit, ncrOrder.getNcrOrderId(), ncrOrderWrappedData)
                    .enqueue(new RetrofitCallbackWrapper<NcrOrderWrappedData, Order>(callback) {
                        @Override
                        protected void onSuccess(Response<NcrOrderWrappedData> response) {
                            if (iGuestOrder) {
                                waitForOrderCommandFinish(
                                        new ResultCallbackWrapper<Order>(callback) {
                                            @Override
                                            public void onSuccess(Order data) {
                                                tracer.end();
                                                postOrderPlaced(callback);
                                            }

                                            @Override
                                            public void onError(Throwable error) {
                                                if (error instanceof OrderFailedException) {
                                                    callback.onError(error);
                                                } else {
                                                    postOrderPlaced(callback);
                                                }
                                            }

                                            @Override
                                            public void onFail(int errorCode, String errorMessage) {
                                                postOrderPlaced(callback);
                                            }
                                        }, NcrOrderStatus.InProgress,
                                        NcrOrderStatus.Validated,
                                        NcrOrderStatus.ReceivedForFulfillment,
                                        NcrOrderStatus.InFulfillment,
                                        NcrOrderStatus.ReadyForPickup, NcrOrderStatus.Finished);
                            } else {
                                waitForOrderCommandFinish(
                                        new ResultCallbackWrapper<Order>(callback) {
                                            @Override
                                            public void onSuccess(Order data) {
                                                tracer.end();
                                                postOrderPlaced(callback);
                                            }

                                            @Override
                                            public void onError(Throwable error) {
                                                if (error instanceof OrderFailedException) {
                                                    callback.onError(error);
                                                } else {
                                                    postOrderPlaced(callback);
                                                }
                                            }

                                            @Override
                                            public void onFail(int errorCode, String errorMessage) {
                                                postOrderPlaced(callback);
                                            }
                                        },
                                        NcrOrderStatus.ReceivedForFulfillment, NcrOrderStatus.InFulfillment,
                                        NcrOrderStatus.ReadyForPickup, NcrOrderStatus.Finished);
                            }


                        }

                        @Override
                        protected void onFail(Response<NcrOrderWrappedData> response) {
                            if (response.code() == HttpURLConnection.HTTP_GATEWAY_TIMEOUT) {
                                Log.e(TAG, new LogErrorException("Order place NCR server timeout."
                                        + " status: " + response.code()
                                        + printOrderData(NcrOrderStatus.OrderPlaced)));
                                postOrderPlaced(callback);
                                return;
                            }

                            Log.e(TAG, new LogErrorException("Order server error for place request."
                                    + " status: " + response.code()
                                    + printOrderData(NcrOrderStatus.OrderPlaced)));
                            super.onFail(response);
                        }

                        @Override
                        protected void onError(Throwable throwable) {
                            Log.e(TAG, new LogErrorException("Order error for place command."
                                    + " message:" + throwable.getMessage()
                                    + printOrderData(NcrOrderStatus.OrderPlaced)));
                            super.onError(throwable);
                        }

                        @Override
                        protected void onNetworkFail(IOException throwable) {
                            if (throwable instanceof SocketTimeoutException) {
                                Log.e(TAG, new LogErrorException("Order timeout for place request."
                                        + printOrderData(NcrOrderStatus.OrderPlaced)));

                                postOrderPlaced(callback);
                            } else {
                                Log.e(TAG, new LogErrorException("Order network fail for place command."
                                        + " message:" + throwable.getMessage()
                                        + printOrderData(NcrOrderStatus.OrderPlaced)));
                                callback.onError(throwable);
                            }
                        }
                    });
        } catch (RuntimeException e) {
            callback.onError(e);
        }
    }

    private void postOrderPlaced(ResultCallback<Order> callback) {
        NcrOrder order = getOrder();
        if (order.getPickupData() != null && order.getPickupData().getYextPickupType() == YextPickupType.Curbside) {
            saveCurbsideOrderData(order);
        }
        ResultCallback<Order> discardCallback = new ResultCallback<Order>() {
            @Override
            public void onSuccess(Order data) {
                // NO-OP
            }

            @Override
            public void onFail(int errorCode, String errorMessage) {
                Log.e(TAG, new LogErrorException(StringUtils.format("Error %d discarding the order: %s", errorCode, errorMessage)));
            }

            @Override
            public void onError(Throwable error) {
                Log.e(TAG, new LogErrorException("Error discarding order", error));
            }
        };
        callback.onSuccess(getOrder());
        boolean iGuestOrder = (!mUserServices.isUserLoggedIn()) && mUserServices.isGuestUserFlowStarted();
        if (!iGuestOrder) {
            discard(discardCallback);
        }
    }

    private void saveCurbsideOrderData(NcrOrder order) {
        PickUpTimeModel pickUpTimeModel = order.getChosenPickUpTime();
        LocalTime pickupTime = pickUpTimeModel.isAsap() ? getClock().getCurrentTime() : pickUpTimeModel.getPickUpTime();

        CurbsideOrderData curbsideOrderData = new CurbsideOrderData(
                order.getNcrOrderId(), getClock().getCurrentDateTime().withTime(pickupTime),
                order.getStoreLocation().getId(),
                order.getPickupData().getCurbsidePickupData(),
                order.getChosenPickUpTime().isAsap(),
                order.getStatus());

        mUserServices.saveCurbsideOrderData(curbsideOrderData);
        mUserServices.saveCurbsideLocationPhone(order.getStoreLocation().getPhone());
        mUserServices.saveCurbsideLocationMessage(order.getStoreLocation().getCurbsideInstruction());
        mUserServices.saveCurbsideLatestPickupTime(curbsideOrderData.getPickupTime());
    }

    @Override
    public void checkOmsStatus(ResultCallback<ResponseBody> callback) {
        mNcrOrderApi.checkServerStatus().enqueue(new RetrofitCallbackWrapper<ResponseBody, ResponseBody>(callback) {
            @Override
            protected void onSuccess(Response<ResponseBody> response) {
                callback.onSuccess(response.body());
            }
        });
    }

    @Override
    public void reorder(RecentOrderModel recentOrderModel, StoreLocation storeLocation, ResultCallback<Order> callback) {
        mNcrOrderApi.reorder(((NcrRecentOrderModel) recentOrderModel).getCheckNumber())
                .enqueue(new RetrofitCallbackWrapper<NcrOrderWrappedData, Order>(callback) {
                    @Override
                    protected void onSuccess(Response<NcrOrderWrappedData> response) {
                        createNcrOrderFromServerData(response.body(), storeLocation, callback);
                    }
                });
    }

    private void createNcrOrderFromServerData(NcrOrderWrappedData ncrOrderWrappedData, StoreLocation storeLocation, ResultCallback<Order> callback) {
        NcrOrder ncrOrder = new NcrOrder(storeLocation);
        setOrder(ncrOrder);
        updateLastActivity();
        List<NcrReorderErrors> ncrReorderErrors = ncrOrderWrappedData.getReorderErrorsList();
        ncrOrder.setErrorReplicatingItems(ncrReorderErrors != null && !ncrReorderErrors.isEmpty());
        ncrOrder.setFromReorder(true);
        List<NcrOrderLine> productOrderLinesByLineId = new ArrayList<>();
        Map<String, List<NcrOrderLine>> customizationOrderLineByParentId = new HashMap<>();
        populateOrderLinesItemCustomization(ncrOrderWrappedData, productOrderLinesByLineId, customizationOrderLineByParentId);

        getMenuDataService().clearCache();
        getMenuDataService().getOrderAheadMenuDataFiltered(storeLocation.getId(), null, new ResultCallbackWrapper<MenuData>(callback) {
            @Override
            public void onSuccess(MenuData data) {
                for (NcrOrderLine productOrderLine : productOrderLinesByLineId) {
                    generateReOrderItem(productOrderLine, customizationOrderLineByParentId.get(productOrderLine.getLineId()));
                }
                callback.onSuccess(getOrder());
            }
        });


    }

    private void populateOrderLinesItemCustomization(NcrOrderWrappedData ncrOrderWrappedData,
                                                     List<NcrOrderLine> productOrderLinesByLineId,
                                                     Map<String, List<NcrOrderLine>> customizationOrderLineByParentId) {
        for (NcrOrderLine ncrOrderLine : ncrOrderWrappedData.getNcrOrderApiData().getNcrOrderLines()) {
            String parentLineId = ncrOrderLine.getParentLineId();
            if (parentLineId == null) {
                productOrderLinesByLineId.add(ncrOrderLine);
            } else {
                List<NcrOrderLine> customizationList = customizationOrderLineByParentId.get(ncrOrderLine.getParentLineId());
                if (customizationList == null) {
                    customizationList = new ArrayList<>();
                    customizationOrderLineByParentId.put(ncrOrderLine.getParentLineId(), customizationList);
                }
                customizationList.add(ncrOrderLine);
            }
        }
    }

    @Override
    public void getProductCustomizations(String
                                                 omsProdId, ResultCallback<ProductCustomizationData> callback) {
        ProductCustomizationData productCustomizationData = mNcrMenuDataService.getProductData(omsProdId).getNcrOmsData();
        if (productCustomizationData == null) {
            callback.onFail(HttpURLConnection.HTTP_NOT_FOUND, "No product customization data for omsProdId: " + omsProdId);
            return;
        }
        callback.onSuccess(productCustomizationData);
    }

    void waitForOrderCommandFinish(ResultCallback<Order> resultCallback, NcrOrderStatus... successfulfinishStatus) {
        mOrderExecutor.execute(() -> {

            boolean keepWaiting = true;
            NcrOrderWrappedData orderServerData = null;
            int attempts = 0;

            Set<NcrOrderStatus> successfulFinishStatus = new ArraySet<>(Arrays.asList(successfulfinishStatus));
            Set<NcrOrderStatus> stopWaitStatusSet = new HashSet<>(successfulFinishStatus);
            stopWaitStatusSet.add(NcrOrderStatus.Failed);

            while (keepWaiting) {

                try {
                    Thread.sleep(AppConstants.ORDER_AHEAD_CHECK_STATUS_WAIT_IN_MILISECONDS);
                } catch (InterruptedException e) {
                    resultCallback.onError(e);
                    return;
                }

                try {
                    Response<NcrOrderWrappedData> response = mNcrOrderApi.getOrder(getOrder().getNcrOrderId()).execute();
                    if (response.isSuccessful()) {
                        orderServerData = response.body();
                        keepWaiting = orderServerData == null
                                || orderServerData.getNcrOrderStatus() == null
                                || !stopWaitStatusSet.contains(orderServerData.getNcrOrderStatus());
                    } else {
                        Log.e(TAG, new LogErrorException("Order server error for check status request."
                                + " responseCode:" + response.code()
                                + " responseMessage: " + response.message()
                                + printOrderData(orderServerData != null ? orderServerData.getNcrOrderStatus() : null)));
                        resultCallback.onFail(response.code(), response.message());
                        return;
                    }
                } catch (Exception e) {
                    if (e instanceof SocketTimeoutException) {
                        Log.e(TAG, new LogErrorException("Order timeout for check status request."
                                + printOrderData(orderServerData != null ? orderServerData.getNcrOrderStatus() : null)));
                    } else {
                        Log.e(TAG, new LogErrorException("Order exception for check status request."
                                + " message:" + e.getMessage()
                                + printOrderData(orderServerData != null ? orderServerData.getNcrOrderStatus() : null)));
                    }
                    resultCallback.onError(e);
                    return;
                }
                attempts++;

                if (attempts >= getCheckStatusMaxAttempts()) {
                    Log.e(TAG, new LogErrorException("Order timeout."
                            + printOrderData(orderServerData.getNcrOrderStatus())));
                    resultCallback.onError(new SocketTimeoutException("Read timeout"));
                    return;
                }
            }

            if (successfulFinishStatus.contains(orderServerData.getNcrOrderStatus())) {
                // We have a final status on the order data
                updateBasicOrderData(orderServerData, false);
                resultCallback.onSuccess(getOrder());
            } else {
                // Order failed
                Log.e(TAG, new LogErrorException("Order failed."
                        + printOrderData(orderServerData != null ? orderServerData.getNcrOrderStatus() : null)));
                resultCallback.onError(new OrderFailedException());
            }
        });
    }

    private NcrRecentOrderModel generateFromOrder(NcrOrderWrappedData order) {
        NcrRecentOrderModel recentOrderModel = new NcrRecentOrderModel();
        RecentOrderStore recentOrderStore = new RecentOrderStore(order.getNcrLocation().getName(), order.getNcrLocation().getNumber());
        recentOrderModel.setRecentOrderStore(recentOrderStore);
        recentOrderModel.setCheckNumber(order.getCheckNumber());
        Map<String, RecentOrderItem> recentOrderItems = new HashMap<>();
        List<NcrOrderLine> recentOrderModifiers = new ArrayList<>();
        separateItemAndModifiers(order, recentOrderItems, recentOrderModifiers);
        populateItemsModifiers(recentOrderItems, recentOrderModifiers);
        recentOrderModel.setOrderItems(new ArrayList<>(recentOrderItems.values()));
        return recentOrderModel;
    }

    private void populateItemsModifiers(Map<String, RecentOrderItem> recentOrderItems, List<NcrOrderLine> recentOrderModifiers) {
        for (NcrOrderLine ncrOrderLine : recentOrderModifiers) {
            RecentOrderItem recentOrderItem = recentOrderItems.get(ncrOrderLine.getParentLineId());
            if (recentOrderItem != null) {
                recentOrderItem.getCustomizations().add(ncrOrderLine.getDescription());
            }
        }
    }

    private void separateItemAndModifiers(NcrOrderWrappedData order, Map<String,
            RecentOrderItem> recentOrderItems, List<NcrOrderLine> recentOrderModifiers) {
        for (NcrOrderLine ncrOrderLine : order.getNcrOrderApiData().getNcrOrderLines()) {
            if (ncrOrderLine.getLineId() != null && ncrOrderLine.getParentLineId() == null) {
                RecentOrderItem recentOrderItemToAdd = new RecentOrderItem();
                recentOrderItemToAdd.setProductName(ncrOrderLine.getDescription());
                recentOrderItemToAdd.setQuantity(new BigDecimal(ncrOrderLine.getQuantity().getValue()));
                recentOrderItems.put(ncrOrderLine.getLineId(), recentOrderItemToAdd);
            } else {
                recentOrderModifiers.add(ncrOrderLine);
            }
        }
    }

    private void updateBasicOrderData(NcrOrderWrappedData orderServerData, boolean updateRewardsError) {
        NcrOrder order = getOrder();
        final String ncrOrderId = orderServerData.getNcrOrderApiData().getNcrOrderId();
        if (ncrOrderId != null) {
            order.setNcrOrderId(ncrOrderId);
        }
        order.setStatus(orderServerData.getNcrOrderApiData().getStatus());

        order.setSubtotal(orderServerData.getSubtotal());
        order.setTaxes(orderServerData.getTax());
        order.setTotal(orderServerData.getTotal());
        List<ServerAppliedReward> rewards = orderServerData.getAppliedRewards();
        updateDiscountLines(rewards);
        if (updateRewardsError) {
            List<NcrRewardError> rewardErrors = orderServerData.getRewardErrors();
            if (rewardErrors == null || rewardErrors.isEmpty()) {
                order.setRewardErrorMessage(null);
            } else {
                order.setRewardErrorMessage(rewardErrors.get(0).getMessage());
            }
        }
    }

    private void generateReOrderItem(NcrOrderLine productOrderLine, List<NcrOrderLine> customizationsOrderLines) {
        NcrOrder currentOrder = getOrder();
        String saleItemProductId = productOrderLine.getProductId().getValue();
        MenuCardItemModel itemMenuData = mNcrMenuDataService.getProductDataBySaleItemId(saleItemProductId);
        if (itemMenuData == null) {
            Log.e(TAG, new LogErrorException("Missing product in menu: " + saleItemProductId));
            return;
        }

        NcrOrderItem ncrOrderItem = new NcrOrderItem(itemMenuData);
        ncrOrderItem.setQuantity(productOrderLine.getQuantity().getValue());
        ncrOrderItem.setId(UUID.randomUUID().toString());
        ncrOrderItem.loadModifiers(itemMenuData.getNcrOmsData());
        NcrSaleItem saleItem = getItemSaleItem(itemMenuData, saleItemProductId);
        if (saleItem == null) {
            Log.e(TAG, new LogErrorException("Missing sales item for product Id: " + saleItemProductId));
            return;
        }
        ncrOrderItem.setSize(getSizeFromSaleItem(saleItem, ncrOrderItem));

        int remaining = currentOrder.calculateRemainingOrderItemQuantity(ncrOrderItem, ncrOrderItem.getQuantity());

        if (remaining >= 0) {
            currentOrder.addItem(ncrOrderItem);
            setCustomizations(ncrOrderItem, saleItemProductId, saleItem, customizationsOrderLines);
        } else if (ncrOrderItem.getQuantity() + remaining > 0) { // if it not possible to add all, try to add as most as possible
            ncrOrderItem.setQuantity(ncrOrderItem.getQuantity() + remaining);
            currentOrder.setErrorMaxQuantityHasChanged(true);
            currentOrder.addItem(ncrOrderItem);
            setCustomizations(ncrOrderItem, saleItemProductId, saleItem, customizationsOrderLines);
        } else {
            currentOrder.setErrorMaxQuantityHasChanged(true);
        }
    }

    private void setCustomizations(
            NcrOrderItem ncrOrderItem,
            String saleItemProductId,
            NcrSaleItem saleItem,
            List<NcrOrderLine> customizationsOrderLines) {
        if (customizationsOrderLines != null) {
            for (NcrOrderLine ncrOrderLine : customizationsOrderLines) {
                String linkItemProductId = ncrOrderLine.getProductId().getValue();
                Pair<NcrLinkGroup, NcrLinkItem> itemCustomization = saleItem.findGroupAndItemByProductId(linkItemProductId);

                if (itemCustomization == null) {
                    Log.e(TAG,
                            new LogErrorException("Missing customization. salesItemProductId: "
                                    + saleItemProductId + " linkItemProductId: " + linkItemProductId));
                    continue;
                }

                ModifierGroup modifierGroup = ncrOrderItem.getModifierGroupById(itemCustomization.first.getId());
                String ncrLinkItemId = itemCustomization.second.getId();
                ItemModifier itemModifier = modifierGroup.getModifierById(ncrLinkItemId);
                ItemOption itemOption = itemModifier.getOptionById(ncrLinkItemId);
                ncrOrderItem.setCustomization(modifierGroup, itemModifier, itemOption);
            }
        }
    }

    private SizeEnum getSizeFromSaleItem(NcrSaleItem saleItem, NcrOrderItem ncrOrderItem) {
        for (Map.Entry<SizeEnum, NcrSaleItem> sizeEnumNcrSaleItemEntry : ncrOrderItem.getPerSizeNcrSalesItems().entrySet()) {
            if (sizeEnumNcrSaleItemEntry.getValue().getProductId().equals(saleItem.getProductId())) {
                return sizeEnumNcrSaleItemEntry.getKey();
            }
        }
        return null;
    }

    private NcrSaleItem getItemSaleItem(MenuCardItemModel itemMenuData, String saleItemProductId) {
        for (NcrSaleItem saleItem : itemMenuData.getNcrOmsData().getSalesItems()) {
            if (saleItem.getProductId().equals(saleItemProductId)) {
                return saleItem;
            }
        }
        return null;
    }


    private String printOrderData(NcrOrderStatus ncrOrderStatus) {
        return printOrderData(getOrder(), ncrOrderStatus);
    }

    private String printOrderData(NcrOrder order, NcrOrderStatus ncrOrderStatus) {
        String orderDataStr = " status: " + ncrOrderStatus + " orderId: " + order.getNcrOrderId();
        if (order.getStoreLocation() != null) {
            orderDataStr += " locationId: " + order.getStoreLocation().getId();
        }
        return orderDataStr;
    }

}
