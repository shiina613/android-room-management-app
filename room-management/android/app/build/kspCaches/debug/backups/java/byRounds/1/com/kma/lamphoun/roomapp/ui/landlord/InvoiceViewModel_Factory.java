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
public final class InvoiceViewModel_Factory implements Factory<InvoiceViewModel> {
  private final Provider<ApiService> apiProvider;

  public InvoiceViewModel_Factory(Provider<ApiService> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public InvoiceViewModel get() {
    return newInstance(apiProvider.get());
  }

  public static InvoiceViewModel_Factory create(Provider<ApiService> apiProvider) {
    return new InvoiceViewModel_Factory(apiProvider);
  }

  public static InvoiceViewModel newInstance(ApiService api) {
    return new InvoiceViewModel(api);
  }
}
