/*
 * Copyright 2023-2025 The STARS Carla Experiments Authors
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

import tools.aqua.stars.combinatorial.testing.experiments.changedLane
import tools.aqua.stars.combinatorial.testing.experiments.hasRelevantRedLight
import tools.aqua.stars.combinatorial.testing.experiments.hasStopSign
import tools.aqua.stars.combinatorial.testing.experiments.hasYieldSign
import tools.aqua.stars.combinatorial.testing.experiments.isInJunction
import tools.aqua.stars.combinatorial.testing.experiments.isOnMultiLane
import tools.aqua.stars.combinatorial.testing.experiments.isOnSingleLane
import tools.aqua.stars.combinatorial.testing.experiments.makesLeftTurn
import tools.aqua.stars.combinatorial.testing.experiments.makesNoTurn
import tools.aqua.stars.combinatorial.testing.experiments.makesRightTurn
import tools.aqua.stars.core.tsc.TSC
import tools.aqua.stars.core.tsc.builder.*
import tools.aqua.stars.data.av.dataclasses.*

/**
 * Returns the [TSC] with the dataclasses [Actor], [TickData], [Segment], [TickDataUnitSeconds], and
 * [TickDataDifferenceSeconds] that is used in this experiment.
 */
@Suppress("StringLiteralDuplication")
fun tscLayer12() =
    tsc<Actor, TickData, Segment, TickDataUnitSeconds, TickDataDifferenceSeconds>(
        "TSC Layer 1 & 2"
    ) {
      all("TSCRoot") {
        exclusive("Road Type") {
          all("Junction") {
            condition { ctx -> isInJunction.holds(ctx) }

            exclusive("Maneuver") {
              leaf("No Turn") { condition { ctx -> makesNoTurn.holds(ctx) } }
              leaf("Right Turn") { condition { ctx -> makesRightTurn.holds(ctx) } }
              leaf("Left Turn") { condition { ctx -> makesLeftTurn.holds(ctx) } }
            }
          }
          all("Multi-Lane") {
            condition { ctx ->
              isOnMultiLane.holds(
                  ctx,
                  ctx.segment.tickData.first().currentTick,
                  ctx.segment.primaryEntityId,
              )
            }

            exclusive("Maneuver") {
              leaf("Lane Change") { condition { ctx -> changedLane.holds(ctx) } }
              leaf("Lane Follow") { condition { ctx -> !changedLane.holds(ctx) } }
            }

            bounded("Stop Type", 0 to 1) {
              leaf("Has Red Light") { condition { ctx -> hasRelevantRedLight.holds(ctx) } }
            }
          }
          all("Single-Lane") {
            condition { ctx ->
              isOnSingleLane.holds(
                  ctx,
                  ctx.segment.tickData.first().currentTick,
                  ctx.segment.primaryEntityId,
              )
            }

            bounded("Stop Type", 0 to 1) {
              leaf("Has Stop Sign") { condition { ctx -> hasStopSign.holds(ctx) } }
              leaf("Has Yield Sign") { condition { ctx -> hasYieldSign.holds(ctx) } }
              leaf("Has Red Light") { condition { ctx -> hasRelevantRedLight.holds(ctx) } }
            }
          }
        }
      }
    }
