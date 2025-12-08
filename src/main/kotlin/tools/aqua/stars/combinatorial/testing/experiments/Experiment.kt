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

package tools.aqua.stars.combinatorial.testing.experiments

import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.zip.ZipFile
import kotlin.io.path.name
import tools.aqua.stars.combinatorial.testing.experiments.flattsc.tscLayer124Flat
import tools.aqua.stars.combinatorial.testing.experiments.flattsc.tscLayerFullFlat
import tools.aqua.stars.combinatorial.testing.experiments.tsc.tscLayer124
import tools.aqua.stars.combinatorial.testing.experiments.tsc.tscLayerFull
import tools.aqua.stars.core.evaluation.TSCEvaluation
import tools.aqua.stars.core.metrics.evaluation.InvalidTSCInstancesPerTSCMetric
import tools.aqua.stars.core.metrics.evaluation.NWayFeatureCombinationsPerTSCMetric
import tools.aqua.stars.core.metrics.evaluation.ValidTSCInstancesPerTSCMetric
import tools.aqua.stars.core.tsc.TSC
import tools.aqua.stars.data.av.dataclasses.*
import tools.aqua.stars.importer.carla.CarlaSimulationRunsWrapper
import tools.aqua.stars.importer.carla.loadSegments

fun main() {
  val tscs =
      mutableListOf<TSC<Actor, TickData, Segment, TickDataUnitSeconds, TickDataDifferenceSeconds>>()

  tscs.addAll(
      listOf(
          tscLayer124().also { println("Size of 1+2+4: ${it.instanceCount}") },
          tscLayerFull().also { println("Size of Full: ${it.instanceCount}") },
          tscLayer124Flat().also { println("Size of 1+2+4 flat: ${it.instanceCount}") },
          tscLayerFullFlat().also { println("Size of Full flat: ${it.instanceCount}") },
      )
  )

  tscs.forEach { tsc ->
    (1..25).forEach { n ->
      println(
          "Possible combinations for '${tsc.identifier}' for n=${n} is: ${tsc.countAllPossibleNWayPredicateCombinations(n)}"
      )
    }
  }

  println("Loading simulation runs...")
  val simulationRunsWrappers = getSimulationRuns()

  println("Loading segments...")
  val segments =
      loadSegments(
          useEveryVehicleAsEgo = true,
          useFirstVehicleAsEgo = false,
          minSegmentTickCount = 11,
          orderFilesBySeed = true,
          simulationRunsWrappers = simulationRunsWrappers,
      )

  TSCEvaluation(
          tscList = tscs,
          writePlots = true,
          writePlotDataCSV = true,
          writeSerializedResults = false,
      )
      .apply {
        registerMetricProviders(ValidTSCInstancesPerTSCMetric(), InvalidTSCInstancesPerTSCMetric())
        (1..6).forEach { registerMetricProviders(NWayFeatureCombinationsPerTSCMetric(it)) }
        runEvaluation(segments = segments)
      }
}

private fun getSimulationRuns(): List<CarlaSimulationRunsWrapper> =
    File("./stars-experiments-data/simulation_runs").let { file ->
      file
          .walk()
          .filter { it.isDirectory && it != file }
          .toList()
          .mapNotNull { mapFolder ->
            var staticFile: Path? = null
            val dynamicFiles = mutableListOf<Path>()
            mapFolder.walk().forEach { mapFile ->
              if (mapFile.nameWithoutExtension.contains("static_data")) {
                staticFile = mapFile.toPath()
              }
              if (mapFile.nameWithoutExtension.contains("dynamic_data")) {
                dynamicFiles.add(mapFile.toPath())
              }
            }

            if (staticFile == null || dynamicFiles.isEmpty()) {
              return@mapNotNull null
            }

            dynamicFiles.sortBy {
              "_seed([0-9]{1,4})".toRegex().find(it.fileName.name)?.groups?.get(1)?.value?.toInt()
                  ?: 0
            }
            return@mapNotNull CarlaSimulationRunsWrapper(staticFile, dynamicFiles)
          }
    }
