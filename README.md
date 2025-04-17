DM-UPnP
====

[![CodeQL](https://github.com/JasonMahdjoub/DM-UPnP/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/JasonMahdjoub/DM-UPnP/actions/workflows/codeql-analysis.yml)

This is a fork of Cling, the DM-UPnP stack for Java and Android
------------------------------------------------------------

This project is a fork of [Cling](https://github.com/4thline/cling). It fixes some security issues, and upgrade used libraries to make them more recent or more compatible with Android.
All tests pass under Android.
The project's goals are strict specification compliance, complete, clean and extensive APIs, as well as rich SPIs for easy customization.

DM-UPnP is Free Software, distributed under the terms of the [GNU Lesser General Public License, version 2.1](https://www.gnu.org/licenses/lgpl-2.1.html).

How to use it ?
---------------
### With Gradle :

Adapt into your build.gradle file, the next code :

 - When using DM-UPnP into desktop environment, please add this dependency (minimum Java version is 11) :
    ```
	    ...
	    dependencies {
		    ...
		    implementation(group:'fr.distrimind.oss.upnp.desktop', name: 'DM-UPnP-Desktop', version: '1.5.3-STABLE')
		    //optional :
		    implementation(group:'org.slf4j', name: 'slf4j-jdk14', version: '2.0.17')
		    ...
	    }
	    ...
    ```

 - When using DM-UPnP into android environment, please add this dependency (Android API version is 26) :

    ```
	    ...
	    dependencies {
		    ...
		    implementation(group:'fr.distrimind.oss.upnp.android', name: 'DM-UPnP-Android', version: '1.5.3-STABLE')
		    ...
	    }
	    ...
    ```

 - Libraries are available on Maven Central. You can check signatures of dependencies with this [public GPG key](key-2023-10-09.pub). You can also use the next repository : 
    ```
        ...
        repositories {
            ...
            maven {
                    url "https://artifactory.distri-mind.fr/ui/native/gradle-release/"
            }
            ...
        }
        ...
    ```

To know what is the last uploaded version, please refer to versions available here : [this repository](https://artifactory.distri-mind.fr/ui/native/DistriMind-Public/fr/distrimind/oss/upnp/)
### With Maven :
Adapt into your pom.xml file, the next code :
 - When using DM-UPnP into desktop environment, please add this dependency (minimum Java version is 11) :
    ```
        ...
        <project>
            ...
            <dependencies>
                ...
                <dependency>
                    <groupId>fr.distrimind.oss.upnp.desktop</groupId>
                    <artifactId>DM-UPnP-Desktop</artifactId>
                    <version>1.5.3-STABLE</version>
                </dependency>
                <-- optional -->
                <dependency>
                    <groupId>org.slf4j</groupId>
                    <artifactId>slf4j-jdk14</artifactId>
                    <version>2.0.17</version>
                </dependency>   
                ...
            </dependencies>
            ...
        </project>
        ...
    ```
   
 - When using DM-UPnP into android environment, please add this dependency (minimum Android API version is 26) :
    ```
        ...
        <project>
            ...
            <dependencies>
                ...
                <dependency>
                    <groupId>fr.distrimind.oss.upnp.android</groupId>
                    <artifactId>DM-UPnP-Android</artifactId>
                    <version>1.5.3-STABLE</version>
                </dependency>
                ...
            </dependencies>
            ...
        </project>
        ...
    ```
   
 - Libraries are available on Maven Central. You can check signatures of dependencies with this [public GPG key](key-2023-10-09.pub). You can also use the next repository : 
    ```
        ...
        <repositories>
            ...
            <repository>
                <id>DistriMind-Public</id>
                <url>https://artifactory.distri-mind.fr/ui/native/gradle-release/</url>
            </repository>
            ...
        </repositories>
        ...		
    ```
To know what last version has been uploaded, please refer to versions available into [this repository](https://artifactory.distri-mind.fr/ui/native/DistriMind-Public/fr/distrimind/oss/upnp/)


