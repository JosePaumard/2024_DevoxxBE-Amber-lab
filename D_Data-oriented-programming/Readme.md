Refactoring to Data Oriented Programming
========================================

## Introduction

This lab takes you through the refactoring of a legacy application using the Data Oriented Programming Principles, so that you can see what you can take from these ideas to improve the quality of your code and your architectures.

You are working on an application that allows you to follow the price of a set of flights.

The principle of the application is the following:
- you declare that you want to follow the price of a different flights between different cities
- the system then pushes the price of these flights when they change
- when a price changes, then it is displayed on a graphical user interface.

Of course all of this is made as simple as possible: there is no network connection, the database is just an in-memory hash table, and the GUI is simply the console.


## Presenting the Application

The application has been organised in the following modules:
- a database module (`A_Database`): you can fetch flights from this module
- a price monitoring module (`B_Price-monitoring`): you can ask this module to send you the price of given flight when it changes
- a graphical user interface (`C_Graphical-user-interface`): you can send information to this module, so that it can display it
- a business module (`D_Flight-business-process`): it is the core of your application, this is where your business process is implemented
- and a main module (`E_Main`), that can run the application.

You can run this application from its main method located in the `Main` class in the `E_Main` module. If you do so, you can see that 4 flights are being monitored: Paris to Atlanta, Amsterdam to New York, London to Miami, and Frankfurt to Washington. You should see the updating of these prices on your console. 

There are many flaws in this application, not only in the code, but also in the way this application is organized.
1. Every module depends on the database.
2. There are classes declared in the object model that are not used by your application. Maybe there are used in other parts of your applications, but your business modules still depends on them. This is the case for the `Plane` class. 
3. Your main business process fetches its technical dependencies itself, using badly implemented singletons.
4. One of the ugly consequence is that it depends on your graphical user interface, meaning that everytime someone decides to change the color of a button, you will recompile your business rules.
5. Many things are not working in this application. For instance, your entities are used all throughout your modules. Which is absurd: your GUI or your consumers depend on your Hibernate entities. 
6. Because everything is so tightly entangled, adding the support for multileg flights is hard, and will be very costly.


## Goal of the Refactoring

The goal of the refactoring is to organize the application in such a way that the dependencies are in the right direction. All your modules should depend on the central module of your application: the one that is implementing your business rules (`D_Flight-business-process` in the application).

Then, once this refactoring is done, you will see how adding the support for multileg flights can be done very easily, using sealed types and pattern matching.


## Step 0: Exploring and Running the Application

You can access the state of the code base at this step by checking out the `DOP_Refactor-step00` of this Git repository. Note that this `Readme.md` is the same in all the branches of this repository. 

You can launch the application as it is by running the `Main` class. As you can see, this class creates four flights: Paris -> Atlanta, Amsterdam -> New-York, London -> Miami, and Frankfurt -> Washington. 
It then decides to follow the prices of these four flights. If you follow the code that is being executed, you will see that these flights change their price every 500ms. 
Then these flights are displayed in the GUI (the console).

Running this code show you the flights that are created, then monitored, and then displayed. Don't forget to stop the running of this application, because if you don't it will run forever.


```shell
Fetching flight FlightID[flightId=PaAt]
Monitoring the price for FlightID[flightId=PaAt]
Fetching flight FlightID[flightId=AmNY]
Monitoring the price for FlightID[flightId=AmNY]
Fetching flight FlightID[flightId=LoMi]
Monitoring the price for FlightID[flightId=LoMi]
Fetching flight FlightID[flightId=FrWa]
Monitoring the price for FlightID[flightId=FrWa]
Fetching flight FlightID[flightId=PaAt]
Fetching flight FlightID[flightId=AmNY]
Fetching flight FlightID[flightId=LoMi]
Fetching flight FlightID[flightId=FrWa]
Flight from London to Miami: price is now 100
Flight from Paris to Atlanta: price is now 105
Flight from Francfort to Washington: price is now 113
Flight from Amsterdam to New York: price is now 110
```


## Step 1: Fixing the Dependencies

You can access the state of the code base at the end of this step by checking out the `DOP_Refactor-step01` of this Git repository.

The first step consists in fixing the dependency problems this application suffers. What we want is that the technical modules (database, price service, GUI) depend on the business module, and not the opposite. For that, you are going to create interfaces and records, and place them in the right modules of the current application.

### Fixing the relation between the Business module and the GUI

To prevent the business module to depend on the GUI, you need to create an interface in the Business module (`D_Flight-business-process`) of your application. This interface has only one method. Calling it displays a flight on the GUI. The GUI thus becomes a client of your business process.

#### Adding Interfaces and Records

You can create the following interface in the `D_Flight-business-process` module. You can put it in a `service` package for instance.

```java
public interface FlightGUIService {
    void displayFlight(Flight flight);
}
```

And because you should not need to depend on the database anymore, you can also create the following records `City` and `Flight`. You can put them in a `model` package for instance.

```java
public record City(String name) {}
```

```java
public record Flight(City from, City to) {}
```

Be careful when doing this refactoring: your `FlightGUIService` should depend on your records, not on the entity classes defined in the `A_Database` module.

