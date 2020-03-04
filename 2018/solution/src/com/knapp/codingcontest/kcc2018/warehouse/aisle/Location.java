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

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.knapp.codingcontest.kcc2018.data.Container;
import com.knapp.codingcontest.kcc2018.warehouse.NoSuchContainerAtLocationException;

public class Location {
  private final Position position;
  private final int locationDepth;
  private final LinkedList<Container> containers = new LinkedList<Container>();

  public Location(final Position position, final int locationDepth) {
    this.position = position;
    this.locationDepth = locationDepth;
  }

  // ----------------------------------------------------------------------------

  @Override
  public String toString() {
    return String.format("Location[A/%s]@A%02d-DIA%03d-%s", containerCodes(), position.getAisle(),
        position.getDistanceIntoAisle(), position.getSide());
  }

  @Override
  public int hashCode() {
    return position.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Location other = (Location) obj;
    return position.equals(other.position);
  }

  // ----------------------------------------------------------------------------

  public Position getPosition() {
    return position;
  }

  public List<Container> getContainers() {
    return Collections.unmodifiableList(containers);
  }

  public int getRemainingContainerCapacity() {
    return locationDepth - containers.size();
  }

  public boolean isReachable(final Container container) {
    boolean reachable = true;
    for (final Container c : containers) {
      if (c.equals(container)) {
        return reachable;
      } else {
        reachable = false;
      }
    }
    throw new NoSuchContainerAtLocationException(container + " not found @ " + this);
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  // used by Shuttle - do not call directly
  public final Container _pull() {
    if (!containers.isEmpty()) {
      final Container container = containers.removeFirst();
      container._setLocation(null); // container @shuttle
      return container;
    }
    throw new IllegalStateException("failed to pull(): " + this);
  }

  // used by Shuttle & initLocations() - do not call directly
  public final void _push(final Container container) {
    if (containers.size() < locationDepth) {
      container._setLocation(this);
      containers.addFirst(container);
    } else {
      throw new IllegalStateException("failed to push(" + container + "): " + this);
    }
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  private String containerCodes() {
    final String[] containerCodes = new String[locationDepth];
    final Iterator<Container> it = containers.descendingIterator();
    int i = containerCodes.length - 1;
    for (; (i >= 0) && it.hasNext(); i--) {
      final Container container = it.next();
      if (!container.isEmpty()) {
        containerCodes[i] = container.getContainerCode();
      } else {
        containerCodes[i] = "(" + container.getContainerCode() + ")";
      }
    }
    while (i >= 0) {
      containerCodes[i--] = "(empty)";
    }
    return Arrays.toString(containerCodes);
  }

  // ----------------------------------------------------------------------------
}
