package com.kma.lamphoun.roomapp.ui.tenant;

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
public final class TenantViewModel_Factory implements Factory<TenantViewModel> {
  private final Provider<ApiService> apiProvider;

  private final Provider<TokenDataStore> tokenDataStoreProvider;

  public TenantViewModel_Factory(Provider<ApiService> apiProvider,
      Provider<TokenDataStore> tokenDataStoreProvider) {
    this.apiProvider = apiProvider;
    this.tokenDataStoreProvider = tokenDataStoreProvider;
  }

  @Override
  public TenantViewModel get() {
    return newInstance(apiProvider.get(), tokenDataStoreProvider.get());
  }

  public static TenantViewModel_Factory create(Provider<ApiService> apiProvider,
      Provider<TokenDataStore> tokenDataStoreProvider) {
    return new TenantViewModel_Factory(apiProvider, tokenDataStoreProvider);
  }

  public static TenantViewModel newInstance(ApiService api, TokenDataStore tokenDataStore) {
    return new TenantViewModel(api, tokenDataStore);
  }
}
