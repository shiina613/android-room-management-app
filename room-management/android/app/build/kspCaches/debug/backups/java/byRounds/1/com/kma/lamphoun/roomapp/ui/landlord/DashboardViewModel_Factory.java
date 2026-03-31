package com.kma.lamphoun.roomapp.ui.landlord;

import com.kma.lamphoun.roomapp.data.local.TokenDataStore;
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
public final class DashboardViewModel_Factory implements Factory<DashboardViewModel> {
  private final Provider<ApiService> apiProvider;

  private final Provider<TokenDataStore> tokenDataStoreProvider;

  public DashboardViewModel_Factory(Provider<ApiService> apiProvider,
      Provider<TokenDataStore> tokenDataStoreProvider) {
    this.apiProvider = apiProvider;
    this.tokenDataStoreProvider = tokenDataStoreProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(apiProvider.get(), tokenDataStoreProvider.get());
  }

  public static DashboardViewModel_Factory create(Provider<ApiService> apiProvider,
      Provider<TokenDataStore> tokenDataStoreProvider) {
    return new DashboardViewModel_Factory(apiProvider, tokenDataStoreProvider);
  }

  public static DashboardViewModel newInstance(ApiService api, TokenDataStore tokenDataStore) {
    return new DashboardViewModel(api, tokenDataStore);
  }
}
