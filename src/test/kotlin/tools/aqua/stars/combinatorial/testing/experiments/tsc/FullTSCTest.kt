/*
 * Copyright 2025 The STARS Carla Experiments Authors
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tools.aqua.stars.combinatorial.testing.experiments.tsc

import java.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertEquals

class FullTSCTest {
  /** Test correct calculation of possible 1-way combinations based on Full TSC. */
  @Test
  fun `Test correct calculation of possible 1-way combinations based on Full TSC`() {
    val tsc = tscLayerFull()

    assertEquals(BigInteger("23"), tsc.countAllPossibleNWayPredicateCombinations(1))
    assertEquals(23, tsc.getAllPossibleNWayPredicateCombinations(1).size)
  }

  /** Test correct calculation of possible 2-way combinations based on Full TSC. */
  @Test
  fun `Test correct calculation of possible 2-way combinations based on Full TSC`() {
    val tsc = tscLayerFull()

    assertEquals(253, tsc.getAllPossibleNWayPredicateCombinations(2).size)
    assertEquals(BigInteger("253"), tsc.countAllPossibleNWayPredicateCombinations(2))
  }

  /** Test correct calculation of possible 3-way combinations based on Full TSC. */
  @Test
  fun `Test correct calculation of possible 3-way combinations based on Full TSC`() {
    val tsc = tscLayerFull()

    assertEquals(BigInteger("1771"), tsc.countAllPossibleNWayPredicateCombinations(3))
    assertEquals(1771, tsc.getAllPossibleNWayPredicateCombinations(3).size)
  }

  /** Test correct calculation of possible 4-way combinations based on Full TSC. */
  @Test
  fun `Test correct calculation of possible 4-way combinations based on Full TSC`() {
    val tsc = tscLayerFull()

    assertEquals(BigInteger("8855"), tsc.countAllPossibleNWayPredicateCombinations(4))
    assertEquals(8855, tsc.getAllPossibleNWayPredicateCombinations(4).size)
  }

  /** Test correct calculation of possible 5-way combinations based on Full TSC. */
  @Test
  fun `Test correct calculation of possible 5-way combinations based on Full TSC`() {
    val tsc = tscLayerFull()

    assertEquals(BigInteger("33649"), tsc.countAllPossibleNWayPredicateCombinations(5))
    assertEquals(33649, tsc.getAllPossibleNWayPredicateCombinations(5).size)
  }

  /** Test correct calculation of possible 6-way combinations based on Full TSC. */
  @Test
  fun `Test correct calculation of possible 6-way combinations based on Full TSC`() {
    val tsc = tscLayerFull()

    assertEquals(BigInteger("100947"), tsc.countAllPossibleNWayPredicateCombinations(6))
    assertEquals(100947, tsc.getAllPossibleNWayPredicateCombinations(6).size)
  }
}
