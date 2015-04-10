package com.websudos.phantom.builder.serializers

import com.websudos.phantom.builder.query.CQLQuery
import com.websudos.phantom.builder.syntax.CQLSyntax

private[builder] trait CollectionModifiers extends BaseModifiers {

  def tupled(name: String, tuples: String*): CQLQuery = {
    CQLQuery(name).wrap(Utils.join(tuples))
  }

  def tuple(name: String, tuples: String*): CQLQuery = {
    CQLQuery(name).forcePad.append(CQLSyntax.Collections.tuple).wrap(Utils.join(tuples))
      .append(CQLSyntax.Symbols.`>`)
  }

  /**
   * This will pre-fix and post-fix the given value with the "<>" diamond syntax.
   * It is used to define the collection type of a column.
   *
   * Sample outputs would be:
   * {{{
   *   dimond("list", "int") = list<int>
   *   dimond("set", "varchar") = set<varchar>
   * }}}
   *
   * @param collection The name of the collection in use.
   * @param value The value, usually the type of the CQL collection.
   * @return A CQL query serialising the CQL collection column definition syntax.
   */
  def diamond(collection: String, value: String): CQLQuery = {
    CQLQuery(collection).append(CQLSyntax.Symbols.`<`).append(value).append(CQLSyntax.Symbols.`>`)
  }

  def prepend(column: String, values: String*): CQLQuery = {
    CQLQuery(column).forcePad.append(CQLSyntax.Symbols.`=`).forcePad.append(
      collectionModifier(Utils.collection(values).queryString, CQLSyntax.Symbols.+, column)
    )
  }

  def append(column: String, values: String*): CQLQuery = {
    CQLQuery(column).forcePad.append(CQLSyntax.Symbols.`=`).forcePad.append(
      collectionModifier(column, CQLSyntax.Symbols.+, Utils.collection(values).queryString)
    )

  }

  def discard(column: String, values: String*): CQLQuery = {
    CQLQuery(column).forcePad.append(CQLSyntax.Symbols.`=`).forcePad.append(
      collectionModifier(Utils.collection(values).queryString, CQLSyntax.Symbols.-, column)
    )
  }

  def add(column: String, values: Set[String]): CQLQuery = {
    CQLQuery(column).forcePad.append(CQLSyntax.Symbols.`=`).forcePad.append(
      collectionModifier(column, CQLSyntax.Symbols.+, Utils.set(values))
    )
  }

  /**
   * Creates a set removal query, to remove the given values from the name set column.
   * Assumes values are already serialised to their CQL form and escaped.
   *
   * {{{
   *  setColumn = setColumn - {`test`, `test2`}
   * }}}
   *
   * @param column The name of the set column.
   * @param values The set of values, pre-serialized and escaped.
   * @return A CQLQuery set remove query as described above.
   */
  def remove(column: String, values: Set[String]): CQLQuery = {
    CQLQuery(column).forcePad.append(CQLSyntax.Symbols.`=`).forcePad.append(
      collectionModifier(column, CQLSyntax.Symbols.-, Utils.set(values))
    )
  }

  def mapSet(column: String, key: String, value: String): CQLQuery = {
    CQLQuery(column).append(CQLSyntax.Symbols.`[`)
      .append(key).append(CQLSyntax.Symbols.`]`)
      .forcePad.append(CQLSyntax.eqs)
      .forcePad.append(value)
  }

  def setIdX(column: String, index: String, value: String): CQLQuery = {
    CQLQuery(column).append(CQLSyntax.Symbols.`[`)
      .append(index).append(CQLSyntax.Symbols.`]`)
      .forcePad.append(CQLSyntax.eqs)
      .forcePad.append(value)
  }

  def put(column: String, pairs: (String, String)*): CQLQuery = {
    CQLQuery(column).forcePad.append(CQLSyntax.Symbols.`=`).forcePad.append(
      collectionModifier(column, CQLSyntax.Symbols.+, Utils.map(pairs))
    )
  }

  def serialize(set: Set[String]): CQLQuery = {
    CQLQuery(CQLSyntax.Symbols.`{`)
      .forcePad.append(CQLQuery(set))
      .forcePad.append(CQLSyntax.Symbols.`}`)
  }

  def serialize(col: Map[String, String] ): CQLQuery = {
    CQLQuery(CQLSyntax.Symbols.`{`).forcePad
      .append(CQLQuery(col.map(item => s"${item._1} : ${item._2}")))
      .forcePad.append(CQLSyntax.Symbols.`}`)
  }

  def mapType(keyType: String, valueType: String): CQLQuery = {
    diamond(CQLSyntax.Collections.map, CQLQuery(List(keyType, valueType)).queryString)
  }

  def listType(valueType: String): CQLQuery = {
    diamond(CQLSyntax.Collections.list, valueType)
  }

  def setType(valueType: String): CQLQuery = {
    diamond(CQLSyntax.Collections.set, valueType)
  }
}
