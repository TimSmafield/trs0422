# trs0422

Written Java 1.12

installation: Intellij: Welcome to Intellij -> Get from VLC enter given GIT URL and press clone. Open src/Shop.java and right click the main function and select "modify run configurations" Enter desired CLI arguments for example:  CHNS 5 7/2/2015 .25 hit apply and ok right click main function and select "Shop.main()" Program should run.

Alternatively with command line you can clone the repo and run javac Shop.java and than something like java Shop CHNS 5 7/2/2015 .25


Shop is basically the runner while RentalAgreementImpl is where most of the logic lives. 
Within this class the method "checkout" calculates values to populate RentAgreementDetails and the method "print" in RentalAgreementImpl 
prints the values as formatted text to the console.  I attempted to code to interface and rely on properties files to allow for extention and internationalization. 

JUnits are located at Tests/RentalAgreementImplTest.java
