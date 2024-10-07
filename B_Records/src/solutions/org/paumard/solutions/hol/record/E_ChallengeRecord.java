package org.paumard.solutions.hol.record;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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


    record City(int id, String name, State state, Population population, LandArea landArea) {

        public static City of(String line) {

            // 1;New York;New York;8 336 817;780,9
            var elements = line.split(";");
            var id = Integer.parseInt(elements[0]);
            var name = elements[1];
            var state = new State(elements[2]);
            var population =
                    new Population(Integer.parseInt(elements[3].replaceAll(" ", "")));
            var surface =
                    new LandArea(Double.parseDouble(elements[4].replaceAll("[ ,]", "")));
            return new City(id, name, state, population, surface);
        }

        public double density() {
            return this.population.amount / this.landArea.amount();
        }

        public static Collector<City, ?, Population> summingPopulation() {
            return Collectors.mapping(City::population,
                    Collectors.reducing(new Population(0), Population.add()));
        }

        public static Collector<City, ?, LandArea> summingLandArea() {
            return Collectors.mapping(City::landArea,
                    Collectors.reducing(new LandArea(0d), LandArea.add()));
        }
    }

    record State(String name) {
    }

    record Population(int amount) implements Comparable<Population> {
        @Override
        public int compareTo(Population other) {
            return Integer.compare(this.amount(), other.amount());
        }

        public static BinaryOperator<Population> add() {
            return (population1, population2) ->
                    new Population(population1.amount() + population2.amount());
        }

        public double density(LandArea landArea) {
            return this.amount / landArea.amount();
        }
    }

    record LandArea(double amount) {

        public static BinaryOperator<LandArea> add() {
            return (landArea1, landArea2) ->
                    new LandArea(landArea1.amount() + landArea2.amount());
        }
    }

    record PopulatedState(State state, Population population) {
        PopulatedState(Map.Entry<State, Population> entry) {
            this(entry.getKey(), entry.getValue());
        }

        public static PopulatedState of(Map.Entry<State, List<City>> entry) {
            return new PopulatedState(entry.getKey(),
                    entry.getValue().stream().map(City::population).reduce(new Population(0), Population.add()));
        }

        public static Comparator<? super PopulatedState> comparingByPopulation() {
            return Comparator.comparing(PopulatedState::population);
        }
    }

    record Cities(List<City> cities) {
        Cities(List<City> cities) {
            this.cities = List.copyOf(cities);
        }

        public static Cities from(List<City> cities) {
            return new Cities(cities);
        }

        public List<City> cities() {
            return List.copyOf(cities);
        }

        public CityByState groupedByState() {
            return new CityByState(
                    cities.stream().collect(Collectors.groupingBy(City::state))
            );
        }
    }

    record CityByState(Map<State, List<City>> cityByState) {
        CityByState(Map<State, List<City>> cityByState) {
            this.cityByState = Map.copyOf(cityByState);
        }

        public Map<State, List<City>> cityByState() {
            return Map.copyOf(cityByState);
        }

        public PopulatedState getMostPopulatedCity() {
            return cityByState.entrySet().stream()
                    .map(PopulatedState::of)
                    .max(PopulatedState.comparingByPopulation())
                    .orElseThrow();
        }

        public PopulatedState getLeastPopulatedCity() {
            return cityByState.entrySet().stream()
                    .map(PopulatedState::of)
                    .min(PopulatedState.comparingByPopulation())
                    .orElseThrow();
        }
    }

    /**
     * Make this <code>City</code> class a record. Create a static method that takes a line,
     * analyzes it and create the city instance with the arguments from this line.
     */
    @Test
    public void e_record01() {

        City city = City.of(line); // TODO

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
    public void e_record02() {

        Path path = Path.of("files/cities.csv");

        try (var lines = Files.newBufferedReader(path).lines()) {
            cities = lines.skip(2).map(City::of).toList();
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
    public void e_record03() {

        states = cities.stream()
                .map(City::state)
                .distinct().toList();

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
    public void e_record05() {

        Population totalPopulation = cities.stream()
                .map(City::population)
                .reduce(new Population(0),
                        Population.add());

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
    public void e_record06() {

        populationByState =
                cities.stream()
                        .collect(Collectors.groupingBy(
                                City::state,
                                City.summingPopulation()
                        ));

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
    public void e_record07() {

        PopulatedState mostPopulatedState =
                populationByState.entrySet().stream()
                        .map(PopulatedState::new)
                        .max(PopulatedState.comparingByPopulation())
                        .orElseThrow();

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
    public void e_record08() {

        PopulatedState mostPopulatedState =
                Cities.from(cities)
                        .groupedByState()
                        .getMostPopulatedCity();
        PopulatedState leastPopulatedState =
                Cities.from(cities)
                        .groupedByState()
                        .getLeastPopulatedCity();

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
    public void e_record09() {

        City mostDenseCity =
                cities.stream()
                        .max(Comparator.comparing(City::density))
                        .orElseThrow();
        City leastDenseCity =
                cities.stream()
                        .min(Comparator.comparing(City::density))
                        .orElseThrow();

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
    public void e_record10() {

        Map<State, Double> densityByState =
                cities.stream()
                        .collect(Collectors.groupingBy(
                                City::state,
                                Collectors.teeing(
                                        City.summingPopulation(),
                                        City.summingLandArea(),
                                        Population::density
                                )
                        ));

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
