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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import com.knapp.codingcontest.kcc2018.warehouse.LocationNotFoundException;

public class Aisle {
  public static enum Side {
    Left, Right, //
    _Undefined_, // used for shuttle and workstation
    ;
  }

  private final int aisle;
  private final Location[][] __locations;
  private final Collection<Location> _locations = new HashSet<Location>();

  // ----------------------------------------------------------------------------

  public Aisle(final int aisle, final int numberOfPositionsPerAisle, final int locationDepth) {
    this.aisle = aisle;
    __locations = new Location[numberOfPositionsPerAisle][];
    initLocations(locationDepth);
  }

  // ----------------------------------------------------------------------------

  public Collection<Location> getLocations() {
    return Collections.unmodifiableCollection(_locations);
  }

  public Location getLocation(final int distanceIntoAisle, final Side side) {
    if (((distanceIntoAisle >= 0) && (distanceIntoAisle < __locations.length)) && (side != null)) {
      return __locations[distanceIntoAisle][side.ordinal()];
    }
    throw new LocationNotFoundException("no location found @ " + distanceIntoAisle + "/" + side);
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  private void initLocations(final int locationDepth) {
    for (int dia = 0; dia < __locations.length; dia++) {
      __locations[dia] = new Location[] { //
      /**/new Location(new Position(aisle, dia, Side.Left), locationDepth),
          new Location(new Position(aisle, dia, Side.Right), locationDepth) };
      _locations.add(__locations[dia][0]);
      _locations.add(__locations[dia][1]);
    }
  }
}
