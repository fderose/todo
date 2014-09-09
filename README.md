All the junit tests can be run through maven:

  mvn test -DdonePhone=+15105890752 -Dboost=3 -DdbType=hashmap

The system property "donePhone" is the phone number that you want the SMS message sent to.

The system property "boost" is the boost that you want applied to ElasticSearch matches on the title field.

The system property "dbType" is the type of database storage you want. Currently supported values are "hashmap" and "mongo". If you specify "mongo,"
the current implementation requires that an instance of the mongo db server be running on localhost at port 27017.
