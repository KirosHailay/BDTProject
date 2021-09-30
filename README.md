Big data project for CS523. 
this project contians two java projects which communicate through network, mainly communicate through kafka one of them wrtie to the topic the other consume what ever the topic I have.
The other thing is the producer project consumes the data from twitter data. and writes it to the topic I have. after that the consumer which is sparkconsumer saves the data to hbase.
Before I save the data I filter those tweets who are delted so I save only tweets that aren't deleted. after that I run multiple query and visualize the data I get from the query through zeepelin. 

Steps to run the project(Assuming you have kafka, hadoop, spark and other configuration ready to be used)

1 gain twitter developer api acess like access token, secret other (I am removing my key for secruity purpose)
2. create kafka topic
3. create the table you can find  the hive-config hql file it's name is ```tweets.hql``` this is going to create the schema. 
4. to run the application consequtively and see what's it's saving by going to hue select Hive under query option and you can do ```Select * from tweets``` and that helps you see the result. 
