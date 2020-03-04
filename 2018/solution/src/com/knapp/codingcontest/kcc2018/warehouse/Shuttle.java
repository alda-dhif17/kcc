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

import com.knapp.codingcontest.kcc2018.data.Container;
import com.knapp.codingcontest.kcc2018.warehouse.aisle.Aisle;
import com.knapp.codingcontest.kcc2018.warehouse.aisle.Location;
import com.knapp.codingcontest.kcc2018.warehouse.aisle.Position;

public class Shuttle {
  // ----------------------------------------------------------------------------

  private final Warehouse warehouse;
  public final String id;

  private Position currentPosition;
  private Container loadedContainer;

  // ----------------------------------------------------------------------------

  public Shuttle(final Warehouse warehouse, final String id, final Position pos0) {
    this.warehouse = warehouse;
    this.id = id;
    currentPosition = pos0;
  }

  // ----------------------------------------------------------------------------

  @Override
  public String toString() {
    return "Shuttle#" + id + "[" + loadedContainer + "] @ " + positionString();
  }

  private String positionString() {
    if (isAtWorkStation()) {
      return String.format("WORKSTATION @ A%02d", getCurrentPosition().getAisle());
    }
    return String.format("A%02d-DIA%03d", getCurrentPosition().getAisle(), getCurrentPosition().getDistanceIntoAisle());
  }

  // ----------------------------------------------------------------------------

  public boolean isAtWorkStation() {
    return getCurrentPosition().getDistanceIntoAisle() < 0;
  }

  public Position getCurrentPosition() {
    return currentPosition;
  }

  public Container getLoadedContainer() {
    return loadedContainer;
  }

  public Location getCurrentLocation(final Aisle.Side side) {
    if (isAtWorkStation()) {
      throw new IllegalStateException("isAtWorkStation()");
    }
    return warehouse.getAisle(currentPosition.getAisle()).getLocation(currentPosition.getDistanceIntoAisle(), side);
  }

  // ----------------------------------------------------------------------------

  public void moveToPosition(final Position position) throws IllegalArgumentException {
    if (position == null) {
      throw new IllegalArgumentException("'position' must not be <null>");
    }

    if (!isValidPosition(position)) {
      throw new IllegalArgumentException("invalid position: " + position + " - must be either WorkStation("
          + warehouse.getWorkStation() + ") or within extends of locations (>=0/0 - <"
          + warehouse.getCharacteristics().getNumberOfAisles() + "/"
          + warehouse.getCharacteristics().getNumberOfPositionsPerAisle() + ")");
    }

    if (isAtWorkStation()) {
      warehouse.getWorkStation().setShuttle(null);
    }

    final Position previousPosition = getCurrentPosition();
    currentPosition = new Position(position.getAisle(), position.getDistanceIntoAisle(), Aisle.Side._Undefined_);

    if (isAtWorkStation()) {
      warehouse.getWorkStation().setShuttle(this);
    }

    warehouse.warehouseOperations.moveShuttle(previousPosition, position);
  }

  private boolean isValidPosition(final Position position) {
    if (position.equalsIgnoreSide(warehouse.getWorkStation())) {
      return true;
    }
    return (((position.getAisle() >= 0) && (position.getAisle() < warehouse.getCharacteristics().getNumberOfAisles()))) //
        && (((position.getDistanceIntoAisle() >= 0) && (position.getDistanceIntoAisle() < warehouse.getCharacteristics()
            .getNumberOfPositionsPerAisle())));
  }

  public void loadFrom(final Location location, final Container expected) throws IllegalArgumentException,
      InvalidShuttleLoadException, InvalidShuttlePositionException, ShuttleNotAtLocationException {
    if (location == null) {
      throw new IllegalArgumentException("'location' must not be <null>");
    }

    if (loadedContainer != null) {
      throw new InvalidShuttleLoadException("shuttle already occupied: " + this);
    }
    if (isAtWorkStation()) {
      throw new InvalidShuttlePositionException("can't load at work-station: " + this);
    }

    if (!getCurrentPosition().equalsIgnoreSide(location.getPosition())) {
      throw new ShuttleNotAtLocationException("shuttle not at " + location + ": " + this);
    }

    if (!location.isReachable(expected)) {
      throw new NoSuchContainerAtLocationException(expected + " not reachable @ " + location);
    }

    loadedContainer = location._pull();

    warehouse.warehouseOperations.loadContainer(location.getPosition().getSide());
  }

  public void storeTo(final Location location) throws IllegalArgumentException, InvalidShuttleLoadException,
      InvalidShuttlePositionException, ShuttleNotAtLocationException {
    if (location == null) {
      throw new IllegalArgumentException("'location' must not be <null>");
    }
    if (loadedContainer == null) {
      throw new InvalidShuttleLoadException("shuttle is empty: " + this);
    }
    if (isAtWorkStation()) {
      throw new InvalidShuttlePositionException("can't store at work-station: " + this);
    }

    if (!getCurrentPosition().equalsIgnoreSide(location.getPosition())) {
      throw new ShuttleNotAtLocationException("shuttle not at " + location + ": " + this);
    }

    location._push(loadedContainer);
    loadedContainer = null;

    warehouse.warehouseOperations.storeContainer(location.getPosition().getSide());
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------
}
