package org.paumard.hol.record;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class B_LessSimpleRecord {

    /**
     * The content of this class has just been added to make the code
     * compile. All this content should be removed when you make this class a record.
     */
    class Range {
        Range(int begin, int end) {
        }

        Range(int end) {
        }

        int begin() {
            return -1;
        }

        int end() {
            return -1;
        }
    }


    /// Rewrite the Range class as a Record. Remove its constructors, `begin()` and `end()`
    /// methods from it.
    /// Uncomment the call of the constructor and make the test pass.
    @Test
    @Disabled
    public void b_record01() {

        Range range = new Range(10, 20);

        assertThat(range.begin()).isEqualTo(10);
        assertThat(range.end()).isEqualTo(20);
    }
    // Hint:
    // <editor-fold defaultstate="collapsed">
    // Replace the existing Range class with a record.  
    // </editor-fold>


    /// Add a constructor that only takes `end` as a parameter
    /// and that initializes it with 0.
    ///
    /// Uncomment the call of the constructor and make the test pass.
    @Test
    @Disabled
    public void b_record02() {

        Range range = new Range(10);

        assertThat(range.begin()).isEqualTo(0);
        assertThat(range.end()).isEqualTo(10);
    }
    // Hint:
    // <editor-fold defaultstate="collapsed">
    // Remember that the constructors of a record should
    // always call the canonical constructor.  
    // </editor-fold>



    /// A valid range should have a `begin` value lesser than
    /// its end value. Write a validation rule to enforce this
    /// property.
    @Test
    @Disabled
    public void b_record03() {

        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Range(20, 10));
    }
    // Hint:
    // <editor-fold defaultstate="collapsed">
    // You can add this validation rule either in the canonical 
    // constructor, or in the compact constructor.
    // </editor-fold>



    /// Let us make this Range iterable! Add the `implements Iterable<Integer>`
    /// to the record declaration.
    /// For your code to compile, you just need to add a single method:
    /// `iterator()`. This method should return an `Iterator<Integer>`.
    ///
    /// You can then uncomment the foreach in the following code
    /// to make the test pass.
    @Test
    @Disabled
    public void b_record04() {

        Range range = new Range(0, 5);
        List<Integer> indexes = new ArrayList<>();
        // TODO: uncomment the following lines
//        for (int index : range) {
//            indexes.add(index);
//        }

        assertThat(indexes).containsExactly(0, 1, 2, 3, 4);
    }
    // Hint:
    // <editor-fold defaultstate="collapsed">
    // You can implement this iterator with an anonymous class.
    // </editor-fold>
}
