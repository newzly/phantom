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
package com.outworkers.phantom.connectors

import scala.collection.JavaConverters._
import com.datastax.driver.core.{ProtocolVersion, Session}

import scala.util.control.NoStackTrace

trait SessionAugmenter {

  def session: Session

  def protocolVersion: ProtocolVersion = {
    session.getCluster.getConfiguration.getProtocolOptions.getProtocolVersion
  }

  def isNewerThan(pv: ProtocolVersion): Boolean = {
    protocolVersion.compareTo(pv) > 0
  }

  def v3orNewer : Boolean = isNewerThan(ProtocolVersion.V2)

  def protocolConsistency: Boolean = isNewerThan(ProtocolVersion.V1)

  def v4orNewer : Boolean = isNewerThan(ProtocolVersion.V3)
}

trait SessionAugmenterImplicits {
  implicit class RichSession(val session: Session) extends SessionAugmenter
}

/**
 * Represents a single Cassandra keySpace.
 *
 * Provides access to the associated `Session` as well as to a
 * `Connector` trait that can be mixed into `CassandraTable`
 * instances.
 *
 * @param name the name of the keySpace
 * @param clusterBuilder the provider for this keySpace
 */
class CassandraConnection(
  val name: String,
  clusterBuilder: ClusterBuilder,
  autoinit: Boolean,
  keyspaceFn: Option[(Session, KeySpace) => String] = None,
  errorHander: Throwable => Throwable = identity
) {

  val provider = new DefaultSessionProvider(KeySpace(name), clusterBuilder, autoinit, keyspaceFn, errorHander)

  val self = this

  /**
   * The Session associated with this keySpace.
   */
  lazy val session: Session = provider.session

  def cassandraVersions: Set[VersionNumber] = {
    session.getCluster.getMetadata.getAllHosts
      .asScala.map(_.getCassandraVersion)
      .toSet[VersionNumber]
  }

  def cassandraVersion: Option[VersionNumber] = {
    val versions = cassandraVersions

    if (versions.nonEmpty) {

      val single = versions.headOption

      if (cassandraVersions.size == 1) {
        single
      } else {

        if (single.forall(item => versions.forall(item ==))) {
          single
        } else {
          throw new RuntimeException(
            s"Illegal single version comparison. You are connected to clusters of different versions." +
              s"Available versions are: ${versions.mkString(", ")}"
          ) with NoStackTrace
        }
      }
    } else {
      throw new RuntimeException("Could not extract any versions from the cluster, versions were empty")
    }
  }

  /**
   * Trait that can be mixed into `CassandraTable`
   * instances.
   */
  trait Connector extends com.outworkers.phantom.connectors.Connector with SessionAugmenterImplicits {

    lazy val provider = self.provider

    lazy val keySpace = self.name

    implicit val space: KeySpace = KeySpace(self.name)

    def cassandraVersion: Option[VersionNumber] = self.cassandraVersion

    def cassandraVersions: Set[VersionNumber] = self.cassandraVersions
  }

}

case class KeySpace(name: String)
