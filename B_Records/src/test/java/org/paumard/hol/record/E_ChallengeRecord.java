package org.paumard.hol.record;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class E_ChallengeRecord {

    /**
     * These exercises are almost the same as the D_HardRecord exercises. The only difference
     * is that, instead of processing the CSV file with <code>String</code> and <code>Integer</code>,
     * you are going to process it using meaningful types: <code>City</code>, <code>Population</code>
     * and <code>State</code>. This will improve the readability of your code, by giving meaningful
     * names to the types you are using in it.
     * </p>
     * You should reuse the code you wrote for the D_HardRecord class.
     */
    String line = "37;Atlanta;Georgia;506 811;345,8";


    static class City {

        City(int id, String name, State state, Population population, LandArea landArea) {
        }

        int id() {
            return -1;
        }

        String name() {
            return "";
        }

        State state() {
            return null;
        }

        Population population() {
            return null;
        }

        LandArea landArea() {
            return null;
        }

        public static City of(String line) {
            return null;
        }

        public double density() {
            return 0d;
        }
    }

    static class State {
        State(String name) {
        }

        String name() {
            return "";
        }
    }

    static class Population {
        Population(int amount) {
        }

        int amount() {
            return -1;
        }
    }

    static class LandArea {
        LandArea(double amount) {
        }

        double amount() {
            return 0d;
        }
    }

    static class PopulatedState {

        PopulatedState(Map.Entry<State, Population> entry) {
        }

        State state() {
            return null;
        }

        Population population() {
            return null;
        }

        public static PopulatedState of(Map.Entry<State, List<City>> entry) {
            return null;
        }
    }

    /**
     * Make this <code>City</code> class a record. Create a static method that takes a line,
     * analyzes it and create the city instance with the arguments from this line. You can use
     * the code you write in the previous set of exercises. Be careful though, some of the types
     * have changed.
     */
    @Test
    @Disabled
    public void e_record01() {

        City city = City.of(line);

        assertThat(city.id()).isEqualTo(37);
        assertThat(city.name()).isEqualTo("Atlanta");
        assertThat(city.state().name()).isEqualTo("Georgia");
        assertThat(city.population().amount()).isEqualTo(506_811);
        assertThat(city.landArea().amount()).isCloseTo(3458d, Offset.offset(0.001));
    }
    // Hint:
    // <editor-fold defaultstate="collapsed">
    // You can analyze the line field by field using the <code>split()</code>
    // method of the <code>String</code> class.
    // </editor-fold>


    /**
     * Use the previous code to create the list of all the cities.
     */

    private List<City> cities = new ArrayList<>();

    @Test
    @Disabled
    public void e_record02() {

        Path path = Path.of("files/cities.csv");

        try (var lines = Files.newBufferedReader(path).lines()) {
            // TODO: add the code to map a line to a list here
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThat(cities).hasSize(317);
    }
    // Hint:
    // <editor-fold defaultstate="collapsed">
    // You should use the constructor you created in the previous exercise. Be careful
    // though, and look at the first two lines of the file.
    // </editor-fold>


    /**
     * Use the previous code to create a list of the states. You will need to
     * create a State record, and remove the State class.
     */

    private List<State> states = new ArrayList<>();

    @Test
    @Disabled
    public void e_record03() {

        states = null; // TODO: create the list

        assertThat(cities).hasSize(317);
        assertThat(states).hasSize(46);
    }
    // Hint:
    // <editor-fold defaultstate="collapsed">
    // You can use the Stream API to create the list of states from the list of cities.
    // </editor-fold>


    /**
     * Compute the total population living in these cities.
     */
    @Test
    @Disabled
    public void e_record05() {

        Population totalPopulation = null; // TODO: find the total population

        assertThat(totalPopulation.amount()).isEqualTo(88_231_877);
    }
    // Hint:
    // <editor-fold defaultstate="collapsed">
    // Try to use the <code>stream.reduce()</code> method to sum the
    // instances of <code>Population</code> directly.
    // </editor-fold>


    /**
     * Build a map in which the key are the states and the values
     * the sum of the population of the cities of this state.
     */

    private Map<State, Population> populationByState = new HashMap<>();

    @Test
    @Disabled
    public void e_record06() {

        populationByState = null; // TODO: build the map

        assertThat(populationByState.size()).isEqualTo(46);
        assertThat(populationByState.get(new State("Georgia")).amount()).isEqualTo(1_434_456);
        assertThat(populationByState.get(new State("New York")).amount()).isEqualTo(8_960_160);
        assertThat(populationByState.get(new State("Illinois")).amount()).isEqualTo(3_565_824);
    }
    // Hint:
    // <editor-fold defaultstate="collapsed">
    // You need to use the <code>Collectors.groupingBy()</code> collector to regroup the cities
    // byt state. You then need to use a downstream collector to map the cities to their population,
    // and another downstream collector to reduce the populations, just as you did in the previous
    // challenge.
    // You can create a factory method on the <code>City</code> record to make your code more
    // readable.
    // </editor-fold>


    /**
     * Find the most populated state from the map you have built in the previous challenge. Return the result
     * in an instance of <code>PopulatedState</code>.
     */
    @Test
    @Disabled
    public void e_record07() {

        PopulatedState mostPopulatedState = null; // TODO: find the most populated state

        assertThat(mostPopulatedState.state()).isEqualTo(new State("California"));
        assertThat(mostPopulatedState.population()).isEqualTo(new Population(18_976_671));
    }
    // Hint:
    // <editor-fold defaultstate="collapsed">
    // The first step of analyzing a histogram is to stream then entry set of the corresponding map.
    // You can then map this stream to a stream of <code>PopulatedState</code> instances, and then
    // get the max element of this stream.
    // You can create a factory method on the <code>PopulatedState</code> record to create the comparator
    // used to find this max element, to further improve the readability of this code.
    // </editor-fold>


    /**
     * Try to improve the readability of your code even further. Implement the different methods
     * of this challenge to make it work.
     */
    @Test
    @Disabled
    public void e_record08() {

        // TODO: remove the '= null' and uncomment the following lines.
        //       Create the elements you need to make the code compile
        PopulatedState mostPopulatedState = null;
//                Cities.from(cities)
//                        .groupedByState()
//                        .getMostPopulatedCity();

        // TODO: remove the '= null' and uncomment the following lines.
        //       Create the elements you need to make the code compile
        PopulatedState leastPopulatedState = null;
//                Cities.from(cities)
//                        .groupedByState()
//                        .getLeastPopulatedCity();

        assertThat(mostPopulatedState.state()).isEqualTo(new State("California"));
        assertThat(mostPopulatedState.population()).isEqualTo(new Population(18_976_671));
        assertThat(leastPopulatedState.state()).isEqualTo(new State("Alaska"));
        assertThat(leastPopulatedState.population()).isEqualTo(new Population(288));
    }
    // Hint:
    // <editor-fold defaultstate="collapsed">
    // You can use the code that you already wrote, and wrap it in records to hide the technical
    // details and expose the high level functionalities.
    // </editor-fold>


    /**
     * Find the cities that have the lowest and highest density of population in this file.
     */
    @Test
    @Disabled
    public void e_record09() {

        City mostDenseCity = null; // TODO: find the most dense city

        City leastDenseCity = null; // TODO: find the least dense city

        assertThat(mostDenseCity.name()).isEqualTo("Seattle");
        assertThat(mostDenseCity.density()).isCloseTo(3473d, Offset.offset(0.5d));
        assertThat(leastDenseCity.name()).isEqualTo("Anchorage");
        assertThat(leastDenseCity.density()).isCloseTo(0.000651d, Offset.offset(0.000005d));
    }
    // Hint:
    // <editor-fold defaultstate="collapsed">
    // You can create any instance method you like on a record. Creating <code>density()</code> method on the
    // <code>City</code> record will help you solve this challenge.
    // </editor-fold>


    /// Build the histogram of the density for each state. This histogram is a `Map<State, Double>`
    /// where the key is the density of population of the corresponding state.
    @Test
    @Disabled
    public void e_record10() {

        Map<State, Double> densityByState = null; // TODO: build the histogram

        assertThat(densityByState).hasSize(46);
        assertThat(densityByState.get(new State("Georgia"))).isCloseTo(47.8d, Offset.offset(0.05));
        assertThat(densityByState.get(new State("New York"))).isCloseTo(822d, Offset.offset(0.5));
        assertThat(densityByState.get(new State("Alabama"))).isCloseTo(46.5d, Offset.offset(0.05));
    }
    // Hint:
    // <editor-fold defaultstate="collapsed">
    // You need to compute two element for each state: the sum of the population of its cities,
    // and the sum of their land areas. Then you need to divide these two values to get the density
    // of the population.
    // You can do that with a teeing collector, summing the populations on the one hand, summing the
    // land areas on the other, and computing the density in the end.
    // You can follow the previous patterns, creating factory methods to improve the readability of
    // your code.
    // </editor-fold>
}
