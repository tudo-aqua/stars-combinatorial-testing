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

package tools.aqua.stars.carla.experiments

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.split
import com.github.ajalt.clikt.parameters.types.int
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.zip.ZipFile
import kotlin.io.path.name
import kotlin.system.exitProcess
import tools.aqua.stars.carla.experiments.Experiment.EXIT_CODE_EQUAL_RESULTS
import tools.aqua.stars.carla.experiments.Experiment.EXIT_CODE_NORMAL
import tools.aqua.stars.carla.experiments.Experiment.EXIT_CODE_NO_RESULTS
import tools.aqua.stars.carla.experiments.Experiment.EXIT_CODE_UNEQUAL_RESULTS
import tools.aqua.stars.carla.experiments.tsc.tsc
import tools.aqua.stars.carla.experiments.tsc.tscLayer124Flat
import tools.aqua.stars.carla.experiments.tsc.tscLayer12Flat
import tools.aqua.stars.carla.experiments.tsc.tscLayer45Flat
import tools.aqua.stars.carla.experiments.tsc.tscLayer4Flat
import tools.aqua.stars.carla.experiments.tsc.tscLayerFullFlat
import tools.aqua.stars.carla.experiments.tsc.tscLayerPedestrianFlat
import tools.aqua.stars.core.evaluation.TSCEvaluation
import tools.aqua.stars.core.metric.metrics.evaluation.*
import tools.aqua.stars.core.metric.metrics.postEvaluation.*
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.baselineDirectory
import tools.aqua.stars.core.tsc.TSC
import tools.aqua.stars.data.av.dataclasses.*
import tools.aqua.stars.data.av.metrics.AverageVehiclesInEgosBlockMetric
import tools.aqua.stars.importer.carla.CarlaSimulationRunsWrapper
import tools.aqua.stars.importer.carla.loadSegments

/**
 * The [ExperimentConfiguration] configures all [CliktCommand]s that can be used for this experiment
 * and passes them to the STARS-framework.
 */
class ExperimentConfiguration : CliktCommand() {

  // region command line options
  private val allEgo: Boolean by
      option("--allEgo", help = "Whether to treat all vehicles as ego").flag(default = false)

  private val writePlots: Boolean by
      option("--writePlots", help = "Whether to write plots").flag(default = true)

  private val writePlotDataCSV: Boolean by
      option("--writePlotData", help = "Whether to write plot data to csv").flag(default = false)

  private val writeSerializedResults: Boolean by
      option("--saveResults", help = "Whether to save serialized results").flag(default = false)
  // endregion

  override fun run() {
    ApplicationConstantsHolder.executionCommand =
        """
        --allEgo=$allEgo
        --writePlots=$writePlots
        --writePlotData=$writePlotDataCSV
        --saveResults=$writeSerializedResults
    """
            .trimIndent()
    println("Executing with the following settings:")
    println(ApplicationConstantsHolder.executionCommand)

    downloadAndUnzipExperimentsData()

    val tscs =
        mutableListOf<TSC<Actor, TickData, Segment, TickDataUnitSeconds, TickDataDifferenceSeconds>>()

    tscs.addAll(tsc().buildProjections().also{ it.forEach { t ->  println("Size of ${t.identifier} flat: ${t.possibleTSCInstances}") }})
    tscs.addAll(listOf(
          tscLayer12Flat().also{ println("Size of 1+2 flat: ${it.possibleTSCInstances}") },
          tscLayer124Flat().also{ println("Size of 1+2+4 flat: ${it.possibleTSCInstances}") },
          tscLayer4Flat().also{ println("Size of 4 flat: ${it.possibleTSCInstances}") },
          tscLayer45Flat().also{ println("Size of 4+5 flat: ${it.possibleTSCInstances}") },
          tscLayerPedestrianFlat().also{ println("Size of Pedestrian flat: ${it.possibleTSCInstances}") },
          tscLayerFullFlat().also{ println("Size of Full flat: ${it.possibleTSCInstances}") }))

    println("Loading simulation runs...")
    val simulationRunsWrappers = getSimulationRuns()

    println("Loading segments...")
    val segments =
        loadSegments(
            useEveryVehicleAsEgo = allEgo,
            minSegmentTickCount = 11,
            orderFilesBySeed = true,
            simulationRunsWrappers = simulationRunsWrappers,
        )

    TSCEvaluation(
                tscList = tscs,
                writePlots = writePlots,
                writePlotDataCSV = writePlotDataCSV,
                writeSerializedResults = writeSerializedResults)
            .apply {
              registerMetricProviders(ValidTSCInstancesPerTSCMetric<Actor, TickData, Segment, TickDataUnitSeconds, TickDataDifferenceSeconds>())
              runEvaluation(segments = segments)
            }
  }

  /**
   * Checks if the experiments data is available. Otherwise, it is downloaded and extracted to the
   * correct folder.
   */
  private fun downloadAndUnzipExperimentsData() {
    val reproductionSourceFolderName = "stars-reproduction-source"
    val reproductionSourceZipFile = "$reproductionSourceFolderName.zip"

    if (File(reproductionSourceFolderName).exists()) {
      println("The 'stars-reproduction-source' already exists")
      return
    }

    if (!File(reproductionSourceZipFile).exists()) {
      println("Start with downloading the experiments data. This may take a while.")
      URL("https://zenodo.org/record/8131947/files/stars-reproduction-source.zip?download=1")
          .openStream()
          .use { Files.copy(it, Paths.get(reproductionSourceZipFile)) }
    }

    check(File(reproductionSourceZipFile).exists()) {
      "After downloading the file '$reproductionSourceZipFile' does not exist."
    }

    println("Extracting experiments data from zip file.")
    extractZipFile(zipFile = File(reproductionSourceZipFile), outputDir = File("."))

    check(File(reproductionSourceFolderName).exists()) { "Error unzipping simulation data." }
    check(File("./$reproductionSourceFolderName").totalSpace > 0) {
      "There was an error while downloading/extracting the simulation data. The test zip file is missing."
    }
  }

  private fun getSimulationRuns(): List<CarlaSimulationRunsWrapper> =
      File("./stars-reproduction-source/stars-experiments-data/simulation_runs").let { file ->
        file
            .walk()
            .filter {
              it.isDirectory && it != file
            }
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

  /**
   * Extract a zip file into any directory.
   *
   * @param zipFile src zip file
   * @param outputDir directory to extract into. There will be new folder with the zip's name inside
   *   [outputDir] directory.
   * @return the extracted directory i.e.
   */
  private fun extractZipFile(zipFile: File, outputDir: File): File? {
    ZipFile(zipFile).use { zip ->
      zip.entries().asSequence().forEach { entry ->
        zip.getInputStream(entry).use { input ->
          if (entry.isDirectory) File(outputDir, entry.name).also { it.mkdirs() }
          else
              File(outputDir, entry.name)
                  .also { it.parentFile.mkdirs() }
                  .outputStream()
                  .use { output -> input.copyTo(output) }
        }
      }
    }
    return outputDir
  }
}
