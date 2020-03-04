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

package com.knapp.codingcontest.kcc2018;

import com.knapp.codingcontest.kcc2018.data.InputData;
import com.knapp.codingcontest.kcc2018.solution.Solution;
import com.knapp.codingcontest.kcc2018.util.PrepareUpload;
import com.knapp.codingcontest.kcc2018.warehouse.Warehouse;

/**
 * ----------------------------------------------------------------------------
 * you may change any code you like
 *   => but changing the output may lead to invalid results!
 * ----------------------------------------------------------------------------
 */
public class Main {
  public static void main(final String... args) throws Exception {
    System.out.println("KNAPP Coding Contest 2018: STARTING...");

    System.out.println("# ... LOADING DATA ...");
    Warehouse warehouse = null;

    final InputData input = new InputData();
    input.readData();
    warehouse = new Warehouse(input);

    System.out.println("# ... RUN PICK/STORAGE OPERATIONS ...");
    final Solution solution = new Solution(warehouse);
    solution.runWarehouseOperations();

    System.out.println("# ... WRITING OUTPUT/RESULT ...");
    PrepareUpload.createZipFile(warehouse);
    System.out
        .println(">>> Created " + PrepareUpload.FILENAME_WAREHOUSE_OPERATIONS + " & " + PrepareUpload.FILENAME_UPLOAD_ZIP);

    System.out.println("KNAPP Coding Contest 2018: FINISHED");
  }

  // ============================================================================
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // ----------------------------------------------------------------------------
  // ............................................................................
  // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
}
