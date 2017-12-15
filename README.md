This is a spring-boot project

In this example  we are exporting some data from mysql database to csv file

1 .Create a database : springBatch

2. Create table user : CREATE TABLE `springBatch`.`user` ();

3. insert some data in your table : INSERT INTO user (id, name) VALUES ("1", "migorman");

Run your project using: mvn spring-boot:run

Verify the generated file under /target/classes folder