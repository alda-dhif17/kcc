/* -*- java -*- ************************************************************************** *
 *
 *                     Copyright (C) KNAPP AG
 *
 *   The copyright to the computer program(s) herein is the property
 *   of Knapp.  The program(s) may be used   and/or copied only with
 *   the  written permission of  Knapp  or in  accordance  with  the
 *   terms and conditions stipulated in the agreement/contract under
 *   which the program(s) have been supplied.
 *
 * *************************************************************************************** */

package com.knapp.codingcontest.kcc2018.data;

public class Order {
  private final String orderCode;
  private final String productCode;
  private final int requestedQuantity;
  private int remainingQuantity;

  // ----------------------------------------------------------------------------

  public Order(final String orderCode, final String productCode, final int requestedQuantity) {
    this.orderCode = orderCode;
    this.productCode = productCode;
    this.requestedQuantity = requestedQuantity;
    remainingQuantity = requestedQuantity;
  }

  // ----------------------------------------------------------------------------

  @Override
  public String toString() {
    return "Order#" + orderCode + "[" + productCode + ", " + remainingQuantity + "/" + requestedQuantity + "]";
  }

  // ----------------------------------------------------------------------------

  public String getOrderCode() {
    return orderCode;
  }

  public String getProductCode() {
    return productCode;
  }

  public int getRequestedQuantity() {
    return requestedQuantity;
  }

  public int getRemainingQuantity() {
    return remainingQuantity;
  }

  // ----------------------------------------------------------------------------

  // called by WorkStation - do not call directly
  public void _put(final String productCode, final int quantity) {
    if (productCode.equals(this.getProductCode())) {
      if ((remainingQuantity - quantity) < 0) {
        throw new IllegalArgumentException("can't pick #" + quantity + " for " + this);
      }
      remainingQuantity -= quantity;
    } else {
      throw new IllegalArgumentException(productCode + " not requested: " + this);
    }
  }

  public boolean hasRemainingItems() {
    return (getRemainingQuantity() > 0);
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------
}
