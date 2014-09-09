All the junit tests can be run through maven:

  mvn test -DdonePhone=+15105890752 -Dboost=3

The system property donePhone is the phone number that you want the SMS message sent to. 

The system property boost is the boost that you want applied to ElasticSearch matches on the title field.

