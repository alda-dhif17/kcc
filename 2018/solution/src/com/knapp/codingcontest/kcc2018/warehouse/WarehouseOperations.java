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

import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

import com.knapp.codingcontest.kcc2018.data.Order;
import com.knapp.codingcontest.kcc2018.warehouse.aisle.Aisle;
import com.knapp.codingcontest.kcc2018.warehouse.aisle.Position;

public final class WarehouseOperations {
  // ----------------------------------------------------------------------------

  public static enum Operation {
    MovePerLocation, //
    MovePerAisle, //
    LoadStore, //
    _UnfinishedOrder_, //
    ;
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  private final Warehouse warehouse;
  private final EnumMap<Operation, Integer> operationCost;

  private final List<WarehouseOperation> warehouseOperations = new LinkedList<WarehouseOperation>();
  private long currentOperationsCost = 0;

  // ----------------------------------------------------------------------------

  WarehouseOperations(final Warehouse warehouse) {
    this.warehouse = warehouse;
    operationCost = new EnumMap<Operation, Integer>(Operation.class);

    operationCost.put(Operation.MovePerLocation, 1);
    operationCost.put(Operation.MovePerAisle, //
        50 * operationCost.get(Operation.MovePerLocation));
    operationCost.put(Operation.LoadStore, //
        10 * operationCost.get(Operation.MovePerLocation));
    operationCost.put(Operation._UnfinishedOrder_, 5000);
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  public long getCurrentOperationsCost() {
    return currentOperationsCost;
  }

  public long getCurrentTotalCost() {
    return getCurrentOperationsCost() + calcUnfinishedOrdersCost() + calcMissingCleanupCost();
  }

  public long getCurrentUnfinishedOrdersCost() {
    return calcUnfinishedOrdersCost();
  }

  public long getCurrentCleanupCost() {
    return calcMissingCleanupCost();
  }

  public Integer getOperationCost(final Operation operation) {
    return operationCost.get(operation);
  }

  public Iterable<WarehouseOperations.WarehouseOperation> result() {
    return Collections.unmodifiableList(warehouseOperations);
  }

  private long calcUnfinishedOrdersCost() {
    int unfinishedOrders = 0;
    for (final Order order : warehouse.getOrders()) {
      if (order.hasRemainingItems()) {
        unfinishedOrders++;
      }
    }
    return unfinishedOrders * operationCost.get(Operation._UnfinishedOrder_);
  }

  private long calcMissingCleanupCost() {
    if (warehouse.getShuttle().getLoadedContainer() != null) {
      return operationCost.get(Operation._UnfinishedOrder_);
    }
    return 0;
  }

  // ----------------------------------------------------------------------------

  void moveShuttle(final Position from, final Position to) {
    final MoveShuttle moveShuttle = new MoveShuttle(from, to);
    add(moveShuttle);
  }

  void loadContainer(final Aisle.Side side) {
    final LoadContainer loadContainer = new LoadContainer(side);
    add(loadContainer);
  }

  void storeContainer(final Aisle.Side side) {
    final StoreContainer storeContainer = new StoreContainer(side);
    add(storeContainer);
  }

  void pickOrder(final Order order, final String productCode, final int quantity) {
    final PickOrder pickOrder = new PickOrder(order, productCode, quantity);
    add(pickOrder);
  }

  private void add(final WarehouseOperation warehouseOperation) {
    warehouseOperations.add(warehouseOperation);
    currentOperationsCost += warehouseOperation.calcCost(warehouse.shuttle);
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  public abstract class WarehouseOperation {
    private final String toResultString;

    private WarehouseOperation(final Object... args) {
      final StringBuilder sb = new StringBuilder();
      sb.append(getClass().getSimpleName()).append(";");
      for (final Object arg : args) {
        sb.append(arg).append(";");
      }
      toResultString = sb.toString();
    }

    public final String toResultString() {
      return toResultString;
    }

    @Override
    public String toString() {
      return toResultString();
    }

    abstract long calcCost(Shuttle shuttle);
  }

  // ----------------------------------------------------------------------------

  public final class MoveShuttle extends WarehouseOperation {
    public final Position from;
    public final Position to;

    public MoveShuttle(final Position from, final Position to) {
      super(from.getAisle(), from.getDistanceIntoAisle(), to.getAisle(), to.getDistanceIntoAisle());
      this.from = from;
      this.to = to;
    }

    @Override
    long calcCost(final Shuttle shuttle) {
      return calcMoveCost(from, to);
    }
  }

  public long calcMoveCost(final Position _from, final Position _to) {
    long cost = 0;
    if (_from.equalsIgnoreSide(_to)) {
      cost += operationCost.get(Operation.MovePerLocation) * 0;
    } else if (_from.getAisle() != _to.getAisle()) {
      cost += operationCost.get(Operation.MovePerLocation); // base cost
      cost += operationCost.get(Operation.MovePerLocation) * (_from.getDistanceIntoAisle() + 1); // crossing is @ (-1)
      cost += operationCost.get(Operation.MovePerAisle) * (Math.abs(_from.getAisle() - _to.getAisle()));
      cost += operationCost.get(Operation.MovePerLocation) * (_to.getDistanceIntoAisle() + 1); // crossing is @ (-1)
    } else {
      cost += operationCost.get(Operation.MovePerLocation); // base cost
      cost += operationCost.get(Operation.MovePerLocation)
          * (Math.abs(_from.getDistanceIntoAisle() - _to.getDistanceIntoAisle()));
    }

    return cost;
  }

  // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

  public final class LoadContainer extends WarehouseOperation {
    public LoadContainer(final Aisle.Side side) {
      super(side);
    }

    @Override
    long calcCost(final Shuttle shuttle) {
      return operationCost.get(Operation.LoadStore);
    }
  }

  // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

  public class StoreContainer extends WarehouseOperation {
    public StoreContainer(final Aisle.Side side) {
      super(side);
    }

    @Override
    long calcCost(final Shuttle shuttle) {
      return operationCost.get(Operation.LoadStore);
    }
  }

  // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

  public final class PickOrder extends WarehouseOperation {
    public final Order order;
    public final String productCode;
    public final int quantity;

    public PickOrder(final Order order, final String productCode, final int quantity) {
      super(order.getOrderCode(), productCode, quantity);
      this.order = order;
      this.productCode = productCode;
      this.quantity = quantity;
    }

    @Override
    long calcCost(final Shuttle shuttle) {
      return 0; // no costs for this operation in this challenge
    }
  }

  // ----------------------------------------------------------------------------
}
