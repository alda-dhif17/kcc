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

import com.knapp.codingcontest.kcc2018.warehouse.aisle.Location;

public class Container {
  // ----------------------------------------------------------------------------

  private final String containerCode;
  private String productCode;
  private int quantity;
  private Location location;

  // ----------------------------------------------------------------------------

  public Container(final String containerCode) {
    this.containerCode = containerCode;
  }

  // ----------------------------------------------------------------------------

  @Override
  public String toString() {
    return "Container#" + containerCode + "[" + (isEmpty() ? "EMPTY" : (productCode + ", #" + quantity)) + "]"
        + (location != null ? "@" + location.getPosition() : "");
  }

  // ----------------------------------------------------------------------------

  @Override
  public int hashCode() {
    return containerCode.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (!(obj instanceof Container)) {
      return false;
    }
    final Container other = (Container) obj;
    return containerCode.equals(other.containerCode);
  }

  // ----------------------------------------------------------------------------

  public boolean isEmpty() {
    return quantity <= 0;
  }

  public String getContainerCode() {
    return containerCode;
  }

  public String getProductCode() {
    return productCode;
  }

  public int getQuantity() {
    return quantity;
  }

  public Location getLocation() {
    return location;
  }

  // ----------------------------------------------------------------------------

  // used by WorkStation - do not call directly
  public void _take(final int quantity) {
    if (quantity > this.quantity) {
      throw new IllegalArgumentException("can't take #" + quantity + " from " + this);
    }

    this.quantity -= quantity;
    if (this.quantity == 0) {
      setProductCode(null);
    }
  }

  // ............................................................................

  // used by InputData - do not call directly
  protected void setProductCode(final String productCode) {
    this.productCode = productCode;
  }

  // used by InputData - do not call directly
  protected void setQuantity(final int quantity) {
    this.quantity = quantity;
  }

  // used by initLocations() & shuttle -  do not call directly
  public void _setLocation(final Location location) {
    this.location = location;
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------
}
