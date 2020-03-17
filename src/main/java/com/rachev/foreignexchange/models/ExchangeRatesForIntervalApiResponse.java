package com.rachev.foreignexchange.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Map;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@ApiModel("Response container for the exchange rates in between a period of time.")
public class ExchangeRatesForIntervalApiResponse {

  @ApiModelProperty("The base currency country code.")
  private String base;

  @ApiModelProperty("Starting date for the interval, in format (yyyy-MM-dd).")
  private String start_at;

  @ApiModelProperty("Ending date for the interval, in format (yyyy-MM-dd).")
  private String end_at;

  @ApiModelProperty("Map of exchange rates for the base currency for each day of the interval.")
  private Map<String, Map<String, Double>> rates;

  public String getBase() {
    return base;
  }

  public void setBase(String base) {
    this.base = base;
  }

  public String getStart_at() {
    return start_at;
  }

  public void setStart_at(String start_at) {
    this.start_at = start_at;
  }

  public String getEnd_at() {
    return end_at;
  }

  public void setEnd_at(String end_at) {
    this.end_at = end_at;
  }

  public Map<String, Map<String, Double>> getRates() {
    return rates;
  }

  public void setRates(
      Map<String, Map<String, Double>> rates) {
    this.rates = rates;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof ExchangeRatesForIntervalApiResponse)) {
      return false;
    }

    ExchangeRatesForIntervalApiResponse that = (ExchangeRatesForIntervalApiResponse) o;

    return new EqualsBuilder()
        .append(base, that.base)
        .append(start_at, that.start_at)
        .append(end_at, that.end_at)
        .append(rates, that.rates)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(base)
        .append(start_at)
        .append(end_at)
        .append(rates)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("base", base)
        .append("start_at", start_at)
        .append("end_at", end_at)
        .append("rates", rates)
        .toString();
  }
}
