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

package com.knapp.codingcontest.kcc2018.warehouse;

import java.util.ArrayList;
import java.util.List;

import com.knapp.codingcontest.kcc2018.data.Container;
import com.knapp.codingcontest.kcc2018.data.Order;
import com.knapp.codingcontest.kcc2018.warehouse.aisle.Aisle;
import com.knapp.codingcontest.kcc2018.warehouse.aisle.Position;

public class WorkStation extends Position {
  // ----------------------------------------------------------------------------

  private final Warehouse warehouse;
  private final List<Order> expectedOrders;
  private int expectedOrderIndex;
  private Shuttle shuttleAtWorkStation;

  // ----------------------------------------------------------------------------

  WorkStation(final Warehouse warehouse, final int aisle) {
    super(aisle, -2, Aisle.Side._Undefined_);
    this.warehouse = warehouse;
    expectedOrders = new ArrayList<Order>(warehouse.getOrders());
    expectedOrderIndex = 0;
  }

  // ----------------------------------------------------------------------------

  @Override
  public String toString() {
    return "WorkStation@" + super.toString() + "[" + getCurrentShuttle() + "]";
  }

  // ----------------------------------------------------------------------------

  public void pickOrder(final Order order) throws IllegalArgumentException, ShuttleNotAtLocationException,
      InvalidShuttleLoadException, ProductMismatchException, UnexpectedOrderException {
    if (order == null) {
      throw new IllegalArgumentException("order must not be <null>");
    }

    if (getCurrentShuttle() == null) {
      throw new ShuttleNotAtLocationException("no shuttle at " + this);
    }
    if ((getCurrentShuttle().getLoadedContainer() == null) || getCurrentShuttle().getLoadedContainer().isEmpty()) {
      throw new InvalidShuttleLoadException("no/empty container on shuttle at " + this);
    }

    final Container container = getCurrentShuttle().getLoadedContainer();
    int quantity = 0;
    if (order.getProductCode().equals(container.getProductCode())) {
      quantity = Math.min(container.getQuantity(), order.getRemainingQuantity());
    } else {
      throw new ProductMismatchException("product not requested: " + order + " != " + container.getProductCode());
    }

    final Order expectedOrder = expectedOrders.get(expectedOrderIndex);
    if (!order.getOrderCode().equals(expectedOrder.getOrderCode())) {
      throw new UnexpectedOrderException("unexpected order\n  " + order + "\n  ==> while processing " + expectedOrder);
    }

    warehouse.warehouseOperations.pickOrder(order, container.getProductCode(), quantity);

    order._put(container.getProductCode(), quantity);
    getCurrentShuttle().getLoadedContainer()._take(quantity);

    if (!expectedOrder.hasRemainingItems()) {
      expectedOrderIndex++;
    }
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  public Shuttle getCurrentShuttle() {
    return shuttleAtWorkStation;
  }

  void setShuttle(final Shuttle shuttleAtWorkStation) {
    this.shuttleAtWorkStation = shuttleAtWorkStation;
  }
}
