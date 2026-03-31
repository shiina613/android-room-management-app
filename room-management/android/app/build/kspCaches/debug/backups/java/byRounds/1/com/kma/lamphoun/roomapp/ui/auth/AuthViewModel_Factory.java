package com.kma.lamphoun.roomapp.ui.auth;

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
public final class AuthViewModel_Factory implements Factory<AuthViewModel> {
  private final Provider<ApiService> apiProvider;

  private final Provider<TokenDataStore> tokenDataStoreProvider;

  public AuthViewModel_Factory(Provider<ApiService> apiProvider,
      Provider<TokenDataStore> tokenDataStoreProvider) {
    this.apiProvider = apiProvider;
    this.tokenDataStoreProvider = tokenDataStoreProvider;
  }

  @Override
  public AuthViewModel get() {
    return newInstance(apiProvider.get(), tokenDataStoreProvider.get());
  }

  public static AuthViewModel_Factory create(Provider<ApiService> apiProvider,
      Provider<TokenDataStore> tokenDataStoreProvider) {
    return new AuthViewModel_Factory(apiProvider, tokenDataStoreProvider);
  }

  public static AuthViewModel newInstance(ApiService api, TokenDataStore tokenDataStore) {
    return new AuthViewModel(api, tokenDataStore);
  }
}
