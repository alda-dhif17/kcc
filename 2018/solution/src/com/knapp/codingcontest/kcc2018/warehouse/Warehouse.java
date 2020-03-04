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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.knapp.codingcontest.kcc2018.data.Container;
import com.knapp.codingcontest.kcc2018.data.InputData;
import com.knapp.codingcontest.kcc2018.data.Order;
import com.knapp.codingcontest.kcc2018.warehouse.aisle.Aisle;
import com.knapp.codingcontest.kcc2018.warehouse.aisle.Location;
import com.knapp.codingcontest.kcc2018.warehouse.aisle.Position;

public class Warehouse {
  // ----------------------------------------------------------------------------

  public final InputData input;

  private final Characteristics characteristics;
  final WarehouseOperations warehouseOperations;
  private final WorkStation workStation;
  final Shuttle shuttle;
  private final Aisle[] aisles;
  private final Collection<Container> containers = new LinkedHashSet<Container>();

  // ----------------------------------------------------------------------------

  public Warehouse(final InputData input) {
    this.input = input;
    characteristics = new Characteristics(input.getWarehouseCharacteristics());
    warehouseOperations = new WarehouseOperations(this);
    workStation = new WorkStation(this, characteristics.getWorkStationAisle());
    shuttle = new Shuttle(this, "A", new Position(characteristics.getWorkStationAisle(), 0, Aisle.Side._Undefined_));
    aisles = new Aisle[characteristics.numberOfAisles];
    initLocations();
  }

  // ----------------------------------------------------------------------------

  public Characteristics getCharacteristics() {
    return characteristics;
  }

  public List<Order> getOrders() {
    return Collections.unmodifiableList(input.getOrders());
  }

  public Collection<Container> getAllContainers() {
    return Collections.unmodifiableCollection(containers);
  }

  // ----------------------------------------------------------------------------

  public Aisle[] getAisles() {
    return aisles;
  }

  public Aisle getAisle(final int aisle) {
    if ((aisle >= 0) && (aisle < aisles.length)) {
      return aisles[aisle];
    }
    throw new LocationNotFoundException("no aisle with index=" + aisle);
  }

  public WorkStation getWorkStation() {
    return workStation;
  }

  public Shuttle getShuttle() {
    return shuttle;
  }

  // ----------------------------------------------------------------------------

  public long getCurrentOperationsCost() {
    return warehouseOperations.getCurrentOperationsCost();
  }

  public long getCurrentUnfinishedOrdersCost() {
    return warehouseOperations.getCurrentUnfinishedOrdersCost();
  }

  public long getCurrentCleanupCost() {
    return warehouseOperations.getCurrentCleanupCost();
  }

  public long getCurrentTotalCost() {
    return warehouseOperations.getCurrentTotalCost();
  }

  public Integer getOperationCost(final WarehouseOperations.Operation operation) {
    return warehouseOperations.getOperationCost(operation);
  }

  public Iterable<WarehouseOperations.WarehouseOperation> result() {
    return warehouseOperations.result();
  }

  // ----------------------------------------------------------------------------

  public static class Characteristics {
    private final int numberOfAisles;
    private final int numberOfPositionsPerAisle;
    private final int locationDepth;
    private final int workStationAisle;

    private Characteristics(final Properties warehouseCharacteristics) {
      numberOfAisles = Integer.parseInt((String) warehouseCharacteristics.get("numberOfAisles"));
      numberOfPositionsPerAisle = Integer.parseInt((String) warehouseCharacteristics.get("numberOfLocations"));
      locationDepth = Integer.parseInt((String) warehouseCharacteristics.get("locationDepth"));
      workStationAisle = Integer.parseInt((String) warehouseCharacteristics.get("workStationAisle"));
    }

    public int getNumberOfAisles() {
      return numberOfAisles;
    }

    public int getNumberOfPositionsPerAisle() {
      return numberOfPositionsPerAisle;
    }

    public int getLocationDepth() {
      return locationDepth;
    }

    public int getWorkStationAisle() {
      return workStationAisle;
    }
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  public long calcMoveCost(final Location from, final Location to) {
    return calcMoveCost(from.getPosition(), to.getPosition());
  }

  public long calcMoveCost(final Position _from, final Position _to) {
    return warehouseOperations.calcMoveCost(_from, _to);
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  private void initLocations() throws IllegalArgumentException, LocationNotFoundException {
    for (int a = 0; a < aisles.length; a++) {
      aisles[a] = new Aisle(a, characteristics.numberOfPositionsPerAisle, characteristics.locationDepth);
    }

    // input.containers are ordered depth-first!
    for (final Map.Entry<Container, Position> entry : input.getContainers().entrySet()) {
      final Container container = entry.getKey();
      final Location location = getLocation(entry.getValue());
      container._setLocation(location);
      containers.add(container);
    }

    for (final Container container : containers) {
      container.getLocation()._push(container);
    }
  }

  private Location getLocation(final Position position) throws IllegalArgumentException, LocationNotFoundException {
    if (position == null) {
      throw new IllegalArgumentException("position must not be <null>");
    }
    if ((position.getAisle() >= 0) && (position.getAisle() < aisles.length)) {
      return aisles[position.getAisle()].getLocation(position.getDistanceIntoAisle(), position.getSide());
    }
    throw new LocationNotFoundException("invalid aisle: " + position);
  }
}
