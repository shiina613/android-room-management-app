package com.kma.lamphoun.roomapp.ui.landlord;

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
public final class ContractViewModel_Factory implements Factory<ContractViewModel> {
  private final Provider<ApiService> apiProvider;

  public ContractViewModel_Factory(Provider<ApiService> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public ContractViewModel get() {
    return newInstance(apiProvider.get());
  }

  public static ContractViewModel_Factory create(Provider<ApiService> apiProvider) {
    return new ContractViewModel_Factory(apiProvider);
  }

  public static ContractViewModel newInstance(ApiService api) {
    return new ContractViewModel(api);
  }
}
