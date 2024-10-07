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
public class D_HardRecord {

    /**
     * These exercises come with a CSV file, files/cities.csv, which contains information
     * about some US cities.
     * Each line represent a city, with the following fields:
     * - 37 is the ID of the city,
     * - Atlanta is the name,
     * - Georgia the name of the state of this city,
     * - 506 811 is the population of Atlanta, according to this file,
     * - 345,8 is the land area of this city, according to this file.
     */
    String line = "37;Atlanta;Georgia;506 811;345,8";

    static class City {

        City(int id, String name, String state, int population, double landArea) {
        }

        public int id() {
            return -1;
        }

        public String name() {
            return "";
        }

        public String state() {
            return "";
        }

        public int population() {
            return -1;
        }

        public double landArea() {
            return -1d;
        }

        public static City of(String line) {

            // 1;New York;New York;8 336 817;780,9
            return new City(-1, "", "", -1, 0d);
        }
    }

    static class State {
        State(String name) {
        }

        public String name() {
            return "";
        }
    }

    /**
     * Make this <code>City</code> class a record. Create a static method that takes a line,
     * analyzes it and create the city instance with the arguments from this line.
     */
    @Test
    @Disabled
    public void d_record01() {

        City city = City.of(line);

        assertThat(city.id()).isEqualTo(37);
        assertThat(city.name()).isEqualTo("Atlanta");
        assertThat(city.state()).isEqualTo("Georgia");
        assertThat(city.population()).isEqualTo(506_811);
        assertThat(city.landArea()).isCloseTo(3458, Offset.offset(0.001));
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
    public void d_record02() {

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
    public void d_record03() {

        states = List.of(); // TODO: replace this code with the correct one

        assertThat(cities).hasSize(317);
        assertThat(states).hasSize(46);
    }
    // Hint:
    // <editor-fold defaultstate="collapsed">
    // You can use the Stream API to create the list of states.
    // </editor-fold>


    /**
     * Sort the list of states in the alphabetical order by making them comparable.
     */
    @Test
    @Disabled
    public void d_record04() {

        states = new ArrayList<>(states);
//        states.sort(Comparator.naturalOrder()); // TODO: uncomment this line

        assertThat(states).hasSize(46);
        assertThat(states.get(0).name()).isEqualTo("Alabama");
        assertThat(states.get(12).name()).isEqualTo("Illinois");
        assertThat(states.get(24).name()).isEqualTo("Montana");
        assertThat(states.get(45).name()).isEqualTo("Wisconsin");
    }
    // Hint:
    // <editor-fold defaultstate="collapsed">
    // You can use the <code>sort()</code> method defined on <code>List</code>.
    // </editor-fold>


    /**
     * Find the largest city, population wise.
     */
    @Test
    @Disabled
    public void d_record05() {

        City largestCity = null; // TODO: find the city with the highest population

        assertThat(largestCity.name()).isEqualTo("New York");
        assertThat(largestCity.population()).isEqualTo(8_336_817);
    }
    // Hint:
    // <editor-fold defaultstate="collapsed">
    // You can sum the elements of a stream as long as they are numbers.
    // You have a <code>mapToIng()</code> method on the Stream API that return
    // an <code>IntStream</code>. There is a <code>sum()</code> method on
    // <code>IntStream</code>.
    // </editor-fold>


    /**
     * Compute the total population living in these cities.
     */
    @Test
    @Disabled
    public void d_record06() {

        int population = 0; // TODO: compute the total population of all the cities

        assertThat(population).isEqualTo(88_231_877);
    }
    // Hint:
    // <editor-fold defaultstate="collapsed">
    // You can sum the elements of a stream as long as they are numbers.
    // You have a <code>mapToIng()</code> method on the Stream API that return
    // an <code>IntStream</code>. There is a <code>sum()</code> method on
    // <code>IntStream</code>.
    // </editor-fold>


    /// Create a histogram of the states and the population of this state. this
    /// histogram is a `Map<String, Integer>`, where the keys are the
    /// name of the states, and the values the sum of the population of the
    /// cities belonging to that state.

    private Map<String, Integer> populationByState = new HashMap<>();

    @Test
    @Disabled
    public void d_record07() {

        populationByState = null; // TODO: create the correct map

        assertThat(populationByState).hasSize(46);
        assertThat(populationByState.get("Georgia")).isEqualTo(1_434_456);
        assertThat(populationByState.get("New York")).isEqualTo(8_960_160);
    }
    // Hint:
    // <editor-fold defaultstate="collapsed">
    // You can create histograms by using the <code>collect()</code> method
    // of the Stream API, and the <code>Collectors.groupingBy()</code> collector.
    // This collector takes a classifier (a function) as a parameter, and another
    // collector, called a downstream collector, used to process the values bound
    // to the same key. You can then use the <code>Collectors.summingInt()</code>
    // as a downstream collector to solve the exercise.
    // </editor-fold>


    /**
     * From the previous histogram, find the state that has the largest population.
     */
    @Test
    @Disabled
    public void d_record08() {

        Map.Entry<String, Integer> mostPopulatedState = null; // TODO: find the max

        assertThat(mostPopulatedState.getKey()).isEqualTo("California");
        assertThat(mostPopulatedState.getValue()).isEqualTo(18_976_671);
    }
    // Hint:
    // <editor-fold defaultstate="collapsed">
    // Finding the max of a stream can be done by just calling <code>max()</code>
    // and passing the right comparator. You cannot stream a map, but you can
    // stream its key / value pairs by streaming its entry set.
    // You can create comparators of map entries directly with the factory methods
    // of the <code>Map.Entry</code> interface.
    // </editor-fold>
}
