## Sealed Types and Pattern Matching

This third part introduces a new paradigm that you can use to write your applications, called Data Oriented Programming. Data Oriented Programming is implemented in Java with three tools: records, sealed types, and pattern matching. It starts with a simple, classical object model: a `Shape` interface and two implementations: `Rectangle` and `Square`. 

Let's take a look at the `Shape` interface and its two implementations: `Circle` and `Square`. You also have a record named `Rectangle`, that we will use later in this tutorial. 

### Computing the Surface of a Shape From the Interface

The first step of this tutorial is to implement the `computeSurface()` method of the `ShapeProcessor` class. 
The first idea that comes to mind would probably to add a `surface()` method to the `Shape` interface, and simply call this method from the `computeSurface()` method. 
The tutorial offers you the code to do that: you just need to add the `surface()` method to the `Shape` interface. 
The implementations of the method is already present in the records `Circle` and `Square`. 

Adding a `surface()` method in the `Shape` interface is natural in Java. It comes with some safety: if you had a new method to an interface, the compiler tells you immediately in which classes you need to implement it. 

But it also comes with a cost. As functional requirements are added to the `Shape` interface, new methods are added, and odds are that this simple interface will become very complex, very soon. 
Also, if a feature is not needed anymore, because all your client code depends on this interface, removing the corresponding method may become costly. 

Not removing this code leads to the creation of dead code. That is, some code that is there, but that serves no business purpose. Dead code still has a maintenance cost in an application. You still need to compile it and to run the corresponding tests when you build it. 

Another annoying point: odds are that your business code that needs to compute areas of shapes and thus depends on this interface, will not need 100% of the available methods in it. 
An update of this interface will have you recompile parts of your application for no real reason if this update brings no value to this business code.  

### Computing the Surface of a Shape From the ShapeProcessor Class

Let's move this code to the `ShapeProcessor` class. You can now remove the `shape()` method from the `Shape` interface and its implementations. 

#### Using Pattern Matching for InstanceOf

The first step consists in moving this method outside your interface and its implementations. 

This first, naive version could be the following: 

```java
public double computeSurface(Shape shape) {
    if (shape instanceof Square) {
        Square square = (Square)shape;
        return square.edge()*edge.square();
    } else if (shape instanceof Circle) {
        Circle circle = (Circle)shape;
        return Math.PI*circle.radius()*circle.radius();
    }
    return 0d;
}
```

Not only this code is ugly, it also loses the safety the previous code had: forgetting a case in the _if-else_ code cannot be spot by the compiler anymore. 
Plus, there is no way you can unit test 100% of this code, because the `return 0;` is just there to make the compiler happy: this line of code cannot be executed. 

You can make this code more readable by using _pattern matching for instanceof_. 
Instead of checking the variable `shape` against the type `Square` and `Circle`, you can check it against two kind of patterns. 

1. You can check it against a _type pattern_, using the following syntax:
```java
if (shape instanceof Square square) {
    // you can use square here
} 
```
That creates a _pattern variable_, `square`, that you can use wherever this variable makes sense, including the _if_ branch. 
Try to refactor the `computeSurface(Shape)` using this pattern, and see how it can improve the readability of your code. 

2. Starting with the JDK 21, you can also check it against a _record pattern_, using the following syntax: 
```java
if (shape instanceof Square(double edge)) {
    // you can use radius here
}
```
That creates a `edge` pattern variable, that takes the value of the edge of this square. 
You can also use this syntax on records that are built on several components, thus creating more than one pattern variable. 
Note that these pattern variables are initialized by calling the accessors of your record. 
This point is important in the case where your accessors are doing some defensive copy, for instance.
Try to refactor the `computeSurface(Shape)` using this second pattern, and see how it can further improve the readability of your code. 

Using _pattern matching for instanceof_ is nice, but it does not fix our first problem : the compiler does not help you in case for forgot to process an implementation of `Shape`.

#### Using Pattern Matching for Switch

This next step consists in using a switch expression to get rid of this _if-else_ structure. 

This block of _if-else_ has the structure of a switch. Fortunately, since JDK 21, you can switch on types, and use patterns as switch cases. 
Try to refactor the previous code so that it looks like the following: 

