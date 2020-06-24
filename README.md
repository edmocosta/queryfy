![Queryfy](docs/img/queryfy.png)

[![Build Status](https://travis-ci.org/edmocosta/queryfy.svg?branch=master)](https://travis-ci.org/edmocosta/queryfy)

Queryfy is a simple SQL-like language designed to provide a safe and flexible way to filter, sort and to paginate data over REST APIs and Front-end as well.

* As it is NOT SQL, there is no SQL Injection.

* The **Queryfy-Core** is responsible for build the abstract syntax tree (AST) from the query string and use a provided visitor to build the filter object (eg. JPAQuery, etc). 

* You can use the existing implementation (QueryDSL JPA, MongoDB (Bson)) or implement your own visitor.

### Usage
```xml
<dependency>
    <groupId>org.evcode.queryfy</groupId>
    <artifactId>queryfy-core</artifactId>
    <version>1.2.1</version>
</dependency>
```

#### QueryDSL

```xml
<dependency>
    <groupId>org.evcode.queryfy</groupId>
    <artifactId>queryfy-querydsl-jpa</artifactId>
    <version>1.2.1</version>
</dependency>
```

```java

EntityManager em = ...;
String query = "select name, age where age > 18 order by name limit 0, 100";

JPAQueryDslParser parser = new JPAQueryDslParser(em);

//Create an evaluation context. All paths added here will be available on the query syntax
QueryDslContext context = QueryDslContext.from(QTest.test)
                .withPath("name", QTest.test.name)
                .withPath("age", QTest.test.age)
                .withPath("other.id", QTest.test.other.id)
                .withPath("other.name", QTest.test.other.name)
                .build();
                
//Parse the query string into a QueryDSL JPAQuery or JPAEvaluatedQuery object
JPAEvaluatedQuery jpaQuery = parser.parseAndFind(query, context);

//List applying the projections fields (name and age)
List<Test> list = jpaQuery.listWithProjections();

```

#### MongoDB (Bson)

```xml
<dependency>
    <groupId>org.evcode.queryfy</groupId>
    <artifactId>queryfy-mongodb</artifactId>
    <version>1.2.1</version>
</dependency>
```

```java

String query = "select name, age where age > 18 order by name limit 0, 100"; 
MongodbQueryParser parser = new MongodbQueryParser();

//Create an evaluation context. All paths added here will be available on the query syntax
MongodbContext context = MongodbContext.builder()
                .withPath("name")
                .withPath("age")
                .withPath("otherId", "other.id")
                .build();
                
MongoCollection<Document> collection = ....;

//The parseAndFind method will parse the query and return a MongoDB FindIterable. you can also use the parseAndApply method
//to set the parameters on an existing FindIterable object.
FindIterable<Document> result = parser.parseAndFind(collection, query, context);

```

### See more

[Documentation](https://github.com/edmocosta/queryfy/wiki)
