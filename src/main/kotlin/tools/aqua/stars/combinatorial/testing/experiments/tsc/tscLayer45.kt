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

import tools.aqua.stars.combinatorial.testing.experiments.hasHighTrafficDensity
import tools.aqua.stars.combinatorial.testing.experiments.hasLowTrafficDensity
import tools.aqua.stars.combinatorial.testing.experiments.hasMidTrafficDensity
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
fun tscLayer45() =
    tsc<Actor, TickData, Segment, TickDataUnitSeconds, TickDataDifferenceSeconds>(
        "TSC Layer 4 & 5"
    ) {
      all("TSCRoot") {
        exclusive("Traffic Density") {
          leaf("High Traffic") { condition { ctx -> hasHighTrafficDensity.holds(ctx) } }
          leaf("Middle Traffic") { condition { ctx -> hasMidTrafficDensity.holds(ctx) } }
          leaf("Low Traffic") { condition { ctx -> hasLowTrafficDensity.holds(ctx) } }
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
