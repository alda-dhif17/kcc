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

public enum Institute {
  Altes_Gymnasium_Leoben, //
  HTL_Bulme_Graz, //
  HTL_Kaindorf, //
  HTL_Pinkafeld, //
  HTL_Rennweg, //
  HTL_Villach, //
  HTL_Weiz, //
  SonstigeSchule, //
  //
  FH_Campus_02, //
  FH_Joanneum, //
  FH_Technikum_Wien, //
  FH_Wr_Neustadt, //
  Montanuniversitaet, //
  TU_Graz, //
  TU_Wien, //
  Uni_Goettingen, //
  Universitaet_Klagenfurt, //
  Universitaet_Wien, //
  Sonstige, //
  ;

  public static Institute find(final String _institute) {
    return Institute.valueOf(_institute);
  }
}
