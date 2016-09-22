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

First step is create [FinatraServer][FinatraServer] that handle [MyFinatraEndpoint][MyFinatraEndpoint]. Each instance [FinatraServer][FinatraServer] has associated an [EndpointActorController][EndpointActorController], which will be used to communicate the endpoint with the cluster. A message is send to [EndpointActorController][EndpointActorController] per request using the ask pattern. A future will be generated and will not be complited until a response is received from the cluster. We should convert the scala future to a tweeter future in order to finatra can handle it:

```scala
(actorController ? DoStuff).mapTo[StuffDone].as[TwitterFuture[StuffDone]]
```

Here is a representation of whats happening:
![Alt text](/../master/images/endpoint.png?raw=true )
Note that the algoritm used for load balancing messages from EnpointActor and BusinessActor is a simple random balance per node rol. Diferent balance policies can be implemented ussing the information that we receive from `ClusterEvents`. Also, it is posible to use Router utilities provided by Akka.

One of the advantages of this implementation is that it is easy to create new instance of endpoints if needed. We could implement an architecture like follows just configuring an Apache Server for http request balance:
![Alt text](/../master/images/multinodes.png?raw=true )
## App Architecture

Is important to decouple as much logic as posible to easy testing and maintenance. Scala Traits and Guice DI are used to achive this.

* [EndpointActorController][EndpointActorController] contains all logic related to push messages from endpoint to the bussiness node. Note that it is a plain Actor, none cluster logic here. In order to use [EndpointActorController][EndpointActorController] it is required a [ActorSelector][ActorSelector]
* [ClusterSupport][ClusterSupport] requires an Actor to be used. Contains all the logic needed for instantiate a cluster for an Actor, subscribe to ClusterEvents and provide functions to react to ClusterEvents.
* [ClusterState][ClusterState] responability is to generate the state of the cluster from `ClusterEvents`. Its implementation, [ByNodeRole][ClusterState] group cluster nodes by role. Furder implementations could change the group logic (for instance, by region).
* [ActorSelector][ActorSelector] is a trait whose responsability is to choose what actor will be used to push messages. It has two implementations:
 * [RandomNodeSelector][ActorSelector] implements a simple random balance algorithm to push messages to [BusinessActorController][BusinessActorController] nodes in cluster.
 * [SimpleBusinessActorSelector][ActorSelector]. Just for testing.

An `ActorRef` is injected by Gice using Finatra tools in [ClusterModule][ClusterModule]. The following section worth a closer look:

```scala
class EndpointNode extends EndpointActorController with ClusterSupport with ByNodeRole with RandomNodeSelector{
      override def receive: Receive = super.receive orElse clusterReceive
}
system.actorOf(Props[EndpointNode], name = "endpoint")
```
Thanks to Scala traits it's preaty easy to see that our endpoint will be a cluster node and will use a random node selector to push messages using a pool of actors depending on the node role.

DI injection also helps us to test our application. Make integration test from a cluster is hard but, we can override de Modules implementation to transform our cluster app in a local one. Testing modules are found in [ClusterTestModule][ClusterTestModule]. Most importat detail is:

```scala
class MyEndpoint extends EndpointActorController with SimpleBusinessActorSelector
system.actorOf(Props[MyEndpoint], name = "endpoint")
```

[EndpointActorController]: https://github.com/robertomoreno/scala-assessment/blob/master/src/main/scala/com/philips/assessment/endpoint/actors/EndpointActorController.scala
[MyFinatraEndpoint]: https://github.com/robertomoreno/scala-assessment/blob/master/src/main/scala/com/philips/assessment/endpoint/MyFinatraEndpoint.scala
[ClusterSupport]: https://github.com/robertomoreno/scala-assessment/blob/master/src/main/scala/com/philips/assessment/utils/ClusterSupport.scala
[ActorSelector]: https://github.com/robertomoreno/scala-assessment/blob/master/src/main/scala/com/philips/assessment/utils/actors/ActorSelector.scala
[ClusterState]: https://github.com/robertomoreno/scala-assessment/blob/master/src/main/scala/com/philips/assessment/utils/actors/ClusterState.scala
[BusinessActorController]: https://github.com/robertomoreno/scala-assessment/blob/master/src/main/scala/com/philips/assessment/business/actors/BusinessActorController.scala
[FinatraServer]: https://github.com/robertomoreno/scala-assessment/blob/master/src/main/scala/com/philips/assessment/endpoint/FinatraServer.scala
[ClusterModule]: https://github.com/robertomoreno/scala-assessment/blob/master/src/main/scala/com/philips/assessment/endpoint/modules/ClusterModule.scala
[ClusterTestModule]: https://github.com/robertomoreno/scala-assessment/blob/master/src/test/scala/com/philips/assessment/endpoint/modules/ClusterTestModule.scala




