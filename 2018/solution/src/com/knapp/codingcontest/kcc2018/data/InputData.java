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

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import com.knapp.codingcontest.kcc2018.warehouse.aisle.Aisle;
import com.knapp.codingcontest.kcc2018.warehouse.aisle.Position;

public class InputData {
  private static final String PATH_INPUT_DATA;
  static {
    try {
      PATH_INPUT_DATA = new File("./data").getCanonicalPath();
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  // ----------------------------------------------------------------------------

  private final String dataPath;

  private final Properties warehouseCharacteristics = new Properties();

  private final Set<String> productCodes = new TreeSet<String>();
  private final List<Order> orders = new LinkedList<Order>();
  private final Map<Container, Position> containers = new LinkedHashMap<Container, Position>();

  // ----------------------------------------------------------------------------

  public InputData() {
    this(InputData.PATH_INPUT_DATA);
  }

  public InputData(final String dataPath) {
    this.dataPath = dataPath;
  }

  // ----------------------------------------------------------------------------

  @Override
  public String toString() {
    return "InputData@" + dataPath + "[\n " + productCodes + ",\n " + orders + ",\n " + containers + "\n]";
  }

  // ----------------------------------------------------------------------------

  public void readData() throws IOException {
    readWarehouseCharacteristics();
    readProducts();
    readOrders();
    readContainers();
  }

  // ----------------------------------------------------------------------------

  public Properties getWarehouseCharacteristics() {
    return warehouseCharacteristics;
  }

  public final List<Order> getOrders() {
    return Collections.unmodifiableList(orders);
  }

  public final Map<Container, Position> getContainers() {
    return Collections.unmodifiableMap(containers);
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  private void readWarehouseCharacteristics() throws IOException {
    final Reader fr = new FileReader(fullFileName("warehouse-characteristics.properties"));
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(fr);
      warehouseCharacteristics.load(reader);
    } finally {
      close(reader);
      close(fr);
    }
  }

  // ----------------------------------------------------------------------------

  private void readProducts() throws IOException {
    final Reader fr = new FileReader(fullFileName("products.csv"));
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(fr);
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        line = line.trim();
        if ("".equals(line) || line.startsWith("#")) {
          continue;
        }
        // code;
        final String[] columns = splitCsv(line);
        final String productCode = columns[0];
        productCodes.add(productCode);
      }
    } finally {
      close(reader);
      close(fr);
    }
  }

  // ----------------------------------------------------------------------------

  private void readOrders() throws IOException {
    final Reader fr = new FileReader(fullFileName("orders.csv"));
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(fr);
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        line = line.trim();
        if ("".equals(line) || line.startsWith("#")) {
          continue;
        }
        // code;product;quantity;
        final String[] columns = splitCsv(line);
        final Order order = new Order(columns[0], columns[1], Integer.parseInt(columns[2]));
        orders.add(order);
      }
    } finally {
      close(reader);
      close(fr);
    }
  }

  // ----------------------------------------------------------------------------

  private void readContainers() throws IOException {
    final Reader fr = new FileReader(fullFileName("containers.csv"));
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(fr);
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        line = line.trim();
        if ("".equals(line) || line.startsWith("#")) {
          continue;
        }
        // code;[product-code];[quantity];ia;ilvl;iloc;side;ip;
        final String[] columns = splitCsv(line);
        final Container container = new Container(columns[0]);
        if (!"".equals(columns[1])) {
          final String productCode = columns[1];
          container.setProductCode(productCode);
          final int quantity = Integer.parseInt(columns[2]);
          container.setQuantity(quantity);
        }
        final int aisle = Integer.parseInt(columns[3]);
        // lvl
        final int distanceIntoAisle= Integer.parseInt(columns[5]);

        final Aisle.Side side = Aisle.Side.valueOf(columns[6]);
        // final int depth = Integer.parseInt(columns[7]);
        final Position position = new Position(aisle, distanceIntoAisle, side);
        containers.put(container, position);
      }
    } finally {
      close(reader);
      close(fr);
    }
  }

  // ----------------------------------------------------------------------------

  protected File fullFileName(final String fileName) {
    final String fullFileName = dataPath + File.separator + fileName;
    return new File(fullFileName);
  }

  protected void close(final Closeable closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (final IOException exception) {
        exception.printStackTrace(System.err);
      }
    }
  }

  // ----------------------------------------------------------------------------

  protected String[] splitCsv(final String line) {
    return line.split(";");
  }

  // ----------------------------------------------------------------------------
  // ............................................................................
}
