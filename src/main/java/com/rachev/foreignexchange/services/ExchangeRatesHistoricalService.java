package com.rachev.foreignexchange.services;

import io.reactivex.rxjava3.core.Maybe;

public interface ExchangeRatesHistoricalService {

  Maybe<String> getRatesForDate(final String base, final String date);

  Maybe<String> getRatesForPeriod(final String base, final String start_at, final String end_at);
}
