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

package com.knapp.codingcontest.kcc2018.warehouse.aisle;

public class Position {
  // shuttle.move
  private final int aisle;
  private final int distanceIntoAisle;
  // shuttle.(load|store)
  private final Aisle.Side side;

  // ----------------------------------------------------------------------------

  public Position(final int aisle, final int distanceIntoAisle, final Aisle.Side side) {
    this.aisle = aisle;
    this.side = side;
    this.distanceIntoAisle = distanceIntoAisle;
  }

  // ----------------------------------------------------------------------------

  @Override
  public String toString() {
    return String.format("%d;%d;%s;", aisle, distanceIntoAisle, side);
  }

  // ----------------------------------------------------------------------------

  public int getAisle() {
    return aisle;
  }

  public int getDistanceIntoAisle() {
    return distanceIntoAisle;
  }

  // ............................................................................

  public Aisle.Side getSide() {
    return side;
  }

  // ----------------------------------------------------------------------------

  public boolean equalsIgnoreSide(final Position position) {
    return (getAisle() == position.getAisle()) && (getDistanceIntoAisle() == position.getDistanceIntoAisle());
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------
}
