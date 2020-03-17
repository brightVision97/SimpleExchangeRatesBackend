package com.rachev.foreignexchange.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@ApiModel("A container for the custom conversion object response.")
public class ExchangeRatesConvertedResponse {

  @ApiModelProperty("The base currency for the conversion.")
  private String from;

  @ApiModelProperty("The currency which we are converting to.")
  private String to;

  @ApiModelProperty("The input amount for conversion.")
  private Double amount;

  @ApiModelProperty("The calculated amount after conversion.")
  private Double convertedResult;

  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public String getTo() {
    return to;
  }

  public void setTo(String to) {
    this.to = to;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public Double getConvertedResult() {
    return convertedResult;
  }

  public void setConvertedResult(Double convertedResult) {
    this.convertedResult = convertedResult;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof ExchangeRatesConvertedResponse)) {
      return false;
    }

    ExchangeRatesConvertedResponse that = (ExchangeRatesConvertedResponse) o;

    return new EqualsBuilder()
        .append(from, that.from)
        .append(to, that.to)
        .append(amount, that.amount)
        .append(convertedResult, that.convertedResult)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(from)
        .append(to)
        .append(amount)
        .append(convertedResult)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("from", from)
        .append("to", to)
        .append("amount", amount)
        .append("convertedResult", convertedResult)
        .toString();
  }
}