#### Refactoring the D_Flight-business-process Module

Now you can refactor the `FlightMonitoring` class. Remove the static field `FlightGUI flightGUIService` and replace it by a private non-static field `FlightGUIService flightGUIService`. Do not initialize it for the moment. Declaring a singleton in that way is a double mistake. First, you should not pull your dependencies yourself. It makes pour `FlighMonitoring` class depend on both the interface `FlightGUIService` and its implementation. Second, initializing a singleton in that way makes your code complex, and subject to race conditions. The pattern you will see is much simpler. 

This class is not pulling its own dependency to the database service anymore, which is one less problem. You need to make the `launchDisplay()` method non-static, because it now needs to read an instance field.

The `FlightMonitoring.getInstance()` method does not compile anymore, which is fine because you will remove it very soon. In the meantime you can just remove the faulty method call `launchDisplay()` within this method.

Your problem now is that you have two different classes that model a flight. The first one is called `FlightEntity` and comes from the `A_Database` module, and the second one is the record you just created. Let us refactor the `monitoredFlights` map so that it stores instances of `Flight` as values, and not `FlightEntity`. Some more elements of this class will not be compiling anymore, but we will take care of them later. 

Both methods `FlightMonitoring.followFlight()` and `FlightMonitoring.monitorFlight()` are not compiling anymore because they both call `dbService.fetchFlight()` that returns a `FlightEntity` instance instead of a `Flight` instance.  

This problem has to be fixed with an adapter. You need to adapt an instance of the `FlightEntity` class defined in the `A_Database` module, to an instance of your `Flight` record.

The code you need to add to both these methods looks like the following:

```java
var flightEntity = dbService.fetchFlight(flightPK);
// Adaptation
City from = new City(flightEntity.from().name());
City to = new City(flightEntity.to().name());
Flight newFlight = new Flight(from, to);
```

At some point you will be able to move this adaptation in the `A_Database` module, where it should be.

#### Fixing the Price Property of Flight

How can you fix the fact that your `Flight` record does not have a price? Your problem here is that the price changes, and that a record is non-modifiable, so you cannot simply add a component to this record, because you will not be able to modify it.

The solution to this problem will come when you have refactored the relation between the `D_Flight-business-process` module and the `B_Price-monitoring` module. In the meantime, you can just stub it by creating an empty `price()` method on your `Flight` record. We will come to it again later.

At this point, the `FlightMonitoring` class still depends on some elements of the `A_Database` module: the `FlightPK` flight primary key, and the `PriceEntity` entity. We will take care of these later. 

#### Refactoring the C_Graphical-user-interface Code

Now you need to refactor the other side of this dependency: the `C_Graphical-user-interface` module.

The `FlightGUI` class should implement the `FlightGUIService` interface you defined in the `D_Flight-business-process` module. For that, you need to declare that the `C_Graphical-user-interface` module depends on the `D_Flight-business-process` module. So you need to open the POM files of both modules:

1. `D_Flight-business-process` module POM: remove the dependency to the `C_Graphical-user-interface` module.
2. And in the `C_Graphical-user-interface` module POM:  add a dependency to the `D_Flight-business-process` module.

If your refresh your Maven configuration in your IDE, you should now be able to import the `FlightGUIService` interface in your  `FlightGUI` class. 

To implement the `FlightGUIService` interface, you will need to refactor the `displayFlight()` method, and have it take a `Flight` instance, instead of a `FlightEntity` instance. This code will need a `price()` method on the `Flight` record, that you can create as a stub for now. We will take care of this method later. You will also need a `Price` record, that you can create in the `model` package. 

```java
public record Price(int price) {}

public record Flight(City from, City to) {
    
    Price price() {
        return new Price(0);
    }
}
```

You can also delete the ugly `FlightGUI.getInstance()` static method, that is not called anymore. Good riddance.

### Fixing the relation between the Business module and the Price Monitoring Module

Let us use the same technique to fix the relation between the `D_Flight-business-process` module and the `B_Price-monitoring` module.

#### Refactoring the D_Flight-business-process Module

The `FlightConsumer` interface is defined in the `B_Price-monitoring` module, thus imposing a dependency in the wrong direction. Let us make it so that the `D_Flight-business-process` module defines this interface contract, and while we are at it, also defines the objects that are moved between these two modules.

1. Move the `FlightConsumer` interface to the `D_Flight-business-process` module, for instance in a `service` package.
2. Make it consume an instance of `Price`, a record you create in the `D_Flight-business-process` module, for instance in a `model` package. This record has only on component: `price`, of type `int`. 

The interface should now look like this:

```java
public interface FlightConsumer {
    void updateFlight(Price price);
}
```

And the `Price` record is the following. 

```java
public record Price(int price) {}
```

You also need to create an interface to model what this `B_Price-monitoring` module is doing. Following what you did with the GUI, this interface needs to be defined on the `D_Flight-business-process` module and implemented in the `B_Price-monitoring` module.

This interface can be the following, you can put it in a `service` package of the `D_Flight-business-process` module.

```java
public interface PriceMonitoringService {
    void followPrice(FlightID flightID, FlightConsumer consumer);
}
```

