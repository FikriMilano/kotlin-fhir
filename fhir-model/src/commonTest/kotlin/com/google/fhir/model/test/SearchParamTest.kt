/*
 * Copyright 2025 Google LLC
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

import com.google.fhir.model.DateSearchParam
import com.google.fhir.model.ReferenceSearchParam
import com.google.fhir.model.StringSearchParam
import com.google.fhir.model.TokenSearchParam
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class SearchParamTest :
  FunSpec({
    context("R4 Patient search params") {
      test("NAME should be a StringSearchParam with paramName 'name'") {
        com.google.fhir.model.r4.Patient.NAME.shouldBeInstanceOf<StringSearchParam>()
        com.google.fhir.model.r4.Patient.NAME.paramName.shouldBe("name")
      }

      test("ACTIVE should be a TokenSearchParam with paramName 'active'") {
        com.google.fhir.model.r4.Patient.ACTIVE.shouldBeInstanceOf<TokenSearchParam>()
        com.google.fhir.model.r4.Patient.ACTIVE.paramName.shouldBe("active")
      }

      test("BIRTHDATE should be a DateSearchParam with paramName 'birthdate'") {
        com.google.fhir.model.r4.Patient.BIRTHDATE.shouldBeInstanceOf<DateSearchParam>()
        com.google.fhir.model.r4.Patient.BIRTHDATE.paramName.shouldBe("birthdate")
      }

      test("GENERAL_PRACTITIONER should be a ReferenceSearchParam") {
        com.google.fhir.model.r4.Patient.GENERAL_PRACTITIONER
          .shouldBeInstanceOf<ReferenceSearchParam>()
        com.google.fhir.model.r4.Patient.GENERAL_PRACTITIONER.paramName.shouldBe(
          "general-practitioner"
        )
      }
    }

    context("Shared search params across resources") {
      test("GIVEN should exist on both Patient and Practitioner as StringSearchParam") {
        com.google.fhir.model.r4.Patient.GIVEN.shouldBeInstanceOf<StringSearchParam>()
        com.google.fhir.model.r4.Practitioner.GIVEN.shouldBeInstanceOf<StringSearchParam>()
        com.google.fhir.model.r4.Patient.GIVEN.paramName.shouldBe("given")
        com.google.fhir.model.r4.Practitioner.GIVEN.paramName.shouldBe("given")
      }
    }

    context("Search params exist across FHIR versions") {
      test("R4B Patient.NAME should be a StringSearchParam") {
        com.google.fhir.model.r4b.Patient.NAME.shouldBeInstanceOf<StringSearchParam>()
        com.google.fhir.model.r4b.Patient.NAME.paramName.shouldBe("name")
      }

      test("R5 Patient.NAME should be a StringSearchParam") {
        com.google.fhir.model.r5.Patient.NAME.shouldBeInstanceOf<StringSearchParam>()
        com.google.fhir.model.r5.Patient.NAME.paramName.shouldBe("name")
      }
    }
  })
