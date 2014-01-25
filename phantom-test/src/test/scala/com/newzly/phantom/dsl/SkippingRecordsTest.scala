
package com.newzly.phantom.dsl

import java.util.UUID
import com.datastax.driver.core.{ Session, Row }
import com.datastax.driver.core.utils.UUIDs

import com.newzly.phantom.{ PrimitiveColumn, CassandraTable }
import com.newzly.phantom.field.{ UUIDPk, LongOrderKey }
import com.newzly.phantom.Implicits._
import com.newzly.phantom.helper.{BaseTest, Tables}
import com.newzly.phantom.helper.AsyncAssertionsHelper._
import org.scalatest.Assertions
import org.scalatest.concurrent.AsyncAssertions

class SkippingRecordsTest extends BaseTest with Assertions with AsyncAssertions  {
  val keySpace: String = "SkippingRecordsTest"

  ignore should "allow skipping records " in {

    object Articles extends Articles {
      override val tableName = "articlestest"
    }

    val article1 = Article("test", UUIDs.timeBased(),  1)
    val article2 = Article("test2", UUIDs.timeBased(), 2)
    val article3 = Article("test3", UUIDs.timeBased(), 3)

    val result = for {
      i1 <- Articles.insert
        .value(_.name, article1.name).value(_.id, article1.id)
        .value(_.order_id, article1.order_id)
        .execute()
      i2 <- Articles.insert
        .value(_.name, article2.name)
        .value(_.id, article2.id)
        .value(_.order_id, article2.order_id)
        .execute()
      i3 <- Articles.insert
        .value(_.name, article3.name)
        .value(_.id, article3.id)
        .value(_.order_id, article3.order_id)
        .execute()
      res <- Articles.select.skip(1).one
    } yield res

    result successful {
      row => assert(row.get == article2)
    }
  }

}

