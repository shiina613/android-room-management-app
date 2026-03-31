package com.kma.lamphoun.roomapp.di;

import com.kma.lamphoun.roomapp.data.local.TokenDataStore;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.Interceptor;

@ScopeMetadata("javax.inject.Singleton")
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
public final class NetworkModule_ProvideAuthInterceptorFactory implements Factory<Interceptor> {
  private final Provider<TokenDataStore> tokenDataStoreProvider;

  public NetworkModule_ProvideAuthInterceptorFactory(
      Provider<TokenDataStore> tokenDataStoreProvider) {
    this.tokenDataStoreProvider = tokenDataStoreProvider;
  }

  @Override
  public Interceptor get() {
    return provideAuthInterceptor(tokenDataStoreProvider.get());
  }

  public static NetworkModule_ProvideAuthInterceptorFactory create(
      Provider<TokenDataStore> tokenDataStoreProvider) {
    return new NetworkModule_ProvideAuthInterceptorFactory(tokenDataStoreProvider);
  }

  public static Interceptor provideAuthInterceptor(TokenDataStore tokenDataStore) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideAuthInterceptor(tokenDataStore));
  }
}
