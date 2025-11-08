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

package tools.aqua.stars.combinatorial.testing.experiments.flattsc

import tools.aqua.stars.combinatorial.testing.experiments.changedLane
import tools.aqua.stars.combinatorial.testing.experiments.follows
import tools.aqua.stars.combinatorial.testing.experiments.hasHighTrafficDensity
import tools.aqua.stars.combinatorial.testing.experiments.hasLowTrafficDensity
import tools.aqua.stars.combinatorial.testing.experiments.hasMidTrafficDensity
import tools.aqua.stars.combinatorial.testing.experiments.hasOvertaken
import tools.aqua.stars.combinatorial.testing.experiments.hasRelevantRedLight
import tools.aqua.stars.combinatorial.testing.experiments.hasStopSign
import tools.aqua.stars.combinatorial.testing.experiments.hasYieldSign
import tools.aqua.stars.combinatorial.testing.experiments.isInJunction
import tools.aqua.stars.combinatorial.testing.experiments.isOnMultiLane
import tools.aqua.stars.combinatorial.testing.experiments.isOnSingleLane
import tools.aqua.stars.combinatorial.testing.experiments.makesLeftTurn
import tools.aqua.stars.combinatorial.testing.experiments.makesNoTurn
import tools.aqua.stars.combinatorial.testing.experiments.makesRightTurn
import tools.aqua.stars.combinatorial.testing.experiments.mustYield
import tools.aqua.stars.combinatorial.testing.experiments.oncoming
import tools.aqua.stars.combinatorial.testing.experiments.pedestrianCrossed
import tools.aqua.stars.combinatorial.testing.experiments.timeDay
import tools.aqua.stars.combinatorial.testing.experiments.timeNight
import tools.aqua.stars.combinatorial.testing.experiments.weatherClear
import tools.aqua.stars.combinatorial.testing.experiments.weatherRain
import tools.aqua.stars.core.tsc.TSC
import tools.aqua.stars.core.tsc.builder.*
import tools.aqua.stars.data.av.dataclasses.*

/**
 * Returns the [TSC] with the dataclasses [Actor], [TickData], [Segment], [TickDataUnitSeconds], and
 * [TickDataDifferenceSeconds] that is used in this experiment.
 */
@Suppress("StringLiteralDuplication")
fun tscLayerFullFlat() =
    tsc<Actor, TickData, Segment, TickDataUnitSeconds, TickDataDifferenceSeconds>("TSC Full Flat") {
      optional("TSCRoot") {
        leaf("Day") { condition { ctx -> ctx.timeDay() } }
        leaf("Night") { condition { ctx -> ctx.timeNight() } }

        leaf("Clear") { condition { ctx -> ctx.weatherClear() } }
        leaf("Rain") { condition { ctx -> ctx.weatherRain() } }

        leaf("Multi-Lane") { condition { ctx -> isOnMultiLane.holds(ctx) } }
        leaf("Single-Lane") { condition { ctx -> isOnSingleLane.holds(ctx) } }
        leaf("Junction") { condition { ctx -> isInJunction.holds(ctx) } }

        leaf("High Traffic") { condition { ctx -> hasHighTrafficDensity.holds(ctx) } }
        leaf("Middle Traffic") { condition { ctx -> hasMidTrafficDensity.holds(ctx) } }
        leaf("Low Traffic") { condition { ctx -> hasLowTrafficDensity.holds(ctx) } }

        leaf("Has Red Light") {
          condition { ctx ->
            (isOnMultiLane.holds(ctx) || isOnSingleLane.holds(ctx)) &&
                hasRelevantRedLight.holds(ctx)
          }
        }

        leaf("Lane Change") {
          condition { ctx -> isOnMultiLane.holds(ctx) && changedLane.holds(ctx) }
        }
        leaf("Lane Follow") {
          condition { ctx -> isOnMultiLane.holds(ctx) && !changedLane.holds(ctx) }
        }

        leaf("Following Leading Vehicle") {
          condition { ctx ->
            ctx.entityIds.any { otherVehicleId -> follows.holds(ctx, entityId2 = otherVehicleId) }
          }
        }

        leaf("Oncoming traffic") {
          condition { ctx ->
            (isOnMultiLane.holds(ctx) || isOnSingleLane.holds(ctx)) &&
                ctx.entityIds.any { otherVehicleId ->
                  oncoming.holds(ctx, entityId2 = otherVehicleId)
                }
          }
        }

        leaf("Overtaking") {
          condition { ctx -> isOnMultiLane.holds(ctx) && hasOvertaken.holds(ctx) }
        }

        leaf("Pedestrian Crossed") { condition { ctx -> pedestrianCrossed.holds(ctx) } }

        leaf("Has Stop Sign") {
          condition { ctx -> isOnSingleLane.holds(ctx) && hasStopSign.holds(ctx) }
        }

        leaf("Has Yield Sign") {
          condition { ctx -> isOnSingleLane.holds(ctx) && hasYieldSign.holds(ctx) }
        }

        leaf("Must Yield") {
          condition { ctx ->
            isInJunction.holds(ctx) &&
                ctx.entityIds.any { otherVehicleId ->
                  mustYield.holds(ctx, entityId2 = otherVehicleId)
                }
          }
        }

        leaf("No Turn") { condition { ctx -> isInJunction.holds(ctx) && makesNoTurn.holds(ctx) } }
        leaf("Right Turn") {
          condition { ctx -> isInJunction.holds(ctx) && makesRightTurn.holds(ctx) }
        }
        leaf("Left Turn") {
          condition { ctx -> isInJunction.holds(ctx) && makesLeftTurn.holds(ctx) }
        }
      }
    }
