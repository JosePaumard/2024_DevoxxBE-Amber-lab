package org.paumard.solutions.hol.record;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class C_HarderRecord {

    /**
     * The content of these classes has just been added to make the code
     * compile. All this content should be removed when you make these classes records.
     */
    record State(String name, List<City> cities) {

        public State(String name, List<City> cities) {
            this.name = name;
            this.cities = List.copyOf(cities);
        }

        public State(String name) {
            this(name, List.of());
        }

        public List<City> cities() {
            return new ArrayList<>(cities);
        }
    }

    record City(String name, int population) {
        public City {
            if (population < 0) {
                throw new IllegalArgumentException("The population cannot be negative");
            }
        }

        public boolean equals(Object other) {
            return other instanceof City city &&
                   this.name.equals(city.name);
        }

        public int hashCode() {
            return this.name.hashCode();
        }
    }

    /// Make the State class a Record. Remove the `name()` method in it.
    /// Create an instance of this record and make the test pass.
    @Test
    public void c_record01() {

        State state = new State("Georgia");

        assertThat(state.name()).isEqualTo("Georgia");
    }
    // Hint:
    // <editor-fold defaultstate="collapsed">
    // Replace the existing State class with a record.  
    // </editor-fold>

    /// Make the City class a Record. Remove the `name()` and the `population()`
    /// methods in it.
    ///
    /// Create an instance of this record and make the test pass.
    @Test
    public void c_record02() {

        City city = new City("Atlanta", 506_811);

        assertThat(city.name()).isEqualTo("Atlanta");
        assertThat(city.population()).isEqualTo(506_811);
    }
    // Hint:
    // <editor-fold defaultstate="collapsed">
    // Replace the existing City class with a record.  
    // </editor-fold>

    /// Modify the City record so that you cannot create any city with
    /// a negative number for the population.
    ///
    /// Uncomment the argument of the constructor call.
    /// Your code should still compile.
    @Test
    public void c_record03() {

        assertThatIllegalArgumentException()
                .isThrownBy(() -> new City("Atlanta", -1));
    }
    // Hint:
    // <editor-fold defaultstate="collapsed">
    // You can add a validation rule to a record by writing either a
    // canonical constructor of a compact constructor.
    // </editor-fold>


    /// Add the component `cities` to the `State` record.
    /// It may make the code you have written for the previous exercises fail,
    /// so you may need to modify the `State` record further.
    ///
    /// This component is a list, that can be modifiable. Modify the record
    /// to prevent any outside modification of the internal state of your
    /// `State` instances. In other words: make your record store
    /// a defensive copy of the `cities` component.
    @Test
    public void c_record04() {

        City atlanta = new City("Atlanta", 506_811);
        City augusta = new City("Augusta", 195_182);
        var cities = new ArrayList<City>();
        cities.add(atlanta);
        cities.add(augusta);
        State georgia = new State("Georgia", cities);

        City athens = new City("Athens", 115_452);

        assertThat(georgia.cities()).containsExactly(atlanta, augusta);
        cities.add(athens);
        assertThat(georgia.cities()).containsExactly(atlanta, augusta);
        georgia.cities().add(athens);
        assertThat(georgia.cities()).containsExactly(atlanta, augusta);
    }
    // Hint:
    // <editor-fold defaultstate="collapsed">
    // When implementing a defensive copy, you should always remember that
    // this copy has to be made when you receive an argument, and when
    // you return it.
    // </editor-fold>


    /**
     * We would like to compare cities only by their names. Modify the
     * <code>City</code> record to implement this behavior.
     */
    @Test
    public void c_record05() {

        City marietta2000 = new City("Marietta", 58_748);
        City marietta2020 = new City("Marietta", 65_351);

        assertThat(marietta2000).isEqualTo(marietta2020);
    }
    // Hint:
    // <editor-fold defaultstate="collapsed">
    // You need to write your own <code>equals()</code> and <code>hashCode()</code> methods.
    // </editor-fold>
}
