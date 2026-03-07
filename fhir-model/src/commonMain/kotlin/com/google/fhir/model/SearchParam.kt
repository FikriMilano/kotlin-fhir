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

package com.google.fhir.model

/** Base type for typed FHIR search parameters. */
public sealed interface SearchParam {
  /** The name of the search parameter as used in search URLs. */
  public val paramName: String
}

/** A search parameter of type `number`. */
public class NumberSearchParam(override val paramName: String) : SearchParam

/** A search parameter of type `date`. */
public class DateSearchParam(override val paramName: String) : SearchParam

/** A search parameter of type `string`. */
public class StringSearchParam(override val paramName: String) : SearchParam

/** A search parameter of type `token`. */
public class TokenSearchParam(override val paramName: String) : SearchParam

/** A search parameter of type `reference`. */
public class ReferenceSearchParam(override val paramName: String) : SearchParam

/** A search parameter of type `composite`. */
public class CompositeSearchParam(override val paramName: String) : SearchParam

/** A search parameter of type `quantity`. */
public class QuantitySearchParam(override val paramName: String) : SearchParam

/** A search parameter of type `uri`. */
public class UriSearchParam(override val paramName: String) : SearchParam

/** A search parameter of type `special`. */
public class SpecialSearchParam(override val paramName: String) : SearchParam
