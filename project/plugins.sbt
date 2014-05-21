resolvers ++= Seq(
    "Sonatype snapshots"                                 at "http://oss.sonatype.org/content/repositories/snapshots/",
    Classpaths.typesafeResolver,
    "jgit-repo"                                          at "http://download.eclipse.org/jgit/maven",
    "Twitter Repo"                                       at "http://maven.twttr.com/"
)

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.4")

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.6.4")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.11.2")

addSbtPlugin("com.twitter" %% "scrooge-sbt-plugin" % "3.14.1")

addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "0.98.2")

<<<<<<< HEAD
addSbtPlugin("com.sksamuel.scoverage" %% "sbt-coveralls" % "0.0.5")
=======
addSbtPlugin("com.twitter" %% "scrooge-sbt-plugin" % "3.14.1")

addSbtPlugin("de.johoop" % "jacoco4sbt" % "2.0.0")
>>>>>>> 1eb957e9336d7ac43e9b11a0cece5cde4c17f7a2
