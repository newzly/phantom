/*
 * Copyright 2013 - 2017 Outworkers Ltd.
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
package com.outworkers.phantom.keys

import com.outworkers.phantom.builder.query.engine.CQLQuery
import com.outworkers.phantom.builder.query.sasi.Analyzer
import com.outworkers.phantom.column.AbstractColumn

trait SASIIndex[A <: Analyzer[A]] {
  self: AbstractColumn[_] =>

  def analyzer: A

  def analyzerOptions: CQLQuery = analyzer.qb

  //abstract override def isSASI: Boolean = true
}

