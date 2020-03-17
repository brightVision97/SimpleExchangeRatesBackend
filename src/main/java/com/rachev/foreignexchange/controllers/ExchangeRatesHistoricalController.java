package com.rachev.foreignexchange.controllers;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import com.rachev.foreignexchange.services.ExchangeRatesHistoricalService;
import com.rachev.foreignexchange.utils.ControllersHelper;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exchangerates/historical")
public class ExchangeRatesHistoricalController {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final ExchangeRatesHistoricalService exchangeRatesHistoricalService;

  @Autowired
  public ExchangeRatesHistoricalController(
      final ExchangeRatesHistoricalService exchangeRatesHistoricalService) {
    this.exchangeRatesHistoricalService = exchangeRatesHistoricalService;
  }

  @ApiResponses({
      @ApiResponse(
          code = HttpServletResponse.SC_OK,
          message = "Latest exchange rates for the given base currency and date.",
          response = ResponseEntity.class),
      @ApiResponse(
          code = HttpServletResponse.SC_NOT_FOUND,
          message = "No json response was returned from the external api.",
          response = ResponseEntity.class),
      @ApiResponse(
          code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          message = "Internal server error",
          response = ResponseEntity.class)
  })
  @GetMapping("/forDate")
  public ResponseEntity<?> getRatesForDate(@RequestParam final String base,
      @RequestParam final String date) {
    
    if (!ControllersHelper.isDateParsable(date)) {
      return new ResponseEntity<>(BAD_REQUEST);
    }

    return exchangeRatesHistoricalService.getRatesForDate(base, date)
        .map(responseJson -> new ResponseEntity<>(responseJson, OK))
        .onErrorReturn(ex -> {
          if (ex instanceof IllegalStateException) {
            logger.warn(ex.getMessage());
            return new ResponseEntity<>(ex.getMessage(), NOT_FOUND);
          }
          return new ResponseEntity<>(ex.getMessage(), INTERNAL_SERVER_ERROR);
        })
        .blockingGet(new ResponseEntity<>(UNPROCESSABLE_ENTITY));
  }

  @ApiResponses({
      @ApiResponse(
          code = HttpServletResponse.SC_OK,
          message = "Latest exchange rates for the given base currency interval of time.",
          response = ResponseEntity.class),
      @ApiResponse(
          code = HttpServletResponse.SC_NOT_FOUND,
          message = "No json response was returned from the external api.",
          response = ResponseEntity.class),
      @ApiResponse(
          code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          message = "Internal server error",
          response = ResponseEntity.class)
  })
  @GetMapping("/forPeriod")
  public ResponseEntity<?> getRatesForPeriod(@RequestParam final String base,
      @RequestParam final String start_at, @RequestParam final String end_at) {

    if (!ControllersHelper.isDateParsable(start_at) || !ControllersHelper.isDateParsable(end_at)) {
      return new ResponseEntity<>(BAD_REQUEST);
    }
    
    return exchangeRatesHistoricalService.getRatesForPeriod(base, start_at, end_at)
        .map(responseJson -> new ResponseEntity<>(responseJson, OK))
        .onErrorReturn(ex -> {
          if (ex instanceof IllegalStateException) {
            logger.warn(ex.getMessage());
            return new ResponseEntity<>(ex.getMessage(), NOT_FOUND);
          }
          return new ResponseEntity<>(ex.getMessage(), INTERNAL_SERVER_ERROR);
        })
        .blockingGet(new ResponseEntity<>(UNPROCESSABLE_ENTITY));
  }
}
