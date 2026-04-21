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
public final class ReportViewModel_Factory implements Factory<ReportViewModel> {
  private final Provider<ApiService> apiProvider;

  public ReportViewModel_Factory(Provider<ApiService> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public ReportViewModel get() {
    return newInstance(apiProvider.get());
  }

  public static ReportViewModel_Factory create(Provider<ApiService> apiProvider) {
    return new ReportViewModel_Factory(apiProvider);
  }

  public static ReportViewModel newInstance(ApiService api) {
    return new ReportViewModel(api);
  }
}