It needs a `FlightID` object, that you can create as a record in your `D_Flight-business-process` module, in the `model` package for instance.

```java
public record FlightID(String id) {}
```

At some point, you will need to adapt the `FlightPK` class from your `A_Database` module to this `FlightID` record. 

You now need to fix the `FlightMonitoring` class, which is not compiling anymore because it depends on the `FlightConsumer` interface that you just modified, following the same principles as previously.

1. Make the `priceMonitoringService` non-static, and remove the ugly call to `FlightPriceMonitoringService.getInstance()`. We will fix the initialization of this field later.
2. Remove the call to `priceMonitoringService.updatePrices()`. The whole method that will be removed anyway.

Make your class `FlightPriceMonitoringService` implement `PriceMonitoringService`. You will need to refactor the `followPrice()` method, that should take a `FlightID` key instead of `FlightPK`, and change the type of the `registry` map, so that it declares `FlightID` keys instead of `FlightPK`. 

At this point, your `D_Flight-business-process` module should not have anymore dependency on the `B_Price-monitoring` module. This module, on the contrary, needs to depend on the `D_Flight-business-process` module, where the interface it should implement is found, as well as the definition of the consumer it should call.

So you need to open the POM files of both modules:

1. In the `D_Flight-business-process` module POM: remove the dependency to the `B_Price-monitoring` module.
2. And in the `B_Price-monitoring` module POM:  add a dependency to the `D_Flight-business-process` module.

You should now refresh your Maven configuration in your IDE, which will fix some of the compiler errors of your `FlightMonitoring` class. 

#### Refactoring the B_Price-monitoring Module

You can see now that the class `FlightPriceMonitoringService` is not compiling anymore, which is expected. All you need to do is to fix the imports: it should only depend on the records of the `D_Flight-business-process` module. You can create a `Price` instance instead of a `PriceEntity` instance in your `updatePrices()` method. 

You can delete the ugly `FlightPriceMonitoringService.getInstance()` static method, that is always a very pleasant thing to do. It is not called anymore, so nobody will notice. And change the type of the `priceMonitoringService` field to `PriceMonitoringService`, as it is the interface modeling this service. 

### Fixing the Dependency between the Business module and the Database Module

The last wrong relation you need to fix is the relation between the `D_Flight-business-process` module and the `A_Database` module.

You are going to follow the same principle, that is to create an interface in the `D_Flight-business-process` module and implement it in the `A_Database` module.

#### Refactoring the D_Flight-business-process Module

You can create this interface in the `service` package of the `D_Flight-business-process` module, and use it in the `FlightMonitoring` class.

This interface can look like this one. `Flight` and `FlightID` are the records you defined in the `D_Flight-business-process` module.

```java
public interface DBService {
    Flight fetchFlight(FlightID flightID);
}
```

Following this, you can get rid of your last call to a `getInstance()` in the `FlightMonitoring` class, and refactor the `dbService` field to have the `DBService` type.

Several fixes should be made in the `FlightMonitoring.followFlight()` method. The final goal is to completely remove any dependency on the entity class of the `A_Database` module. 
1. Make it take a `FlightID` instead of an `FlightPK`. Remember that we want this module to not depend on `A_Database` anymore. Now you do not need your adapter code anymore.
2. You will also need to create an empty `updatePrice()` method in your `Flight` record, just to make the compiler happy. We will fix this method later.
3. Also, make this `updatePrice()` method take an instance of the `Price` record ot the `D_Flight-business-process` module, instead of the `A_Database` one. It will make the `flightConsumer` easier to write, because you do not need any adaptation there neither.
4. And at last, make the `followPrice()` method take an instance of `FlightID` instead of `FlightPK`. 

Some more fixes are needed in the `FlightMonitoring.monitorFlight()` method.
1. Make it take a `FlightID` instead of an `FlightPK`.
2. The method `fetchFlight()` should already take a `FlightID` instance. 
3. Fix the `monitoredFlights` registry. Its keys should now be your record `FlightID`.

Your `FlightMonitoring` class should not depend on any class from the `A_Database` module anymore. You can check that in its imports.

Now you can invert the dependency between the two modules `D_Flight-business-process` and `A_Database`.
1. In the `D_Flight-business-process` module POM: remove the dependency to the `A_Database` module.
2. And in the `A_Database` module POM: add a dependency to the `D_Flight-business-process` module.

If you refresh your Maven configuration in your IDE, your `E_Main` module should not compile anymore, which is OK for now.

As you can see, your business module does not depend on any other module anymore. Your high level business code doesn't depend on any low-level implementation detail anymore, as it should be. 

#### Refactoring the A_Database Module

The same will apply to the `A_Database` module, that should become a client of the `D_Flight-business-process` module.

Your `FlightDBService` class should implement the `DBService` interface from your `D_Flight-business-process` module.

Also, this class should now depend on the model object your `D_Flight-business-process` module is providing. That includes `City`, `Flight` and `IDFlight`. It is perfectly OK for this module to use its own object model, since in a real application it would probably map it to some kind of database. But it is its responsibility to adapt its object model to the requirements of your business modules.

