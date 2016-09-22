## Running the app

In order to execute correctly this application it is needed to run 2 diferents main class. 

* Start, at least, two akka nodes in port 2551 and in port 2552 (If no port is indicated, the launcher will run an instance for each seed node):
```scala
sbt "runMain com.philips.assessment.business.BusinessLauncher 2552"
```

* Start a Finatra endpoint. Port 9999 is used but this can be changed ussing finatra flags as usual:
```scala
sbt "runMain com.philips.assessment.endpoint.FinatraServerMain"
```

**Run considerations**: SBT is required. If it is not possible to run the seed nodes in these 2 ports, go to
`scala-assessment\src\main\resources\application.conf` and change it.

## Explanation

Akka is a JDK libray that allows develope concurrent, distributed, scalable and reliable applications based on actor model. A comon use case is to have a cluster of several nodes in orther to be able of process hight throughtput requirements (IoT for example). Our mission is create a new endpoint to interact with the cluster througth a REST API using Finatra Framework. [BusinessActorController][BusinessActorController] has been created to simulate a cluster node.   

First step is create `FinatraServer` that handle [MyFinatraEndpoint][MyFinatraEndpoint]. Each `FinatraServer` has associated an Actor, [EndpointActorController][EndpointActorController], which will be used to communicate the endpoint with the cluster. For each request, a message is send to [EndpointActorController][EndpointActorController] using the ask pattern. A future will be generated and will not be complited until a response is received from the cluster.

```scala
(actorController ? DoStuff).mapTo[StuffDone]
```

Hire is a representation of whats happening:

![Alt text](/../master/images/endpoint.png?raw=true )

Note that the algoritm used for load balancing messages from EnpointActor and BusinessActor is a simple random balance per node rol. Diferent balances policies can be implemented ussing the information that we receive from ClusterEvents. Also, it is posible to use Router utilitis provides by Akka.

One of the advantages of this implementation is that is easy create new instance of endpoints if needed. We could implement an architecture similar like as follows just configuring an Apache Server for the balance of the Http requests:

![Alt text](/../master/images/multinodes.png?raw=true )

## App Architecture

Is important to decouple as much logic as posible to easy testing and maintenance. Traits and Guice DI are used to achive this target.

* [EndpointActorController][EndpointActorController] contains all logic related to push messages from endpoint to the bussiness node. Note that it is a plain Actor, none cluster logic hire. In order to use [EndpointActorController][EndpointActorController] it is required a [ActorSelector][ActorSelector]
* [ClusterSupport][ClusterSupport] requires an Actor to be used. Contains all the logic needed for instantiate a cluster for an Actor, subscribe to ClusterEvents and provide functions to react to ClusterEvents.
* [ActorSelector][ActorSelector] is a trait whose responsability is to choose what actor will be used to push messages. It has two implementations:
** [RandomClusterSelector][ActorSelector] implements a simple random balance algorithm to push messages to [BusinessActorController][BusinessActorController] nodes in cluster.
** [SimpleBusinessActorSelector][ActorSelector]. Just for testing.

[EndpointActorController]: https://github.com/robertomoreno/scala-assessment/blob/master/src/main/scala/com/philips/assessment/endpoint/actors/EndpointActorController.scala
[MyFinatraEndpoint]: https://github.com/robertomoreno/scala-assessment/blob/master/src/main/scala/com/philips/assessment/endpoint/MyFinatraEndpoint.scala
[ClusterSupport]: https://github.com/robertomoreno/scala-assessment/blob/master/src/main/scala/com/philips/assessment/utils/ClusterSupport.scala
[ActorSelector]: https://github.com/robertomoreno/scala-assessment/blob/master/src/main/scala/com/philips/assessment/utils/actors/ActorSelector.scala
[ClusterState]: https://github.com/robertomoreno/scala-assessment/blob/master/src/main/scala/com/philips/assessment/utils/actors/ClusterState.scala
[BusinessActorController]: https://github.com/robertomoreno/scala-assessment/blob/master/src/main/scala/com/philips/assessment/business/actors/BusinessActorController.scala




