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

package com.google.fhir.codegen

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

/** Generates a `SearchParam.kt` file with the sealed interface and concrete search param types. */
object SearchParamFileSpecGenerator {
  private val searchParamTypes =
    listOf(
      "Number" to "number",
      "Date" to "date",
      "String" to "string",
      "Token" to "token",
      "Reference" to "reference",
      "Composite" to "composite",
      "Quantity" to "quantity",
      "Uri" to "uri",
      "Special" to "special",
    )

  fun generate(packageName: String): FileSpec {
    return FileSpec.builder(packageName, "SearchParam")
      .addType(
        TypeSpec.interfaceBuilder("SearchParam")
          .addModifiers(KModifier.PUBLIC, KModifier.SEALED)
          .addKdoc("Base type for typed FHIR search parameters.")
          .addProperty(
            PropertySpec.builder("paramName", kotlin.String::class)
              .addModifiers(KModifier.PUBLIC)
              .addKdoc("The name of the search parameter as used in search URLs.")
              .build()
          )
          .build()
      )
      .apply {
        for ((name, fhirType) in searchParamTypes) {
          addType(
            TypeSpec.classBuilder("${name}SearchParam")
              .addModifiers(KModifier.PUBLIC)
              .addSuperinterface(ClassName(packageName, "SearchParam"))
              .addKdoc("A search parameter of type `$fhirType`.")
              .primaryConstructor(
                FunSpec.constructorBuilder().addParameter("paramName", kotlin.String::class).build()
              )
              .addProperty(
                PropertySpec.builder("paramName", kotlin.String::class)
                  .addModifiers(KModifier.OVERRIDE, KModifier.PUBLIC)
                  .initializer("paramName")
                  .build()
              )
              .build()
          )
        }
      }
      .build()
  }
}
