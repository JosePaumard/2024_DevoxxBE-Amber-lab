package org.paumard.hol.textblock;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

public class A_SimpleStringBlock {

    private String sonnet =
            "From fairest creatures we desire increase,\n" +
            "That thereby beauty's rose might never die,\n" +
            "But as the riper should by time decease,\n" +
            "His tender heir might bear his memory.\n" +
            "But thou, contracted to thine own bright eyes,\n" +
            "Feed'st thy light's flame with self-substantial fuel,\n" +
            "Making a famine where abundance lies,\n" +
            "Thy self thy foe, to thy sweet self too cruel.\n" +
            "Thou that art now the world's fresh ornament,\n" +
            "And only herald to the gaudy spring,\n" +
            "Within thine own bud buriest thy content,\n" +
            "And, tender churl, mak'st waste in niggarding.\n" +
            "    Pity the world, or else this glutton be,\n" +
            "    To eat the world's due, by the grave and thee.\n";


    /**
     * Write the line String as a text block, without changing the content of the string.
     * Your IDE may help you if you need it.
     * What does it do if you forget the line feed after the opening triple double quote?
     */
    @Test
    public void a_text_block01() {

        assertThat(sonnet).isEqualTo(
                "From fairest creatures we desire increase,\n" +
                "That thereby beauty's rose might never die,\n" +
                "But as the riper should by time decease,\n" +
                "His tender heir might bear his memory.\n" +
                "But thou, contracted to thine own bright eyes,\n" +
                "Feed'st thy light's flame with self-substantial fuel,\n" +
                "Making a famine where abundance lies,\n" +
                "Thy self thy foe, to thy sweet self too cruel.\n" +
                "Thou that art now the world's fresh ornament,\n" +
                "And only herald to the gaudy spring,\n" +
                "Within thine own bud buriest thy content,\n" +
                "And, tender churl, mak'st waste in niggarding.\n" +
                "   Pity the world, or else this glutton be,\n" +
                "   To eat the world's due, by the grave and thee.");
    }
    // Hint:
    // <editor-fold defaultstate="collapsed">
    // There is no trick there: a text block starts with a triple double quote,
    // immediately followed by a line feed.
    // It should close with another triple double quote.
    // </editor-fold>

    /**
     * Write the code to count the lines of this sonnet.
     */
    @Test
    @Disabled
    public void a_text_block02() {

        long lineNumber = 0L;

        assertThat(lineNumber).isEqualTo(14);
    }
    // Hint:
    // <editor-fold defaultstate="collapsed">
    // You can stream the lines of a string with the String.lines() method.
    // </editor-fold>

    /**
     * Change the sonnet text block so that each line starts with at least one blank
     * space.
     */
    @Test
    @Disabled
    public void a_text_block03() {

        boolean allLinesStartWithBlank =
                sonnet.lines()
                        .allMatch(line -> Character.isWhitespace(line.charAt(0)));

        assertThat(allLinesStartWithBlank).isTrue();
    }
    // Hint:
    // <editor-fold defaultstate="collapsed">
    // By moving the closing triple double quote to the left of the leftmost character
    // of the text block, you can move the position of the last incidental white space.
    // You can use this trick to add blank spaces at the beginning of each line.
    // </editor-fold>

    /**
     * Change the sonnet text block so that all the line feed at the end of each line
     * are ignored.
     */
    @Test
    @Disabled
    public void a_text_block04() {

        long countLines =
                sonnet.lines().count();

        assertThat(countLines).isEqualTo(1L);
    }
    // Hint:
    // <editor-fold defaultstate="collapsed">
    // Adding a backslash character at the end of a line of a text block removes
    // the line feed for this line.
    // </editor-fold>

    /**
     * Change the sonnet text block so that all the lines have the same length,
     * adding blank spaces at the end of them, if needed.
     */
    @Test
    @Disabled
    public void a_text_block05() {

        long countLengthOfLines =
                sonnet.lines()
                        .mapToInt(String::length)
                        .distinct()
                        .count();

        assertThat(countLengthOfLines).isEqualTo(1L);
    }
    // Hint:
    // <editor-fold defaultstate="collapsed">
    // Adding \s at the end of a line forces to keep the trailing blank spaces.
    // </editor-fold>

    /**
     * Change the sonnet text block so that all the lines have the same length 55,
     * start with at least one blank space, and end with at least one blank space.
     */
    @Test
    @Disabled
    public void a_text_block06() {

        Predicate<String> allMatch =
                line -> line.length() == 55 &&
                        Character.isWhitespace(line.charAt(0)) &&
                        Character.isWhitespace(line.charAt(54));

        boolean allLinesMatch =
                sonnet.lines().allMatch(allMatch);

        assertThat(allLinesMatch).isTrue();
    }
    // Hint:
    // <editor-fold defaultstate="collapsed">
    // Combine all the previous elements to build this text block.
    // Remember that \s translates to a single space. It thus counts as one character.
    // </editor-fold>
}