An example of such an adaptation is the implementation of the `FlightDBService.fetchFlight()` method. This method takes an instance of `FlightID` as a parameter, which is an object sent by the `D_Flight-business-process` module. But it needs a primary key to access the right flightEntity, that is of type `FlightPK`, defined in this `A_Database` module. So an adaptation should be done here, between these two instances. Doing this adaptation is the responsibility of the `A_Database` module.

You can fix this module with the following steps. 
1. First, the `FlightDBService` class should implement the `DBService` interface. 
2. Then, you need to refactor the `fetchFlight()` method. It should return an instance of `Flight`, and take a `FlightID` as a parameter. These classes are the records you defined in the `D_Flight-business-process`. 
3. Then you need to implement two adaptations. 
   - The first one consists in adapting the `FlightID` to `FlightPK`. 
   - The second one consists in adapting the `FlightEntity` this method fetches internally to a `Flight` instance. You will also need to adapt the `CityEntity` instances to the `City` records from `D_Flight-business-process` to do that. 

Now that you have refactored the returned type of the `fetchFlight()` method, you can remove the adaptation you added in the `FlightMonitoring.monitorFlight()` and the `FlightMonitoring.followFlight()` methods in the module `D_Fligh-business-process`. The object you get has the right type. 

### Fixing the Main Module

Fixing the `E_Main` module consists in two things.

First, the `Main` class does not compile anymore. You should now create instances of `FlightID` instead of `FlightPK` to make the code compile.

Second, you should get rid of this ugly call to `FlightMonitoring.getInstance()`, and delete this method from `FlightMonitoring`.  

Third, remember that your business class `FlightMonitoring` depends on three services that are not initialized anymore. Well, this is where you should instantiate these classes and pass them when constructing your `FlightMonitoring` instance. 

All the instantiations of the interfaces you created should be done there. Once you have these instances, you should pass them to the `FlightMonitoring` class constructor. You will need to add dependencies to this module. This module has a dependency to all the other modules of this application. This `E_Main` module is the only module that knows all the implementation details of your application. 

In the end, creating your `FlightMonitoring` instance should look like this.

```java
DBService dbService =
        new FlightDBService();
FlightGUIService guiService =
        new FlightGUI();
PriceMonitoringService monitoringService =
        new FlightPriceMonitoringService();
var flightMonitoring = 
        new FlightMonitoring(
                dbService, 
                guiService, 
                monitoringService);
```

Note that we are using a dependency injection pattern here. The `FlightMonitoring` class declares the dependencies it needs in its constructor, and we are using it to send them to it. 

You do not need these `getInstance()` methods anymore, because you are now controlling the instantiations of your service classes in this class. You are providing the implementations of the services that your objects need by injecting them through their constructor. These classes do not have to fetch these implementations themselves. 

You do not need any smart implementation of the Singleton pattern, because you are the only one who is calling these constructors, and you are doing it only once.

The constructor of your `FlightMonitoring` class should look like this. You can now make these fields final. 

```java
public class FlightMonitoring {
    private final DBService dbService;
    private final PriceMonitoringService priceMonitoringService;
    private final FlightGUIService flightGUIService;

    public FlightMonitoring(
            DBService dbService,
            FlightGUIService guiService,
            PriceMonitoringService monitoringService) {
        this.dbService = dbService;
        flightGUIService = guiService;
        priceMonitoringService = monitoringService;
    }
}
```

The last thing you need to do in order to have a working application is to launch the two services that update the prices in the background, and that displays automatically the prices you chose to follow. You need to call the two corresponding methods from your main method:

```java
monitoringService.updatePrices();
flightMonitoring.launchDisplay();
```

At this point the `updatePrices()` is not declared on the `PriceMonitoringService` interface. So you may need to add it now.

### Fixing the Prices

One thing was left aside: the updating of the prices.

At this point, if you followed the instructions, you should have two stub methods in your `Flight` record: 
- `updatePrice(Price price)`, and
- `price()`. 

You cannot make a record modifiable, and the price of a flight changes over time. So you cannot make the price a component of your record. 

Since the price is updated by an external service that calls `updatePrice()`, you can store it in a registry, and read it when you need it. Remember that this is happening in different threads, so your registry need to be thread-safe. 

In that case, your `Flight` record could look like the following.

```java
public record Flight(City from, City to) {

    public Flight {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
    }

    private static final Map<Flight, Price> pricePerFlight =
            new ConcurrentHashMap<>();

    public Price price() {
        return pricePerFlight.get(this);
    }

    public void updatePrice(Price price) {
        pricePerFlight.put(this, price);
    }
}
```


## Step 2: Launching the Application Again

You can access the state of the code base at this step by checking out the branch `DOP_Refactor-step01` of this Git repository.

At this point, you should be able to launch your application properly again. All your dependencies has been fixed. Your application is organized around your Business module, all the other modules are isolated behind interfaces. Moreover, your modules are isolated from one another. 

