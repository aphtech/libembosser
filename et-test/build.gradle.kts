plugins {
  application
  kotlin("jvm") version "1.2.71"
}

application {
  mainClassName = "org.brailleblaster.libembosser.etTest.EtTestKt"
}

dependencies {
  implementation(kotlin("stdlib"))
}