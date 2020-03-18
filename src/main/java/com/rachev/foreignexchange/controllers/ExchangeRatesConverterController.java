package com.rachev.foreignexchange.controllers;

import com.rachev.foreignexchange.services.ExchangeRatesConverterService;
import com.rachev.foreignexchange.utils.ControllersHelper;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exchangerates")
public class ExchangeRatesConverterController {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final ExchangeRatesConverterService exchangeRatesConverterService;
  private final ExchangeRatesConverterService.Async asyncExchangeRatesConverterService;

  @Autowired
  public ExchangeRatesConverterController(
      final ExchangeRatesConverterService exchangeRatesConverterService,
      final ExchangeRatesConverterService.Async asyncExchangeRatesConverterService) {
    this.exchangeRatesConverterService = exchangeRatesConverterService;
    this.asyncExchangeRatesConverterService = asyncExchangeRatesConverterService;
  }

  @ApiResponses({
      @ApiResponse(
          code = HttpServletResponse.SC_OK,
          message = "Latest exchange rates for particular country codes.",
          response = ResponseEntity.class),
      @ApiResponse(
          code = HttpServletResponse.SC_BAD_REQUEST,
          message = "One or more invalid input parameters.",
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
  @GetMapping("/convert")
  public ResponseEntity<?> convert(@RequestParam final String from,
      @RequestParam final String to, final @RequestParam String amount) {

    if (StringUtils.isAnyEmpty(from, to, amount)) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    if (!ControllersHelper.isDoubleParsable(amount)) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    return exchangeRatesConverterService.convert(from, to, Double.parseDouble(amount))
        .map(response -> new ResponseEntity<>(response, HttpStatus.OK))
        .onErrorReturn(ex -> {
          if (ex instanceof IllegalStateException) {
            logger.warn(ex.getMessage());
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
          }
          return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        })
        .blockingGet(new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY));
  }

  @GetMapping("/convertAsync")
  public ResponseEntity<?> convertAsync(@RequestParam final String from, 
      @RequestParam final String to, final @RequestParam String amount) {

    if (StringUtils.isAnyEmpty(from, to, amount)) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    if (!ControllersHelper.isDoubleParsable(amount)) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    return asyncExchangeRatesConverterService.convert(from, to, Double.parseDouble(amount))
        .thenApply(ResponseEntity::ok)
        .exceptionally(ex -> ResponseEntity.status(ex instanceof IllegalStateException
            ? HttpStatus.NOT_FOUND
            : HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ex.getMessage()))
        .join();
  }
}