```shell
Fetching flight FlightID[id=PaAt]
Monitoring the price for FlightID[id=PaAt]
Fetching flight FlightID[id=AmNY]
Monitoring the price for FlightID[id=AmNY]
Fetching flight FlightID[id=LoMi]
Monitoring the price for FlightID[id=LoMi]
Fetching flight FlightID[id=FrWa]
Monitoring the price for FlightID[id=FrWa]
Fetching flight FlightID[id=PaAt]
Fetching flight FlightID[id=AmNY]
Fetching flight FlightID[id=LoMi]
Fetching flight FlightID[id=FrWa]
Flight from Paris to Atlanta: price is now 101
Flight from Francfort to Washington: price is now 105
Flight from Amsterdam to New York: price is now 113
Flight from London to Miami: price is now 110
```


## Step 3: Moving to a Data Oriented Programming Approach

You can start working on this step by checking out the branch `DOP_Refactor-step01` of this Git repository.

The first idea is to model flights with an interface that capture the nature of the `Flight` type. And second, for each kind of flight, you create a concrete type, in the form of a record, that extend the `Flight` type. These records do not carry any behavior, only non-modifiable state. You will see how you can implement some modifiable state, and how you can add behavior without adding any method.

In this step, you will only have one implementation for the `Flight` type, so it will look weird to do this refactoring. But it is actually an intermediate step to add another implementation later. 

### Creating Sealed Interfaces

Everytime you need a type, your reflex should be to create a sealed interface. 

In this context `Flight` and `FlightID` become two sealed interfaces, with only one permitted implementation each.

There are two types you need to update:
- `FlightID`, that should become `SimpleFlightID`,
- and `Flight`, that should become `SimpleFlight`.


Here is the code for the `Flight` hierarchy.

```java
public sealed interface Flight
        permits SimpleFlight {
}
```

```java
public record SimpleFlight(SimpleFlightID id, City from, City to) 
        implements Flight {

    public SimpleFlight {
        Objects.requireNonNull(id);
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
    }
}
```

You need to adapt this pattern of code to the `FlightID` hierarchy.

```java
public sealed interface FlightID
        permits SimpleFlightID {
}
```

```java
public record SimpleFlightID(String id) 
        implements FlightID {

    public SimpleFlightID {
        Objects.requireNonNull(id);
    }
}
```

Doing that breaks many things in your application, but it's not actually too bad, because each fix is very simple, and clearly localized within your code, thanks to the error the compiler is giving you. 

### Fixing the FlightGUI Class

The `FlightGUI.displayFlight()` method takes a `Flight` as a parameter. 

Instead of calling methods on your types, the Data Oriented Programming approach consists in using pattern matching to check for the concrete type of the object you get. Then, depending on this type, you decide on what to do with this object. 

This is usually done with a `switch`, that is very simple in this case, since you have only one concrete type. `Flight` is a sealed interface, so the compiler knows all the concrete types that `Flight` has. If you miss something, it will tell you. 

A first way of writing this method would be the following. Is uses the _type pattern_. 

```java
public void displayFlight(Flight flight) {
    switch (flight) {
        case SimpleFlight simpleFlight -> System.out.println(
                "Flight from " + simpleFlight.from().name() + 
                " to " + simpleFlight.to().name() +
                ": price is now " + simpleFlight.price().price());
    }
}
```

But you have another kind of pattern, called _record pattern_, that deconstructs the record you have and gives you its components. 

```java
public void displayFlight(Flight flight) {
    switch(flight) {
        case SimpleFlight(City from, City to) -> System.out.println(
                "Flight from " + from.name() + " to " + to.name() +
                ": price is now " + ((SimpleFlight)flight).price().price());
    };
}
```

The `from` and `to` variables are called _pattern variables_. They are created by calling the corresponding accessors of your `SimpleFlight` record. 

Several points are worth noting.
1. The deconstruction allows you to have a simpler code for the printing of the information.
2. Because you used a sealed interface, this `switch` does not need any `default` branch. Actually you should not put any. If you do, and add more implementations of `Flight` in the future, then the compiler will not be able to tell you that you forgot a case.
3. All the different ways of printing your different objects are at the same place in your application, making its writing and maintenance easier.
4. For now, you can only deconstruct records to get their components. This may change in the future.

You can also add a static method to `SimpleFlight`: `price()`, that takes the `flight` as a parameter. These two methods are built on the same pattern of code. They simply store the price of the flight in a registry. Here is the method for the `SimpleFlight` record. 

To avoid having to use an object that could be complex as a map key, you could also use its ID. For that, you can add a `SimpleFlightID` as the first component of the `SimpleFlight` record. 

Your record becomes the following: 

```java
record SimpleFlight(SimpleFlightID id, City from, City to) {}
```

```java
private static final Map<SimpleFlightID, Price> pricePerFlight
        = new ConcurrentHashMap<>();

public static Price price(SimpleFlightID id) {
    return pricePerFlight.get(id);
}

public static void updatePrice(SimpleFlightID id, Price price) {
    pricePerFlight.put(id, price);
}
```

With all this, the `displayFlight()` becomes the following. 

```java
public void displayFlight(Flight flight) {
    switch(flight) {
        case SimpleFlight(SimpleFlightID id, City from, City to) -> System.out.println(
                "Flight from " + from.name() + " to " + to.name() +
                ": price is now " + SimpleFlight.price(id).price());
    };
}
```

