## Records

This second part is about records, a new language construct to model your immutable data, in the form of named tuples, added to the JDK 16.

### Simple Records

This first set of exercises shows you the first basic elements you need to know on records:
- how you can create a record class,
- how you can add a custom constructor,
- how you can make a record comparable and sort records in a list,
- how you can define the constructor of a record, in its normal form or its compact form,
- how you can add some validation rules on your records.

### Less Simple Records

This second set makes you create a `Range` record, that implements the `Iterable` interface and some validation rules.

### Harder Records

This third set makes you use defensive copy to ensure the immutability of the internal state of your record. 
A record may be built on mutable components, that should be copied when you create your record, and when you return the value of such a component.

It then makes you create your own `equals()` and `hashCode()` methods to define your own record identity.

### Hard Records

This fourth set makes you analyze a CSV text file containing US cities, along with some more information. You will map each line of this file in a `City` record and compute various things on the list of cities that you will get:
- sort them in the alphabetical order,
- find the city with the largest population,
- find the states references in this file,
- then build the histogram of the population per state,
- and lastly find the most populated state.
  This set makes you use records with the Stream API.

### Challenge Records

This last set of exercises invites you to rewrite the code of the previous set, using records everywhere you can think it suits to have them. 
Creating a record is (almost) free and can greatly improve the readability of your core, especially when you are writing complex data processing algorithms. 

## References

- JEP 395 Records: https://openjdk.org/jeps/395
