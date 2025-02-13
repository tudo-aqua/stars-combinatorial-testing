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

import tools.aqua.stars.carla.experiments.isInJunction
import tools.aqua.stars.carla.experiments.isOnMultiLane
import tools.aqua.stars.carla.experiments.isOnSingleLane
import tools.aqua.stars.carla.experiments.noon
import tools.aqua.stars.carla.experiments.pedestrianCrossed
import tools.aqua.stars.carla.experiments.sunset
import tools.aqua.stars.carla.experiments.weatherClear
import tools.aqua.stars.carla.experiments.weatherCloudy
import tools.aqua.stars.carla.experiments.weatherHardRain
import tools.aqua.stars.carla.experiments.weatherMidRain
import tools.aqua.stars.carla.experiments.weatherSoftRain
import tools.aqua.stars.carla.experiments.weatherWet
import tools.aqua.stars.carla.experiments.weatherWetCloudy
import tools.aqua.stars.core.tsc.TSC
import tools.aqua.stars.core.tsc.builder.*
import tools.aqua.stars.data.av.dataclasses.*

/**
 * Returns the [TSC] with the dataclasses [Actor], [TickData], [Segment], [TickDataUnitSeconds], and
 * [TickDataDifferenceSeconds] that is used in this experiment.
 */
@Suppress("StringLiteralDuplication")
fun tscLayerPedestrianFlat() =
    tsc<Actor, TickData, Segment, TickDataUnitSeconds, TickDataDifferenceSeconds>(
        "$LAYER_PEDESTRIAN Flat") {
          optional("TSCRoot") {
            leaf("Clear") { condition { ctx -> ctx.weatherClear() } }
            leaf("Cloudy") { condition { ctx -> ctx.weatherCloudy() } }
            leaf("Wet") { condition { ctx -> ctx.weatherWet() } }
            leaf("Wet Cloudy") { condition { ctx -> ctx.weatherWetCloudy() } }
            leaf("Soft Rain") { condition { ctx -> ctx.weatherSoftRain() } }
            leaf("Mid Rain") { condition { ctx -> ctx.weatherMidRain() } }
            leaf("Hard Rain") { condition { ctx -> ctx.weatherHardRain() } }
            leaf("Junction") { condition { ctx -> isInJunction.holds(ctx) } }
            leaf("Pedestrian Crossed in Junction") { condition { ctx -> isInJunction.holds(ctx) && pedestrianCrossed.holds(ctx) } }
            leaf("Pedestrian Crossed on Multi-Lane") { condition { ctx -> isOnMultiLane.holds(ctx) && pedestrianCrossed.holds(ctx) } }
            leaf("Pedestrian Crossed on Single-Lane") { condition { ctx ->
              isOnSingleLane.holds(ctx) &&
                  pedestrianCrossed.holds(ctx) } }
            leaf("Multi-Lane") {
              condition { ctx ->
                isOnMultiLane.holds(
                    ctx)
              }
            }
            leaf("Single-Lane") {
              condition { ctx ->
                isOnSingleLane.holds(
                    ctx)
              }
            }
            leaf("Sunset") { condition { ctx -> ctx.sunset() } }
            leaf("Noon") { condition { ctx -> ctx.noon() } }
          }
        }