### Fixing the FlightDBService Class

The database module defines entities and primary keys. Entities cannot be part of any sealed hierarchy, because they cannot be final. But primary keys can be. Plus, they can be made non-modifiable. 

Let us refactor this model using sealed interfaces to model our primary keys. 

For that, you can create the following sealed interface. 

```java
public sealed interface FlightPK
        permits SimpleFlightPK {
}
```

The corresponding primary key is the following.

```java
public final class SimpleFlightPK implements FlightPK {
    private String flightId;

    // the content of the class
}
```

Fixing the `FlightDBService` class can now follow the exact same pattern as the previous one, based on the use of `switch`. 

First, you need to adapt the `FlightID` instance you get to a `FlightPK` instance. 

```java
var flightPK = switch (flightId) {
    case SimpleFlightID(String id) -> new SimpleFlightPK(id);
};
```

Then you can create your `FlightEntity` with another `switch`. Note that you cannot use a deconstruction pattern for `SimpleFlightPK`, because this class is not a record. You can make it a record though, or wait until Java supports the deconstruction of regular classes, something that should come in the future.  

```java
var flightEntity = switch (flightPK) {
    case SimpleFlightPK simpleFlightPK -> simpleFlights.computeIfAbsent(
            flightPK,
            _ -> {
                var from = simpleFlightPK.flightId().substring(0, 2);
                var to = simpleFlightPK.flightId().substring(2);
                return new SimpleFlightEntity(
                        flightPK,
                        cities.get(from), cities.get(to),
                        new PriceEntity(100), new PlaneEntity("Airbus A350"));
            });
};
```

Note that we are using an unnamed parameter for the function that `computeIfAbsent()` takes as a second parameter, because we are not using it in the implementation. 

The registry `simpleFlights` is the map that holds all the simple flights that this system supports. It is there to simulate a real database. Here is its declaration. It is not meant to support any other type than `SimpleFlightPK` or `SimpleFlightEntity`. 

```java
private static Map<SimpleFlightPK, SimpleFlightEntity> simpleFlights = new HashMap<>();
```

The last step is to adapt this `SimpleFlightEntity` into a `SimpleFlight` transport record, and return it. 

You have actually two solution for this particular `switch`. The first one consists in switching over the types of the `flightEntity` object, that is the entity itself. It would give you the following. 

```java
return switch (flightEntity) {
    case SimpleFlightEntity simpleFlightEntity -> {
        var from = new City(simpleFlightEntity.from().name());
        var to = new City(simpleFlightEntity.to().name());
        yield new SimpleFlight(new SimpleFlightID(simpleFlightEntity.id().flightId()), 
                               from, to);
    }
};
```

The second solution consists in switching over the types of the `flightPK` object, that is the primary key of your entity. It would give you the following. 

```java
return switch (flightPK) {
    case SimpleFlightPK _ -> {
        var simpleFlightEntity = (SimpleFlightEntity)flightEntity; 
        var from = new City(simpleFlightEntity.from().name());
        var to = new City(simpleFlightEntity.to().name());
        var id = new SimpleFlightID(simpleFlightEntity.id().flightId());
        yield new SimpleFlight(id, from, to);
    }
};
```

You may be thinking that the first solution is actually safer, since you have an unchecked cast in the second one that could fail. In fact even if it is unchecked it cannot fail, because the type of the entity is bound to the type of the primary key. 

The biggest difference between both solutions is that the first one does not compile. It is a switch on types that you cannot seal, because they are entities, and entities cannot be final classes. So whatever you do, this hierarchy is open. The only solution to have an exhaustive `switch` in that case is to add a `default` branch, something that you want to avoid, because in the case where you add new types in your hierarchy, the compiler cannot tell you that this `switch` misses some cases.

The second solution on the other hand, is a `switch` on the primary key type, which is sealed. So it compiles without a `default` branch. Not adding one is what you want to do, because you want the compiler to tell you that this `switch` misses a branch in case you add a new type of primary key.  

### Fixing the FlightMonitoring Class

The issue in this class is the `FlightConsumer` construction. This consumer needs to update the price of a flight. It is done by updating a registry, that is held in the `SimpleFlight` record. 

You need a consumer that is specific to the `SimpleFlight` type, that you can create in a `switch`, with the following pattern. 

```java
public void followFlight(FlightID flightID) {
    var flight = dbService.fetchFlight(flightID);
    FlightConsumer flightConsumer = price -> {
        switch(flight) {
            case SimpleFlight(SimpleFlightID id, City _, City _) -> 
                    SimpleFlight.updatePrice(id, price);
        }
    };
    priceMonitoringService.followPrice(flightID, flightConsumer);
}
```

Note that you are using a record pattern to deconstruct your simple flight, and an unnamed pattern to avoid the construction of the two `City` components. 

So the declaration of the task that update all the prices may look like the following.

```java
Runnable task = () -> {
    for (var entry : registry.entrySet()) {
        var flightId = entry.getKey();
        var flightConsumer = entry.getValue();
        flightConsumer.updateFlight(flightId, new Price(random.nextInt(80, 120)));
    }
};
```

