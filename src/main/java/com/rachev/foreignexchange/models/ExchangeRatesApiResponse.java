package com.rachev.foreignexchange.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Map;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@ApiModel("Container for the response of the external exchange rates api.")
public class ExchangeRatesApiResponse {

  @ApiModelProperty("The base currency country code.")
  private String base;
  
  @ApiModelProperty("The date of relevance of the information, in format (yyyy-MM-dd)")
  private String date;

  @ApiModelProperty("Key value pairs of country codes and corresponding conversion rates.")
  private Map<String, Double> rates;

  public String getBase() {
    return base;
  }

  public void setBase(String base) {
    this.base = base;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public Map<String, Double> getRates() {
    return rates;
  }

  public void setRates(Map<String, Double> rates) {
    this.rates = rates;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof ExchangeRatesApiResponse)) {
      return false;
    }

    ExchangeRatesApiResponse that = (ExchangeRatesApiResponse) o;

    return new EqualsBuilder()
        .append(base, that.base)
        .append(date, that.date)
        .append(rates, that.rates)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(base)
        .append(date)
        .append(rates)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("base", base)
        .append("date", date)
        .append("rates", rates)
        .toString();
  }
}
