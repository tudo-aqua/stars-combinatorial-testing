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

import tools.aqua.stars.combinatorial.testing.experiments.isInJunction
import tools.aqua.stars.combinatorial.testing.experiments.isOnMultiLane
import tools.aqua.stars.combinatorial.testing.experiments.isOnSingleLane
import tools.aqua.stars.combinatorial.testing.experiments.pedestrianCrossed
import tools.aqua.stars.combinatorial.testing.experiments.timeDay
import tools.aqua.stars.combinatorial.testing.experiments.timeNight
import tools.aqua.stars.combinatorial.testing.experiments.weatherClear
import tools.aqua.stars.combinatorial.testing.experiments.weatherRain
import tools.aqua.stars.core.tsc.TSC
import tools.aqua.stars.core.tsc.builder.tsc
import tools.aqua.stars.data.av.dataclasses.*

/**
 * Returns the [TSC] with the dataclasses [Actor], [TickData], [Segment], [TickDataUnitSeconds], and
 * [TickDataDifferenceSeconds] that is used in this experiment.
 */
@Suppress("StringLiteralDuplication")
fun tscLayerPedestrian() =
    tsc<Actor, TickData, Segment, TickDataUnitSeconds, TickDataDifferenceSeconds>(
        "TSC Pedestrian"
    ) {
      all("TSCRoot") {
        exclusive("Road Type") {
          all("Junction") {
            condition { ctx -> isInJunction.holds(ctx) }

            optional("Dynamic Relation") {
              leaf("Pedestrian Crossed") { condition { ctx -> pedestrianCrossed.holds(ctx) } }
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

            optional("Dynamic Relation") {
              leaf("Pedestrian Crossed") { condition { ctx -> pedestrianCrossed.holds(ctx) } }
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

            optional("Dynamic Relation") {
              leaf("Pedestrian Crossed") { condition { ctx -> pedestrianCrossed.holds(ctx) } }
            }
          }
        }

        exclusive("Weather") {
          leaf("Clear") { condition { ctx -> ctx.weatherClear() } }
          leaf("Rain") { condition { ctx -> ctx.weatherRain() } }
        }

        exclusive("Time of Day") {
          leaf("Day") { condition { ctx -> ctx.timeNight() } }
          leaf("Night") { condition { ctx -> ctx.timeDay() } }
        }
      }
    }
