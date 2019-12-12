<p align="center">
  <img src="https://www.corda.net/wp-content/uploads/2016/11/fg005_corda_b.png" alt="Corda" width="500">
</p>

# Letter of Credit Demo

This is a CorDapp that uses a letter-of-credit business scenario to showcase Corda.

This CorDapp was written with the goal of creating a good demo, not a commercial letter-of-credit application. A real 
letter-of-credit application would be tweaked, especially in terms of the contract code.

## Running the nodes:

* Create the node driver by running `./gradlew buildExecutableJar` (osX) or `gradlew buildExecutableJar` (Windows)
* Run the node driver using `java -jar kotlin-source/build/libs/eloc-demo.jar`

If you receive error messages that say `Exception in thread "main" java.net.BindException: Address already in use`, you have zombie Java processes. You can kill them using `killall java -9` on osX, or `wmic process where "name like '%java%'" delete` on Windows.

## Interacting with the nodes:

Once all the nodes are started, go to http://localhost:10014/web/loc/. Follow the script here: 
https://github.com/corda/LetterOfCredit/blob/release/script.md.

## Update process (development only)

### Updating the web frontend

If the [front-end](https://github.com/corda/LetterOfCreditWeb) has been modified, it needs to be redeployed onto the nodes. You do this by

* Clone the [front-end project](https://github.com/corda/LetterOfCreditWeb)
* Run `npm install` in the root folder of the project
* Building the [front-end](https://github.com/corda/LetterOfCreditWeb) project by running `ng build --prod --aot --build-optimizer --base-href="/web/loc/"` from the root of the project
* Navigate to the newly created 'dist' folder within the root of the [front-end](https://github.com/corda/LetterOfCreditWeb) project
* Copy the entire contents of the 'dist' folder into the CorDapp project at 'LetterOfCredit\kotlin-source\src\main\resources\loc'
* Redeploy CorDapp as above

### Updating the cash issuance CorDapp

* Clone the [cash issuance CorDapp](https://bitbucket.org/R3-CEV/wildfire)
  * This repository is currently private
* Check out the `cais_v2` branch
* Make the changes
* From the root of the project, run `yarn run deploy`
* Rename the `kotlin-source-0.1.jar` file in `kotlin-source/build/libs` to `wildfire.jar`
* Copy `wildfire.jar` into the `lib` folder in the root of the `LetterOfCredit` project, overwriting the existing file