```java
switch (shape) {
    case Square square -> ...;
    case Circle circle -> ...;
}
```

You can use both _type patterns_ or _record patterns_ for your switch cases. Try them both, and see which one you prefer. 

The code you will end up is more readable than the old-fashioned _if-else_, thanks to using _switch expressions_ (a feature added in the JDK 14). 
But, to make the compiler happy, you still need to add a `default` case to it. 

#### Using Sealed Types

This third step consists in getting rid of this default case, using a sealed interface. 

The fact is: you can tell the compiler that there is no other type than `Circle` and `Square` to implement the `Shape` interface, by _sealing_ this interface. 
_Sealed classes_ is a feature added to the JDK 17, that is very useful in this case. 
You can now seal any type in Java: interfaces, classes and abstract classes. 

- A sealed interface needs to declare its implementations. 
- A sealed abstract class needs to be sealed, or _non-sealed_. This is a way to create an extension point in an otherwise sealed hierarchy.
- A sealed class needs to be final, sealed, or _non-sealed_.

When you seal a type, you need to tell the compiler what are the types allowed to extend this type. There are two ways to do that: 
1. either you add a `permits` clause in the declaration of your type,
2. or you do not use this clause, but create auxiliary or nested types in this type, and the compiler will infer that these are the permitted types. 

You can create a non-sealed class by just adding the non-sealed declaration to it. 

```java
public non-sealed abstract class AbstractShape implements Shape {
}
```

Try to seal the `Shape` interface. It should look like the following: 

```java
public sealed interface Shape permits ... {
}
```

After the `permits` keyword, you should have a coma separated list of the types allowed to implement `Shape`. 

Do not forget to make `Circle` and `Square` to implement `Shape`, of course. 

Now the compiler knows that a `Shape` object cannot be anything else than a `Circle` object or a `Square` object, and it can use this information when you create a switch expression 1on the `Shape` type.

Once you have sealed your `Shape` interface, try to remove the `default` case in your switch. You should see that this code is still compiling. 

There is a `Rectangle` record available in the `model` package. Try to make it implement the `Shape` interface. Try to use _record patterns_ for your switch case, to make your code even simpler. Remember that you can have more than one _pattern variable_ in a pattern, this is something you can use for the `Rectangle` record, that has two components. 

You should see a compiler error if you do that before you add `Rectangle` to the list of the permitted class. 

Now that you have added an implementation to this sealed hierarchy, you should see a compiler error in your switch expression. The compiler is helping you again by telling you that you forgot a case in this switch. Just as it was helping you by telling you that you forgot to implement a method from an interface, in an implementing class. Your code is safe again. 

#### Nested Patterns and Unnamed Patterns

Suppose that you need to add a component `center` to your circle, of type `Point`. Add this component to your `Circle` record. 

If you used a record pattern to deconstruct your `Circle` in your switch expression, you should now see a compiler error. Using record patterns allows the compiler to check for the modification of your records. If you change your object model, the compiler sees it, and can use this information to help you. 

To fix this code, you need to add the `center` component to your record pattern. You can do it in two ways. The first one is the following. 

```java
switch(shape) {
    case Circle(Point center, double radius) -> ...
}
```

For such a scenario, you create a `center` pattern variable. From it, you can get the coordinates of this point. 

But you can also nest your record patterns in that way.

```java
switch(shape) {
    case Circle(Point(int px, int py), double radius) -> ...
}
```

In that case, you can deconstruct your circle and its center in one pass. 

Note that, in this last case, you do not need to create this pattern variable. 
Remember that creating a pattern variable calls the corresponding accessor, which can be costly, if, for instance, this accessor is doing some defensive copy. 

So you can also use the _unnamed pattern_ while deconstructing your circle, in that way. 

```java
switch(shape) {
    case Circle(Point _, double radius) -> ...
}
```

## References

- JEP 395 Records: https://openjdk.org/jeps/395
- JEP 395 Pattern Matching for instanceof: https://openjdk.org/jeps/394
- JEP 440 Record Patterns: https://openjdk.org/jeps/440
- JEP 441 Pattern Matching for switch: https://openjdk.org/jeps/441
- JEP 456 Unnamed Variables & Patterns: https://openjdk.org/jeps/456
