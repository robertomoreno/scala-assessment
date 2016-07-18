# Assessment

This assessment is used to provide a topic for interviewing a candidate for
a Scala role.

## Case:

In Januari 2015 our team started developing an IoT application using the
libraries Spray and [Akka](http://www.akka.io).  
On year later, because of changed requirements we have to make some major changes
to the application. We use this opportunity to also change the application
because of changed insights and having learned to use these libraries.

In particular, we want to replace Spray with another HTTP framework.
After looking at several alternatives, we choose [Finatra](https://twitter.github.io/finatra/), a Twitter framework based on [Finagle](http://twitter.github.io/finagle/) and [TwitterServer](http://twitter.github.io/twitter-server/).

Even though we selected a new http framework, we would like to reuse as much code of the original [Akka](http://www.akka.io) application as possible.

## Question

* How would you integrate Finatra and Akka ?
* Implement an endpoint which uses an Akka actor.
* Write an integration test for the endpoint using ScalaTest.
