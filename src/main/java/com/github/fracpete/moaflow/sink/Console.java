/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Console.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.moaflow.sink;

/**
 * Just outputs the object on stdout.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Console
  extends AbstractSink<Object> {

  protected int everyNth;

  protected int counter;

  public Console() {
    setEveryNth(1);
  }

  public void setEveryNth(int value) {
    everyNth = value;
    counter = 0;
  }

  public int getEveryNth() {
    return everyNth;
  }

  protected void doProcess(Object input) {
    counter++;
    if (counter == everyNth) {
      System.out.println(input);
      counter = 0;
    }
  }
}
