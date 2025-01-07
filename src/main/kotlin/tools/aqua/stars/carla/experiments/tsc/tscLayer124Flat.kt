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

package tools.aqua.stars.carla.experiments.tsc

import tools.aqua.stars.carla.experiments.changedLane
import tools.aqua.stars.carla.experiments.follows
import tools.aqua.stars.carla.experiments.hasHighTrafficDensity
import tools.aqua.stars.carla.experiments.hasLowTrafficDensity
import tools.aqua.stars.carla.experiments.hasMidTrafficDensity
import tools.aqua.stars.carla.experiments.hasOvertaken
import tools.aqua.stars.carla.experiments.hasRelevantRedLight
import tools.aqua.stars.carla.experiments.hasStopSign
import tools.aqua.stars.carla.experiments.hasYieldSign
import tools.aqua.stars.carla.experiments.isInJunction
import tools.aqua.stars.carla.experiments.isOnMultiLane
import tools.aqua.stars.carla.experiments.isOnSingleLane
import tools.aqua.stars.carla.experiments.makesLeftTurn
import tools.aqua.stars.carla.experiments.makesNoTurn
import tools.aqua.stars.carla.experiments.makesRightTurn
import tools.aqua.stars.carla.experiments.mustYield
import tools.aqua.stars.carla.experiments.oncoming
import tools.aqua.stars.carla.experiments.pedestrianCrossed
import tools.aqua.stars.core.tsc.TSC
import tools.aqua.stars.core.tsc.builder.*
import tools.aqua.stars.data.av.dataclasses.*

/**
 * Returns the [TSC] with the dataclasses [Actor], [TickData], [Segment], [TickDataUnitSeconds], and
 * [TickDataDifferenceSeconds] that is used in this experiment.
 */
@Suppress("StringLiteralDuplication")
fun tscLayer124Flat() =
    tsc<Actor, TickData, Segment, TickDataUnitSeconds, TickDataDifferenceSeconds> {
      optional("TSCRoot") {
        leaf("Junction") { condition { ctx -> isInJunction.holds(ctx) } }
        leaf("Pedestrian Crossed") { condition { ctx -> pedestrianCrossed.holds(ctx) } }
        leaf("Must Yield") {
          condition { ctx ->
            ctx.entityIds.any { otherVehicleId -> mustYield.holds(ctx, entityId2 = otherVehicleId) }
          }
        }
        leaf("Following Leading Vehicle") {
          condition { ctx ->
            ctx.entityIds.any { otherVehicleId -> follows.holds(ctx, entityId2 = otherVehicleId) }
          }
        }
        leaf("No Turn") { condition { ctx -> makesNoTurn.holds(ctx) } }
        leaf("Right Turn") { condition { ctx -> makesRightTurn.holds(ctx) } }
        leaf("Left Turn") { condition { ctx -> makesLeftTurn.holds(ctx) } }
        leaf("Multi-Lane") {
          condition { ctx ->
            isOnMultiLane.holds(
                ctx, ctx.segment.tickData.first().currentTick, ctx.segment.primaryEntityId)
          }
        }
        leaf("Oncoming traffic") {
          condition { ctx ->
            ctx.entityIds.any { otherVehicleId -> oncoming.holds(ctx, entityId2 = otherVehicleId) }
          }
        }
        leaf("Overtaking") { condition { ctx -> hasOvertaken.holds(ctx) } }
        leaf("Lane Change") { condition { ctx -> changedLane.holds(ctx) } }
        leaf("Lane Follow") { condition { ctx -> !changedLane.holds(ctx) } }
        leaf("Has Red Light") { condition { ctx -> hasRelevantRedLight.holds(ctx) } }
        leaf("Single-Lane") {
          condition { ctx ->
            isOnSingleLane.holds(
                ctx, ctx.segment.tickData.first().currentTick, ctx.segment.primaryEntityId)
          }
        }
        leaf("Has Stop Sign") { condition { ctx -> hasStopSign.holds(ctx) } }
        leaf("Has Yield Sign") { condition { ctx -> hasYieldSign.holds(ctx) } }
        leaf("High Traffic") { condition { ctx -> hasHighTrafficDensity.holds(ctx) } }
        leaf("Middle Traffic") { condition { ctx -> hasMidTrafficDensity.holds(ctx) } }
        leaf("Low Traffic") { condition { ctx -> hasLowTrafficDensity.holds(ctx) } }
      }
    }
