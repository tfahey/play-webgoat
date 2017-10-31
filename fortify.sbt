// this makes it so sbt can resolve the plugin
credentials += Credentials(Path.userHome / ".lightbend" / "commercial.credentials")
resolvers += Resolver.url(
  "lightbend-commercial-releases",
  new URL("http://repo.lightbend.com/commercial-releases/"))(
  Resolver.ivyStylePatterns)

// enable the plugin
libraryDependencies += compilerPlugin(
  "com.lightbend" %% "scala-fortify" % "4f346b33"
    classifier "assembly"
    cross CrossVersion.patch)

// configure the plugin
scalacOptions += s"-P:fortify:out=${target.value}"

// `translate` task
val translate: TaskKey[Unit] = taskKey("Fortify Translation")
translate := Def.sequential(
  clean in Compile,
  compile in Compile
).value

// `scan` task
val fpr = "scan.fpr"
val scan: TaskKey[Unit] = taskKey("Fortify Scan")
scan := {
  Seq("bash","-c", s"rm -rf ${fpr}").!
  Seq("bash","-c", s"sourceanalyzer -f ${fpr} -scan target/*.nst").!
}