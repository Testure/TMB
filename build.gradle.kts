import com.smushytaco.lwjgl_gradle.Preset
import groovy.namespace.QName
import groovy.util.Node
import groovy.util.NodeList
import groovy.xml.XmlParser
import java.io.FileNotFoundException
import java.net.URL

plugins {
	alias(libs.plugins.loom)
	alias(libs.plugins.lwjgl)
    java
	`maven-publish`
}
val modVersion: Provider<String> = providers.gradleProperty("mod_version")
val modGroup: Provider<String> = providers.gradleProperty("mod_group")
val modName: Provider<String> = providers.gradleProperty("mod_name")

val javaVersion: Provider<Int> = libs.versions.java.map { it.toInt() }

base.archivesName = modName
group = modGroup.get()
version = modVersion.get()
loom {
    customMinecraftMetadata.set("https://downloads.betterthanadventure.net/bta-client/${libs.versions.btaChannel.get()}/${libs.versions.bta.get()}/manifest.json")
}
repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/") { name = "Fabric" }
    maven("https://maven.thesignalumproject.net/infrastructure") { name = "SignalumMavenInfrastructure" }
    maven("https://maven.thesignalumproject.net/releases") { name = "SignalumMavenReleases" }
	maven("https://maven.thesignalumproject.net/nightly") { name = "SignalumMavenNightly" }
    ivy("https://github.com/Better-than-Adventure") {
        patternLayout { artifact("[organisation]/releases/download/[revision]/[module]-bta-[revision].jar") }
        metadataSources { artifact() }
    }
    ivy("https://downloads.betterthanadventure.net/bta-client/${libs.versions.btaChannel.get()}/") {
        patternLayout { artifact("/v[revision]/client.jar") }
        metadataSources { artifact() }
    }
    ivy("https://downloads.betterthanadventure.net/bta-server/${libs.versions.btaChannel.get()}/") {
        patternLayout { artifact("/v[revision]/server.jar") }
        metadataSources { artifact() }
    }
    ivy("https://piston-data.mojang.com") {
        patternLayout { artifact("v1/[organisation]/[revision]/[module].jar") }
        metadataSources { artifact() }
    }
}
lwjgl {
	version = libs.versions.lwjgl
	implementation(Preset.MINIMAL_OPENGL)
}
dependencies {
    minecraft("::${libs.versions.bta.get()}")

	runtimeOnly(libs.clientJar)
	implementation(libs.loader)
	// If you do not need Halplibe you can comment out or delete this line.
	implementation(libs.halplibe)
	implementation(libs.modMenu)
	implementation(libs.legacyLwjgl)

	implementation(libs.slf4jApi)
	implementation(libs.guava)
	implementation(libs.log4j.slf4j2.impl)
	implementation(libs.log4j.core)
	implementation(libs.log4j.api)
	implementation(libs.log4j.api12)
	implementation(libs.gson)

	implementation(libs.commonsLang3)
	include(libs.commonsLang3)
}
java {
	toolchain {
		languageVersion = javaVersion.map { JavaLanguageVersion.of(it) }
		vendor = JvmVendorSpec.ADOPTIUM
	}
	sourceCompatibility = JavaVersion.toVersion(javaVersion.get())
	targetCompatibility = JavaVersion.toVersion(javaVersion.get())
	withSourcesJar()
}
val licenseFile = run {
	val rootLicense = layout.projectDirectory.file("LICENSE")
	val parentLicense = layout.projectDirectory.file("../LICENSE")
	when {
		rootLicense.asFile.exists() -> {
			logger.lifecycle("Using LICENSE from project root: {}", rootLicense.asFile)
			rootLicense
		}
		parentLicense.asFile.exists() -> {
			logger.lifecycle("Using LICENSE from parent directory: {}", parentLicense.asFile)
			parentLicense
		}
		else -> {
			logger.warn("No LICENSE file found in project or parent directory.")
			null
		}
	}
}
tasks {
	withType<JavaCompile>().configureEach {
		options.encoding = "UTF-8"
		sourceCompatibility = javaVersion.get().toString()
		targetCompatibility = javaVersion.get().toString()
		if (javaVersion.get() > 8) options.release = javaVersion
	}
	named<UpdateDaemonJvm>("updateDaemonJvm") {
		languageVersion = libs.versions.gradleJava.map { JavaLanguageVersion.of(it.toInt()) }
		vendor = JvmVendorSpec.ADOPTIUM
	}
	withType<JavaExec>().configureEach { defaultCharacterEncoding = "UTF-8" }
	withType<Javadoc>().configureEach { options.encoding = "UTF-8" }
	withType<Test>().configureEach { defaultCharacterEncoding = "UTF-8" }
	withType<Jar>().configureEach {
		licenseFile?.let {
			from(it) {
				rename { original -> "${original}_${archiveBaseName.get()}" }
			}
		}
	}
	processResources {
		val resourceMap = mapOf(
			"version" to modVersion.get(),
			"loader" to libs.versions.loader.get(),
			"halplibe" to libs.versions.halplibe.get(),
			"java" to libs.versions.java.get(),
			"modmenu" to libs.versions.modMenu.get()
		)
		inputs.properties(resourceMap)
		filesMatching("fabric.mod.json") { expand(resourceMap) }
		filesMatching("**/*.mixins.json") { expand(resourceMap.filterKeys { it == "java" }) }
	}
}
// Removes LWJGL2 dependencies
configurations.configureEach { exclude(group = "org.lwjgl.lwjgl") }



publishing {
	if(checkVersion(modGroup.get(), modName.get(), modVersion.get())){
		repositories {
			maven {
				name = "signalumMaven"
				url = uri("https://maven.thesignalumproject.net/releases")
				credentials(PasswordCredentials::class)
				authentication {
					create<BasicAuthentication>("basic")
				}
			}

			publications {
				create<MavenPublication>("maven") {
					groupId = project.property("mod_group").toString()
					artifactId = project.property("mod_name").toString()
					version = project.property("mod_version").toString()
					from(components["java"])
				}
			}
		}
	}
}

fun checkVersion(group: String, name: String, version: String): Boolean {
	return try {
		val xml = URL("https://maven.thesignalumproject.net/releases/$group/$name/maven-metadata.xml").readText()
		val metadata = XmlParser().parseText(xml)

		val versions = metadata.getAt(QName("versioning")).getAt("versions").getAt("version").map { (it as Node).text() }

		if (version in versions) {
			System.err.println("Version $version of $group.$name already exists!")
			false
		} else {
			true
		}
	} catch (ignored: FileNotFoundException) {
		true
	}
}
