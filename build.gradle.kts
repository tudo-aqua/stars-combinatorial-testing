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

import java.net.URI

plugins {
  kotlin("jvm") version "2.2.20"
  application
  id("com.diffplug.spotless") version "8.0.0"
}

group = "tools.aqua"

version = "0.5"

val starsVersion = "1.0-sncs-cmbt-27-9e34966-SNAPSHOT"

repositories {
  mavenCentral()
  mavenLocal()

  maven { url = URI("https://central.sonatype.com/repository/maven-snapshots/") }
}

dependencies {
  testImplementation(kotlin("test"))
  implementation(group = "tools.aqua", name = "stars-core", version = starsVersion)
  implementation(group = "tools.aqua", name = "stars-logic-kcmftbl", version = starsVersion)
  implementation(group = "tools.aqua", name = "stars-data-av", version = starsVersion)
  implementation(group = "tools.aqua", name = "stars-importer-carla", version = starsVersion)
}

spotless {
  kotlin {
    licenseHeaderFile(rootProject.file("contrib/license-header.template.kt")).also {
      it.updateYearWithLatest(true)
    }
    ktfmt()
  }
  kotlinGradle {
    licenseHeaderFile(
            rootProject.file("contrib/license-header.template.kt"),
            "(import |@file|plugins |dependencyResolutionManagement|rootProject.name)",
        )
        .also { it.updateYearWithLatest(true) }
    ktfmt()
  }
}

tasks.test { useJUnitPlatform() }

val reproductionTest by
    tasks.registering(JavaExec::class) {
      group = "verification"
      description = "Runs the reproduction test."
      dependsOn(tasks.run.get().taskDependencies)

      mainClass.set("tools.aqua.stars.carla.experiments.Experiment")
      classpath = sourceSets.main.get().runtimeClasspath
      jvmArgs = listOf("-Xmx64g")
      args =
          listOf(
              // Configure input
              "--input",
              "./stars-reproduction-source/stars-experiments-data/simulation_runs",

              // Set minSegmentTicks filter
              "--minSegmentTicks",
              "11",

              // Sort seeds
              "--sorted",

              // Save results
              "--saveResults",

              // Run reproduction mode
              "--reproduction",
              "baseline",
          )
    }

val reproductionTestAll by
    tasks.registering(JavaExec::class) {
      group = "verification"
      description = "Runs the reproduction test."
      dependsOn(tasks.run.get().taskDependencies)

      mainClass.set("tools.aqua.stars.carla.experiments.Experiment")
      classpath = sourceSets.main.get().runtimeClasspath
      jvmArgs = listOf("-Xmx64g")
      args =
          listOf(
              // Configure input
              "--input",
              "./stars-reproduction-source/stars-experiments-data/simulation_runs",

              // Set minSegmentTicks filter
              "--minSegmentTicks",
              "11",

              // Set allEgo
              "--allEgo",

              // Sort seeds
              "--sorted",

              // Save results
              "--saveResults",

              // Run reproduction mode
              "--reproduction",
              "baseline-all",

              // Show memory usage
              "--showMemoryConsumption",
          )
    }

application {
  mainClass.set("tools.aqua.stars.combinatorial.testing.experiments.ExperimentKt")
  applicationDefaultJvmArgs = listOf("-Xmx40g", "-Xms2g")
}

kotlin { jvmToolchain(17) }
