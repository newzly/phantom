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
package com.outworkers.phantom.tables.bugs

import com.outworkers.phantom.dsl._

abstract class SchemaBugSecondaryIndex extends Table[SchemaBugSecondaryIndex, Item] {

  override def tableName: String = "items"

  object id extends IntColumn with PartitionKey
  object name extends Col[Name]
  object ref extends DateTimeColumn with Index
  object date extends Col[DateTime]
}

case class Name(value: String)

object Name {
  implicit val namePrimitive: Primitive[Name] = Primitive.derive[Name, String](_.value)(Name.apply)
}

case class Item(
  id: Int,
  name: Name,
  reference: DateTime,
  date: DateTime
)
