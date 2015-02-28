# NotifyBidders
This is the notify bidders service for my FYP. It is written in Java. It uses a 0mq binding and also connects to a MongoDB
database. The service is responsible for emailing the bidders registered for the auction.

## Project Setup

Requires a MongoDB daemon running on port 27017 with the correct database and collection.

Database: AuctionItems
Collection: items

Sample Item JSON:
  {
    "_id" : "1",
    "item" : {
      "name" : "HP Omen",
      "description" : "Sleek and light, this rig is ready to go wherever you are. Play with better performance and
        battery life on one of the world's thinnest and lightest gaming notebooks.",
      "starting_bid" : 2000
    } 
  }

## License

None

## Setting up NotifyBidders service on AWS

- Created AWS EC2 Linux instance
- Connected to instance using FileZilla using Public DNS and .pem keyfile
- Upload JAR folder containing JAR file and properties folder to Server
- Connected to server instance using PuTTy using ec2-user@PublicDNS and .pem keyfile for SSH Auth
- Installed Mongo -> http://michaelconnor.org/2013/07/install-mongodb-on-amazon-64-bit-linux/
  and ran mongo daemon
- Created Mongo database called 'AuctionItems' - mongo AuctionData
- Created a collection called 'items' in the database and inserted sample objects:

	db.auctions.insert({
    "_id" : "1",
    "item" : {
      "name" : "HP Omen",
      "description" : "Sleek and light, this rig is ready to go wherever you are. Play with better performance and
        battery life on one of the world's thinnest and lightest gaming notebooks.",
      "starting_bid" : 2000
    } 
  })
	
## Application Setup Required
- Installed pymongo -> sudo easy_install pymongo
- Error for JDK installed vs. JDK for App
- Installed Java 8 -> sudo yum install java-1.8.0-openjdk
- Installed Java 8 dev -> sudo yum install java-1.8.0-openjdk-devel
- Set Java 8 as JDK -> sudo update-alternatives --config java, choose JDK 8
- cd to JAR directory

- Running the service -> java -jar NotifyBidders_jar/NotifyBidders.jar

- Service runs and works as expected