### Fixing the Main Class

The last element you need to update is your main class. Since `FlightID` is now an interface, you need to change this code. You can just replace your primary keys creation by the following:

```java
var f1 = new SimpleFlightID("PaAt");
var f2 = new SimpleFlightID("AmNY");
var f3 = new SimpleFlightID("LoMi");
var f4 = new SimpleFlightID("FrWa");
```


## Step 4: Launching the Application Again

You can access the state of the code base at this step by checking out the branch `DOP_Refactor-step02` of this Git repository.

At this point, you should be able to launch your application properly again. All your application now follows the Data Oriented Programming paradigm, and you are probably wondering why, since you have just one type of flight. It looks pretty useless, no? 

Well, bear with me, because the next step is to add another type of flight. This refactoring will be a breeze. 

In the meantime, here is the output of the execution of the application. 

```shell
Fetching flight SimpleFlightID[id=PaAt]
Monitoring the price for SimpleFlightID[id=PaAt]
Fetching flight SimpleFlightID[id=AmNY]
Monitoring the price for SimpleFlightID[id=AmNY]
Fetching flight SimpleFlightID[id=LoMi]
Monitoring the price for SimpleFlightID[id=LoMi]
Fetching flight SimpleFlightID[id=FrWa]
Monitoring the price for SimpleFlightID[id=FrWa]
Fetching flight SimpleFlightID[id=PaAt]
Fetching flight SimpleFlightID[id=AmNY]
Fetching flight SimpleFlightID[id=LoMi]
Fetching flight SimpleFlightID[id=FrWa]
Flight from Paris to Atlanta: price is now Price[price=101]
Flight from Francfort to Washington: price is now Price[price=105]
Flight from Amsterdam to New York: price is now Price[price=113]
Flight from London to Miami: price is now Price[price=110]
```


## Step 5: Adding the Support for Multileg Flights

A new business requirement has to be implemented: instead of supporting simple flights between cities, you need to support multileg flights, with an intermediate city between the departure and the destination.

In an Object Oriented context, you would probably refactor your object model with an `AbstractFlight` class, and extensions to support that. It is doable but would not work very well in our case, because the real object model of this application is hidden in the `A_Database` module, no other module knows about it, and we certainly do not want anybody to depend on this module. All we have are transport objects, implemented with record, that carry their state, but no behavior.

In the Data Oriented Programming approach, supporting this new requirement is actually easy, with minimal refactoring.

Let us just add this `MulitlegFlight` record in the sealed `Flight` hierarchy. 

```java
public record MultilegFlight(MulitlegFlightID id, City from, City via, City to) 
        implements Flight {

    public MultilegFlight {
        Objects.requireNonNull(id);
        Objects.requireNonNull(from);
        Objects.requireNonNull(via);
        Objects.requireNonNull(to);
    }
}
```

You also need the `MulitlegFlightID` record to model your new key. 

```java
public record MultilegFlightID(String id) implements FlightID {}
```

Do not forget to update the `permits` clause of the `Flight` and `FlightID` sealed interfaces, to add these new implementations. 

Since all your code is based on the use of `switch` on a sealed type, modifying this sealed type and adding a new extension to it triggers a compiler error. 
This `switch` checks for exhaustiveness, and if the compiler detects a missing case, then it can tell it to you. 

So at this point, all you need to do is to visit each of your modules, and add the support for this new kind of flight, following the Java compiler's advice. 

### Fixing the FlightGUI Class

Fixing this class consists in adding a branch in your switch statement. 

```java
switch (flight) {
    case SimpleFlight simpleFlight -> /* this code is not modified */
    case MultilegFlight multilegFlight -> System.out.println(
            "Flight from " + multilegFlight.from().name() + 
            " to " + multilegFlight.to().name() +
            " via " + multilegFlight.via().name() +
            ": price is now " + MultilegFlight.price(multilegFlight).price());
}
```

You can follow the same pattern as the previous one, in which case you will need to add a `price()` static method in your `MultilegFlight` record. The pattern of this method is the same as the one in your `SimpleFlight` record, so you can just copy it. You can even copy the `update()` method, that you will need later. 

Note that, since you know that you have a `MultilegFlight` instance when you call this method, you do not need to use polymorphism here, neither in the declaration of your map, nor in the declarations of your method. 

```java
private static final Map<MultilegFlightID, Price> pricePerFlight =
        new ConcurrentHashMap<>();

public static Price price(MultilegFlight flight) {
    return pricePerFlight.get(flight.id());
}

public static void updatePrice(MultilegFlightID id, Price price) {
    pricePerFlight.put(id, price);
}
```

### Fixing the FlightDBService Class

The second place where you need to fix your `switch` is the `FlightDBService` class. Here you have two `switch` to fix, and actually a third one that is going to break as soon as you fix the first one. 

First, you need to update your object model to support this new entity. Here, we suppose that it involves the creation of the new primary key class, and a new entity object. 

You can create a `MultilegFlightPK` class along with the `SimpleFlightPK` class that you created at the previous step. 

```java
public final class MultilegFlightPK implements FlightPK {
    private String flightId;

   // the content of the class
}
```

