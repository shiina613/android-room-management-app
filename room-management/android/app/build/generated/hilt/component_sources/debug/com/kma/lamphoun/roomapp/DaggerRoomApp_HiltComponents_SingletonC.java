package com.kma.lamphoun.roomapp;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.kma.lamphoun.roomapp.data.local.TokenDataStore;
import com.kma.lamphoun.roomapp.data.remote.api.ApiService;
import com.kma.lamphoun.roomapp.di.NetworkModule_ProvideApiServiceFactory;
import com.kma.lamphoun.roomapp.di.NetworkModule_ProvideAuthInterceptorFactory;
import com.kma.lamphoun.roomapp.di.NetworkModule_ProvideOkHttpClientFactory;
import com.kma.lamphoun.roomapp.di.NetworkModule_ProvideRetrofitFactory;
import com.kma.lamphoun.roomapp.ui.auth.AuthViewModel;
import com.kma.lamphoun.roomapp.ui.auth.AuthViewModel_HiltModules;
import com.kma.lamphoun.roomapp.ui.landlord.ContractViewModel;
import com.kma.lamphoun.roomapp.ui.landlord.ContractViewModel_HiltModules;
import com.kma.lamphoun.roomapp.ui.landlord.DashboardViewModel;
import com.kma.lamphoun.roomapp.ui.landlord.DashboardViewModel_HiltModules;
import com.kma.lamphoun.roomapp.ui.landlord.InvoiceViewModel;
import com.kma.lamphoun.roomapp.ui.landlord.InvoiceViewModel_HiltModules;
import com.kma.lamphoun.roomapp.ui.landlord.MeterReadingViewModel;
import com.kma.lamphoun.roomapp.ui.landlord.MeterReadingViewModel_HiltModules;
import com.kma.lamphoun.roomapp.ui.landlord.PaymentViewModel;
import com.kma.lamphoun.roomapp.ui.landlord.PaymentViewModel_HiltModules;
import com.kma.lamphoun.roomapp.ui.landlord.ReportViewModel;
import com.kma.lamphoun.roomapp.ui.landlord.ReportViewModel_HiltModules;
import com.kma.lamphoun.roomapp.ui.landlord.RoomViewModel;
import com.kma.lamphoun.roomapp.ui.landlord.RoomViewModel_HiltModules;
import com.kma.lamphoun.roomapp.ui.shared.NotificationViewModel;
import com.kma.lamphoun.roomapp.ui.shared.NotificationViewModel_HiltModules;
import com.kma.lamphoun.roomapp.ui.shared.ProfileViewModel;
import com.kma.lamphoun.roomapp.ui.shared.ProfileViewModel_HiltModules;
import com.kma.lamphoun.roomapp.ui.tenant.TenantViewModel;
import com.kma.lamphoun.roomapp.ui.tenant.TenantViewModel_HiltModules;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class DaggerRoomApp_HiltComponents_SingletonC {
  private DaggerRoomApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public RoomApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements RoomApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public RoomApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements RoomApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public RoomApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements RoomApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public RoomApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements RoomApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public RoomApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements RoomApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public RoomApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements RoomApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public RoomApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements RoomApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public RoomApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends RoomApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends RoomApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends RoomApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends RoomApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity arg0) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(11).put(LazyClassKeyProvider.com_kma_lamphoun_roomapp_ui_auth_AuthViewModel, AuthViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_kma_lamphoun_roomapp_ui_landlord_ContractViewModel, ContractViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_kma_lamphoun_roomapp_ui_landlord_DashboardViewModel, DashboardViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_kma_lamphoun_roomapp_ui_landlord_InvoiceViewModel, InvoiceViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_kma_lamphoun_roomapp_ui_landlord_MeterReadingViewModel, MeterReadingViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_kma_lamphoun_roomapp_ui_shared_NotificationViewModel, NotificationViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_kma_lamphoun_roomapp_ui_landlord_PaymentViewModel, PaymentViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_kma_lamphoun_roomapp_ui_shared_ProfileViewModel, ProfileViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_kma_lamphoun_roomapp_ui_landlord_ReportViewModel, ReportViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_kma_lamphoun_roomapp_ui_landlord_RoomViewModel, RoomViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_kma_lamphoun_roomapp_ui_tenant_TenantViewModel, TenantViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_kma_lamphoun_roomapp_ui_auth_AuthViewModel = "com.kma.lamphoun.roomapp.ui.auth.AuthViewModel";

      static String com_kma_lamphoun_roomapp_ui_landlord_MeterReadingViewModel = "com.kma.lamphoun.roomapp.ui.landlord.MeterReadingViewModel";

      static String com_kma_lamphoun_roomapp_ui_landlord_InvoiceViewModel = "com.kma.lamphoun.roomapp.ui.landlord.InvoiceViewModel";

      static String com_kma_lamphoun_roomapp_ui_landlord_ReportViewModel = "com.kma.lamphoun.roomapp.ui.landlord.ReportViewModel";

      static String com_kma_lamphoun_roomapp_ui_shared_NotificationViewModel = "com.kma.lamphoun.roomapp.ui.shared.NotificationViewModel";

      static String com_kma_lamphoun_roomapp_ui_tenant_TenantViewModel = "com.kma.lamphoun.roomapp.ui.tenant.TenantViewModel";

      static String com_kma_lamphoun_roomapp_ui_landlord_ContractViewModel = "com.kma.lamphoun.roomapp.ui.landlord.ContractViewModel";

      static String com_kma_lamphoun_roomapp_ui_landlord_DashboardViewModel = "com.kma.lamphoun.roomapp.ui.landlord.DashboardViewModel";

      static String com_kma_lamphoun_roomapp_ui_shared_ProfileViewModel = "com.kma.lamphoun.roomapp.ui.shared.ProfileViewModel";

      static String com_kma_lamphoun_roomapp_ui_landlord_PaymentViewModel = "com.kma.lamphoun.roomapp.ui.landlord.PaymentViewModel";

      static String com_kma_lamphoun_roomapp_ui_landlord_RoomViewModel = "com.kma.lamphoun.roomapp.ui.landlord.RoomViewModel";

      @KeepFieldType
      AuthViewModel com_kma_lamphoun_roomapp_ui_auth_AuthViewModel2;

      @KeepFieldType
      MeterReadingViewModel com_kma_lamphoun_roomapp_ui_landlord_MeterReadingViewModel2;

      @KeepFieldType
      InvoiceViewModel com_kma_lamphoun_roomapp_ui_landlord_InvoiceViewModel2;

      @KeepFieldType
      ReportViewModel com_kma_lamphoun_roomapp_ui_landlord_ReportViewModel2;

      @KeepFieldType
      NotificationViewModel com_kma_lamphoun_roomapp_ui_shared_NotificationViewModel2;

      @KeepFieldType
      TenantViewModel com_kma_lamphoun_roomapp_ui_tenant_TenantViewModel2;

      @KeepFieldType
      ContractViewModel com_kma_lamphoun_roomapp_ui_landlord_ContractViewModel2;

      @KeepFieldType
      DashboardViewModel com_kma_lamphoun_roomapp_ui_landlord_DashboardViewModel2;

      @KeepFieldType
      ProfileViewModel com_kma_lamphoun_roomapp_ui_shared_ProfileViewModel2;

      @KeepFieldType
      PaymentViewModel com_kma_lamphoun_roomapp_ui_landlord_PaymentViewModel2;

      @KeepFieldType
      RoomViewModel com_kma_lamphoun_roomapp_ui_landlord_RoomViewModel2;
    }
  }

  private static final class ViewModelCImpl extends RoomApp_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<AuthViewModel> authViewModelProvider;

    private Provider<ContractViewModel> contractViewModelProvider;

    private Provider<DashboardViewModel> dashboardViewModelProvider;

    private Provider<InvoiceViewModel> invoiceViewModelProvider;

    private Provider<MeterReadingViewModel> meterReadingViewModelProvider;

    private Provider<NotificationViewModel> notificationViewModelProvider;

    private Provider<PaymentViewModel> paymentViewModelProvider;

    private Provider<ProfileViewModel> profileViewModelProvider;

    private Provider<ReportViewModel> reportViewModelProvider;

    private Provider<RoomViewModel> roomViewModelProvider;

    private Provider<TenantViewModel> tenantViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.authViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.contractViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.dashboardViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.invoiceViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.meterReadingViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.notificationViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.paymentViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.profileViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
      this.reportViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 8);
      this.roomViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 9);
      this.tenantViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 10);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(11).put(LazyClassKeyProvider.com_kma_lamphoun_roomapp_ui_auth_AuthViewModel, ((Provider) authViewModelProvider)).put(LazyClassKeyProvider.com_kma_lamphoun_roomapp_ui_landlord_ContractViewModel, ((Provider) contractViewModelProvider)).put(LazyClassKeyProvider.com_kma_lamphoun_roomapp_ui_landlord_DashboardViewModel, ((Provider) dashboardViewModelProvider)).put(LazyClassKeyProvider.com_kma_lamphoun_roomapp_ui_landlord_InvoiceViewModel, ((Provider) invoiceViewModelProvider)).put(LazyClassKeyProvider.com_kma_lamphoun_roomapp_ui_landlord_MeterReadingViewModel, ((Provider) meterReadingViewModelProvider)).put(LazyClassKeyProvider.com_kma_lamphoun_roomapp_ui_shared_NotificationViewModel, ((Provider) notificationViewModelProvider)).put(LazyClassKeyProvider.com_kma_lamphoun_roomapp_ui_landlord_PaymentViewModel, ((Provider) paymentViewModelProvider)).put(LazyClassKeyProvider.com_kma_lamphoun_roomapp_ui_shared_ProfileViewModel, ((Provider) profileViewModelProvider)).put(LazyClassKeyProvider.com_kma_lamphoun_roomapp_ui_landlord_ReportViewModel, ((Provider) reportViewModelProvider)).put(LazyClassKeyProvider.com_kma_lamphoun_roomapp_ui_landlord_RoomViewModel, ((Provider) roomViewModelProvider)).put(LazyClassKeyProvider.com_kma_lamphoun_roomapp_ui_tenant_TenantViewModel, ((Provider) tenantViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_kma_lamphoun_roomapp_ui_auth_AuthViewModel = "com.kma.lamphoun.roomapp.ui.auth.AuthViewModel";

      static String com_kma_lamphoun_roomapp_ui_landlord_ContractViewModel = "com.kma.lamphoun.roomapp.ui.landlord.ContractViewModel";

      static String com_kma_lamphoun_roomapp_ui_shared_ProfileViewModel = "com.kma.lamphoun.roomapp.ui.shared.ProfileViewModel";

      static String com_kma_lamphoun_roomapp_ui_landlord_PaymentViewModel = "com.kma.lamphoun.roomapp.ui.landlord.PaymentViewModel";

      static String com_kma_lamphoun_roomapp_ui_landlord_MeterReadingViewModel = "com.kma.lamphoun.roomapp.ui.landlord.MeterReadingViewModel";

      static String com_kma_lamphoun_roomapp_ui_tenant_TenantViewModel = "com.kma.lamphoun.roomapp.ui.tenant.TenantViewModel";

      static String com_kma_lamphoun_roomapp_ui_shared_NotificationViewModel = "com.kma.lamphoun.roomapp.ui.shared.NotificationViewModel";

      static String com_kma_lamphoun_roomapp_ui_landlord_RoomViewModel = "com.kma.lamphoun.roomapp.ui.landlord.RoomViewModel";

      static String com_kma_lamphoun_roomapp_ui_landlord_DashboardViewModel = "com.kma.lamphoun.roomapp.ui.landlord.DashboardViewModel";

      static String com_kma_lamphoun_roomapp_ui_landlord_ReportViewModel = "com.kma.lamphoun.roomapp.ui.landlord.ReportViewModel";

      static String com_kma_lamphoun_roomapp_ui_landlord_InvoiceViewModel = "com.kma.lamphoun.roomapp.ui.landlord.InvoiceViewModel";

      @KeepFieldType
      AuthViewModel com_kma_lamphoun_roomapp_ui_auth_AuthViewModel2;

      @KeepFieldType
      ContractViewModel com_kma_lamphoun_roomapp_ui_landlord_ContractViewModel2;

      @KeepFieldType
      ProfileViewModel com_kma_lamphoun_roomapp_ui_shared_ProfileViewModel2;

      @KeepFieldType
      PaymentViewModel com_kma_lamphoun_roomapp_ui_landlord_PaymentViewModel2;

      @KeepFieldType
      MeterReadingViewModel com_kma_lamphoun_roomapp_ui_landlord_MeterReadingViewModel2;

      @KeepFieldType
      TenantViewModel com_kma_lamphoun_roomapp_ui_tenant_TenantViewModel2;

      @KeepFieldType
      NotificationViewModel com_kma_lamphoun_roomapp_ui_shared_NotificationViewModel2;

      @KeepFieldType
      RoomViewModel com_kma_lamphoun_roomapp_ui_landlord_RoomViewModel2;

      @KeepFieldType
      DashboardViewModel com_kma_lamphoun_roomapp_ui_landlord_DashboardViewModel2;

      @KeepFieldType
      ReportViewModel com_kma_lamphoun_roomapp_ui_landlord_ReportViewModel2;

      @KeepFieldType
      InvoiceViewModel com_kma_lamphoun_roomapp_ui_landlord_InvoiceViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.kma.lamphoun.roomapp.ui.auth.AuthViewModel 
          return (T) new AuthViewModel(singletonCImpl.provideApiServiceProvider.get(), singletonCImpl.tokenDataStoreProvider.get());

          case 1: // com.kma.lamphoun.roomapp.ui.landlord.ContractViewModel 
          return (T) new ContractViewModel(singletonCImpl.provideApiServiceProvider.get());

          case 2: // com.kma.lamphoun.roomapp.ui.landlord.DashboardViewModel 
          return (T) new DashboardViewModel(singletonCImpl.provideApiServiceProvider.get(), singletonCImpl.tokenDataStoreProvider.get());

          case 3: // com.kma.lamphoun.roomapp.ui.landlord.InvoiceViewModel 
          return (T) new InvoiceViewModel(singletonCImpl.provideApiServiceProvider.get());

          case 4: // com.kma.lamphoun.roomapp.ui.landlord.MeterReadingViewModel 
          return (T) new MeterReadingViewModel(singletonCImpl.provideApiServiceProvider.get());

          case 5: // com.kma.lamphoun.roomapp.ui.shared.NotificationViewModel 
          return (T) new NotificationViewModel(singletonCImpl.provideApiServiceProvider.get());

          case 6: // com.kma.lamphoun.roomapp.ui.landlord.PaymentViewModel 
          return (T) new PaymentViewModel(singletonCImpl.provideApiServiceProvider.get());

          case 7: // com.kma.lamphoun.roomapp.ui.shared.ProfileViewModel 
          return (T) new ProfileViewModel(singletonCImpl.provideApiServiceProvider.get());

          case 8: // com.kma.lamphoun.roomapp.ui.landlord.ReportViewModel 
          return (T) new ReportViewModel(singletonCImpl.provideApiServiceProvider.get());

          case 9: // com.kma.lamphoun.roomapp.ui.landlord.RoomViewModel 
          return (T) new RoomViewModel(singletonCImpl.provideApiServiceProvider.get());

          case 10: // com.kma.lamphoun.roomapp.ui.tenant.TenantViewModel 
          return (T) new TenantViewModel(singletonCImpl.provideApiServiceProvider.get(), singletonCImpl.tokenDataStoreProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends RoomApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends RoomApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends RoomApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<TokenDataStore> tokenDataStoreProvider;

    private Provider<Interceptor> provideAuthInterceptorProvider;

    private Provider<OkHttpClient> provideOkHttpClientProvider;

    private Provider<Retrofit> provideRetrofitProvider;

    private Provider<ApiService> provideApiServiceProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.tokenDataStoreProvider = DoubleCheck.provider(new SwitchingProvider<TokenDataStore>(singletonCImpl, 4));
      this.provideAuthInterceptorProvider = DoubleCheck.provider(new SwitchingProvider<Interceptor>(singletonCImpl, 3));
      this.provideOkHttpClientProvider = DoubleCheck.provider(new SwitchingProvider<OkHttpClient>(singletonCImpl, 2));
      this.provideRetrofitProvider = DoubleCheck.provider(new SwitchingProvider<Retrofit>(singletonCImpl, 1));
      this.provideApiServiceProvider = DoubleCheck.provider(new SwitchingProvider<ApiService>(singletonCImpl, 0));
    }

    @Override
    public void injectRoomApp(RoomApp roomApp) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.kma.lamphoun.roomapp.data.remote.api.ApiService 
          return (T) NetworkModule_ProvideApiServiceFactory.provideApiService(singletonCImpl.provideRetrofitProvider.get());

          case 1: // retrofit2.Retrofit 
          return (T) NetworkModule_ProvideRetrofitFactory.provideRetrofit(singletonCImpl.provideOkHttpClientProvider.get());

          case 2: // okhttp3.OkHttpClient 
          return (T) NetworkModule_ProvideOkHttpClientFactory.provideOkHttpClient(singletonCImpl.provideAuthInterceptorProvider.get());

          case 3: // okhttp3.Interceptor 
          return (T) NetworkModule_ProvideAuthInterceptorFactory.provideAuthInterceptor(singletonCImpl.tokenDataStoreProvider.get());

          case 4: // com.kma.lamphoun.roomapp.data.local.TokenDataStore 
          return (T) new TokenDataStore(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
