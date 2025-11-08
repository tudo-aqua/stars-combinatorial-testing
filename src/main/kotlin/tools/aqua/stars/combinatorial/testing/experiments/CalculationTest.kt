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

package tools.aqua.stars.combinatorial.testing.experiments

import tools.aqua.stars.core.tsc.builder.tsc
import tools.aqua.stars.data.av.dataclasses.Actor
import tools.aqua.stars.data.av.dataclasses.Segment
import tools.aqua.stars.data.av.dataclasses.TickData
import tools.aqua.stars.data.av.dataclasses.TickDataDifferenceSeconds
import tools.aqua.stars.data.av.dataclasses.TickDataUnitSeconds

fun main() {
  val testTsc =
      tsc<Actor, TickData, Segment, TickDataUnitSeconds, TickDataDifferenceSeconds> {
        bounded("q_r", bounds = 1 to 2) {
          bounded("x", 0 to 2) {
            leaf("u") { condition { true } }
            leaf("v") { condition { true } }
          }
          exclusive("y") {
            condition { true }
            leaf("s") { condition { true } }
            leaf("t") { condition { true } }
          }
        }
      }

  (1..5).forEach { n ->
    println(
        "Possible combinations for '${testTsc.identifier}' for n=${n} is: ${
        testTsc.countAllPossibleNWayPredicateCombinations(
          n
        )  
      } with combinations ${testTsc.getAllPossibleNWayPredicateCombinations(n)}"
    )
  }
}
