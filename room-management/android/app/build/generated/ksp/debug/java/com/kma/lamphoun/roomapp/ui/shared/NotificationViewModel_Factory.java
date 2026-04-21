package com.kma.lamphoun.roomapp.ui.shared;

import com.kma.lamphoun.roomapp.data.remote.api.ApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
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
public final class NotificationViewModel_Factory implements Factory<NotificationViewModel> {
  private final Provider<ApiService> apiProvider;

  public NotificationViewModel_Factory(Provider<ApiService> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public NotificationViewModel get() {
    return newInstance(apiProvider.get());
  }

  public static NotificationViewModel_Factory create(Provider<ApiService> apiProvider) {
    return new NotificationViewModel_Factory(apiProvider);
  }

  public static NotificationViewModel newInstance(ApiService api) {
    return new NotificationViewModel(api);
  }
}
