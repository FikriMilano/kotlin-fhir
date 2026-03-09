/*
 * Copyright 2025-2026 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.fhir.model.test

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.io.File
import kotlin.reflect.KProperty1
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.memberProperties
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
private data class SearchParameterDef(
  val code: String,
  val base: List<String> = emptyList(),
  val type: String,
)

private val json = Json { ignoreUnknownKeys = true }

private val fhirTypeToClassName: Map<String, String> =
  mapOf(
    "number" to "NumberSearchParam",
    "date" to "DateSearchParam",
    "string" to "StringSearchParam",
    "token" to "TokenSearchParam",
    "reference" to "ReferenceSearchParam",
    "composite" to "CompositeSearchParam",
    "quantity" to "QuantitySearchParam",
    "uri" to "UriSearchParam",
    "special" to "SpecialSearchParam",
  )

private data class SearchParamTestSuite(
  val fhirVersion: String,
  val corePackageSubdirectory: String,
  val modelPackage: String,
)

private fun loadSearchParams(
  corePackageSubdirectory: String
): Map<String, List<SearchParameterDef>> {
  val rootDir = System.getProperty("projectRootDir")
  return File("$rootDir/third_party/$corePackageSubdirectory")
    .listFiles()!!
    .asSequence()
    .filter { it.isFile && it.name.matches("SearchParameter-.*\\.json".toRegex()) }
    .map { json.decodeFromString<SearchParameterDef>(it.readText()) }
    .filter { it.base.isNotEmpty() }
    .flatMap { searchParam -> searchParam.base.map { resource -> resource to searchParam } }
    .groupBy({ it.first }, { it.second })
}

private fun codeToConstantName(code: String): String = code.replace("-", "_").uppercase()

class SearchParamTest :
  FunSpec({
    listOf(
        SearchParamTestSuite("R4", "hl7.fhir.r4.core/package", "com.google.fhir.model.r4"),
        SearchParamTestSuite("R4B", "hl7.fhir.r4b.core/package", "com.google.fhir.model.r4b"),
        SearchParamTestSuite("R5", "hl7.fhir.r5.core/package", "com.google.fhir.model.r5"),
      )
      .forEach { testSuite ->
        val searchParamsByResource = loadSearchParams(testSuite.corePackageSubdirectory)
        val searchParamInterface = Class.forName("${testSuite.modelPackage}.SearchParam")

        context("${testSuite.fhirVersion} search params should match definitions") {
          searchParamsByResource.forEach { (resourceName, expectedParams) ->
            val resourceClass =
              try {
                Class.forName("${testSuite.modelPackage}.$resourceName").kotlin
              } catch (_: ClassNotFoundException) {
                null
              }

            // Skip resources that don't have generated classes or are abstract
            if (resourceClass == null) return@forEach
            if (java.lang.reflect.Modifier.isAbstract(resourceClass.java.modifiers)) return@forEach

            val companion = resourceClass.companionObjectInstance ?: return@forEach

            @Suppress("UNCHECKED_CAST")
            val companionProperties =
              companion::class.memberProperties as Collection<KProperty1<Any, *>>

            val searchParamProperties =
              companionProperties
                .filter { prop ->
                  try {
                    searchParamInterface.isInstance(prop.get(companion))
                  } catch (_: Exception) {
                    false
                  }
                }
                .associate { prop ->
                  val value = prop.get(companion)!!
                  prop.name to value
                }

            val dedupedExpected = expectedParams.distinctBy { it.code }.sortedBy { it.code }

            test("$resourceName should have ${dedupedExpected.size} search params") {
              searchParamProperties.size.shouldBe(dedupedExpected.size)
            }

            dedupedExpected.forEach { expected ->
              val constantName = codeToConstantName(expected.code)
              test(
                "$resourceName.$constantName should be ${expected.type} with paramName '${expected.code}'"
              ) {
                val actual =
                  searchParamProperties[constantName]
                    ?: error(
                      "Missing search param constant $constantName on $resourceName. Available: ${searchParamProperties.keys}"
                    )
                val paramName =
                  actual::class.memberProperties.first { it.name == "paramName" }.call(actual)
                    as String
                paramName.shouldBe(expected.code)
                val expectedClassName =
                  fhirTypeToClassName[expected.type]
                    ?: error("Unknown search param type: ${expected.type}")
                actual::class.simpleName.shouldBe(expectedClassName)
              }
            }
          }
        }
      }
  })
