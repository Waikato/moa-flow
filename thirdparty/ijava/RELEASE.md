# How To Release IJava to Maven

## Binary

* Download release archive:

  https://github.com/SpencerPark/IJava/releases/download/v1.3.0/ijava-1.3.0.zip

* Extract `ijava-1.3.0.jar` from release archive

## Sources

* Download source archive:

  https://github.com/SpencerPark/IJava/archive/v1.3.0.zip

* Extract and merge `src/main/java` an extract `src/main/resources`

* Generate jar out of it (from inside the top-level directory of the sources, about `io`):

  ```
  jar -cf ../ijava-1.3.0-sources.jar .
  ```

## Javadoc

* Generate javadoc files (from the same directory):

  ```
  javadoc -verbose -author --source-path . -d ../doc -cp ../ijava-1.3.0.jar -subpackages io
  cd ../doc
  jar -cf ../ijava-1.3.0-javadoc.jar .
  ```

## Upload artifacts

* Run the following Maven command to deploy the package:

  ```
  mvn gpg:sign-and-deploy-file \
    -DgroupId=com.github.waikato.thirdparty \
    -DartifactId=ijava \
    -Dversion=1.3.0 \
    -Dpackaging=jar \
    -Dfile=ijava-1.3.0.jar \
    -Dsources=ijava-1.3.0-sources.jar \
    -Djavadoc=ijava-1.3.0-javadoc.jar \
    -DpomFile=ijava-1.3.0.pom \
    -DrepositoryId=sonatype-nexus-staging \
    -Durl=https://oss.sonatype.org/service/local/staging/deploy/maven2/
  ```

* See the following link for details:

  http://maven.apache.org/plugins/maven-gpg-plugin/sign-and-deploy-file-mojo.html

* Close and release the repository on the Sonatype Nexus