And then you can create an entity for this multileg flight: `MultilegFlightEntity`, with everything you need in it.

From there, you can refactor your first `switch`, which become the following. It consists in just adding the code you need to support your new primary key class. 

```java
var flightPK = switch (flightId) {
    case SimpleFlightID(String id) -> new SimpleFlightPK(id);
    case MultilegFlightID(String id) -> new MultilegFlightPK(id);
};
```

The second `switch` you need to refactor concerns creating your new entity and persisting it. In this example, the database is just a concurrent map. 

```java
var flightEntity = switch (flightPK) {
   case SimpleFlightPK simpleFlightPK -> { /* this is the existing code */}
   case MultilegFlightPK multilegFlightPK -> multilegFlights.computeIfAbsent(
           multilegFlightPK,
           _ -> {
              var from = multilegFlightPK.flightId().substring(0, 2);
              var via = multilegFlightPK.flightId().substring(2, 4);
              var to = multilegFlightPK.flightId().substring(4);
              return new MultilegFlightEntity(
                      multilegFlightPK,
                      cities.get(from), cities.get(via), cities.get(to),
                      new PriceEntity(100), new PlaneEntity("Airbus A350"));
           });
};
```

The third and last `switch` you need to refactor consists of adapting your `MultilegFlightEntity` entity to the transport record `MultilegFlight`. 
Once again, all you need to do is to add the support for the new types. 

If you chose the solution where you switch on the type of the primary key, then you need to add a case for `MultilegFlightPK`. 

```java
return switch (flightPK) {
    case SimpleFlightPK _ -> { /* this is the existing code */ }
    case MultilegFlightPK _ -> {
        var multilegFlightEntity = (MultilegFlightEntity)flightEntity;
        var from = new City(multilegFlightEntity.from().name());
        var via = new City(multilegFlightEntity.via().name());
        var to = new City(multilegFlightEntity.to().name());
        var id = new MultilegFlightID(multilegFlightEntity.id().flightId());
        yield new MultilegFlight(id, from, via, to);
    }
};
```

If you chose the other solution, then the code is almost the same. The `default` branch is needed for this code to compile, as the type of the entity is not sealed. Using this `switch` defeats the purpose of having a `switch` on types, I do not recommend it. 

```java
return switch (flightEntity) {
    case SimpleFlightEntity simpleFlightEntity -> { /* this is the existing code */ }
    case MultilegFlightEntity multilegFlightEntity -> {
        var from = new City(multilegFlightEntity.from().name());
        var via = new City(multilegFlightEntity.via().name());
        var to = new City(multilegFlightEntity.to().name());
        var id = new MultilegFlightID(multilegFlightEntity.id().flightId());
        yield new MultilegFlight(id, from, via, to);
    }
    default -> throw new IllegalStateException("Unexpected value: " + flightEntity);
};
```

### Fixing the FlightMonitoring Class

There is a `switch` that needs some attention in the `FlightMonitoring` class, in the method `followFlight()`. The consumer that update the price of your flight needs to handle the case of multileg flights. 
The fix is pretty straightforward, since you already created the `MultilegFlight.updatePrice()` method earlier.  

```java
FlightConsumer flightConsumer = price -> {
    switch(flight) {
        case SimpleFlight(SimpleFlightID id, City _, City _) -> 
                SimpleFlight.updatePrice(id, price);
        case MultilegFlight(MultilegFlightID id, City _, City _, City _) -> 
                MultilegFlight.updatePrice(id, price);
    }
};
```

### Fixing the Main Class

The `Main` class doesn't really need to be fixed, since it is compiling properly. But you can add some multileg flights to the flight you want to monitor. 


```java
var f1 = new SimpleFlightID("PaAt");
var f2 = new SimpleFlightID("AmNY");
var f3 = new MultilegFlightID("LoPaMi");
var f4 = new MultilegFlightID("FrLoWa");
```


## Step 4: Launching the Application Again

You can access the state of the code base at this step by checking out the branch `DOP_Refactor-step03` of this Git repository. You can launch your application again, and it should work in the same way as previously.

```shell
Fetching flight SimpleFlightID[id=PaAt]
Monitoring the price for SimpleFlightID[id=PaAt]
Fetching flight SimpleFlightID[id=AmNY]
Monitoring the price for SimpleFlightID[id=AmNY]
Fetching flight MultilegFlightID[id=LoPaMi]
Monitoring the price for MultilegFlightID[id=LoPaMi]
Fetching flight MultilegFlightID[id=FrLoWa]
Monitoring the price for MultilegFlightID[id=FrLoWa]
Fetching flight SimpleFlightID[id=PaAt]
Fetching flight SimpleFlightID[id=AmNY]
Fetching flight MultilegFlightID[id=LoPaMi]
Fetching flight MultilegFlightID[id=FrLoWa]
Flight from Paris to Atlanta: price is now 101
Flight from London to Miami via Paris: price is now 105
Flight from Francfort to Washington via London: price is now 113
Flight from Amsterdam to New York: price is now 110
```

Congratulations, you have reached the end of this hands on lab! Thank you for sending us comments, corrections, or any feedback you may have. 