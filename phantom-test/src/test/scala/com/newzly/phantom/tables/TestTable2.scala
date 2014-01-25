package com.newzly.phantom.tables

import com.datastax.driver.core.Row
import com.newzly.phantom.{
  CassandraTable
}
import com.newzly.phantom.helper.{TestSampler, ModelSampler, Sampler}

case class SimpleStringClass(something: String)

object SimpleStringClass extends ModelSampler[SimpleStringClass] {
  def sample: SimpleStringClass = SimpleStringClass(Sampler.getAUniqueString)
}

case class SimpleMapOfStringsClass(something: Map[String, Int])

object SimpleMapOfStringsClass extends ModelSampler[SimpleMapOfStringsClass] {
  def sample: SimpleMapOfStringsClass = SimpleMapOfStringsClass(Map(
    Sampler.getAUniqueString -> Sampler.getARandomInteger(),
    Sampler.getAUniqueString -> Sampler.getARandomInteger(),
    Sampler.getAUniqueString -> Sampler.getARandomInteger(),
    Sampler.getAUniqueString -> Sampler.getARandomInteger(),
    Sampler.getAUniqueString -> Sampler.getARandomInteger()
  ))
}

case class TestList(key: String, l: List[String])

object TestList extends ModelSampler[TestList] {
  def sample: TestList = TestList(
    Sampler.getAUniqueString,
    List.range(0, 20).map(x => Sampler.getAUniqueString)
  )
}

case class SimpleStringModel(something: String) extends ModelSampler[SimpleStringModel] {
  def sample: SimpleStringModel = SimpleStringModel(Sampler.getAUniqueString)
}

case class TestRow2(
  key: String,
  optionalInt: Option[Int],
  simpleMapOfString: SimpleMapOfStringsClass,
  optionalSimpleMapOfString: Option[SimpleMapOfStringsClass],
  mapOfStringToCaseClass: Map[String, SimpleMapOfStringsClass]
)

object TestRow2 extends ModelSampler[TestRow2] {
  def sample: TestRow2 = {
    TestRow2(
      Sampler.getAUniqueString,
      Some(Sampler.getARandomInteger()),
      SimpleMapOfStringsClass.sample,
      Some(SimpleMapOfStringsClass.sample),
      List.range(0, 20).map(x => { x.toString -> SimpleMapOfStringsClass.sample}).toMap
    )
  }
}

sealed class TestTable2 extends CassandraTable[TestTable2, TestRow2] {
  def fromRow(r: Row): TestRow2 = {
    TestRow2(
      key(r),
      optionA(r),
      classS(r),
      optionS(r),
      mapIntoClass(r)
    )
  }
  object key extends PrimitiveColumn[String]
  object optionA extends OptionalPrimitiveColumn[Int]
  object classS extends JsonColumn[SimpleMapOfStringsClass]
  object optionS extends JsonColumn[Option[SimpleMapOfStringsClass]]
  object mapIntoClass extends JsonColumn[Map[String, SimpleMapOfStringsClass]]
  val _key = key
}

object TestTable2 extends TestTable2 with TestSampler[TestTable2, TestRow2] {
  override val tableName = "TestTable2"

  def createSchema: String = {
    """|CREATE TABLE TestTable2(
      |key text PRIMARY KEY,
      |optionA int,
      |classS text,
      |optionS text,
      |mapIntoClass map<text,text>);
    """.stripMargin
  }
}