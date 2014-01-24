/*
 * Copyright 2013 newzly ltd.
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
package com
package newzly

package phantom

import scala.collection.JavaConverters._
import com.datastax.driver.core.querybuilder.{ Assignment, QueryBuilder, Clause }
import com.datastax.driver.core.Row

abstract class AbstractQueryColumn[RR: CassandraPrimitive](col: Column[RR]) {

  def eqs(value: RR): Clause = QueryBuilder.eq(col.name, CassandraPrimitive[RR].toCType(value))
  def in[L <% Traversable[RR]](vs: L) = QueryBuilder.in(col.name, vs.map(CassandraPrimitive[RR].toCType).toSeq: _*)
  def gt(value: RR): Clause = QueryBuilder.gt(col.name, CassandraPrimitive[RR].toCType(value))
  def gte(value: RR): Clause = QueryBuilder.gte(col.name, CassandraPrimitive[RR].toCType(value))
  def lt(value: RR): Clause = QueryBuilder.lt(col.name, CassandraPrimitive[RR].toCType(value))
  def lte(value: RR): Clause = QueryBuilder.lte(col.name, CassandraPrimitive[RR].toCType(value))
}

class QueryColumn[RR: CassandraPrimitive](col: Column[RR]) extends AbstractQueryColumn[RR](col)

abstract class AbstractModifyColumn[RR](name: String) {

  def toCType(v: RR): AnyRef

  def setTo(value: RR): Assignment = QueryBuilder.set(name, toCType(value))
}

class ModifyColumn[RR](col: AbstractColumn[RR]) extends AbstractModifyColumn[RR](col.name) {

  def toCType(v: RR): AnyRef = col.toCType(v)
}

class ModifyColumnOptional[RR](col: OptionalColumn[RR]) extends AbstractModifyColumn[Option[RR]](col.name) {

  def toCType(v: Option[RR]): AnyRef = v.map(col.toCType).orNull
}

abstract class SelectColumn[T](val col: AbstractColumn[_]) {

  def apply(r: Row): T
}

class SelectColumnRequired[T](override val col: Column[T]) extends SelectColumn[T](col) {

  def apply(r: Row): T = col.apply(r)
}

class SelectColumnOptional[T](override val col: OptionalColumn[T]) extends SelectColumn[Option[T]](col) {

  def apply(r: Row): Option[T] = col.apply(r)

}