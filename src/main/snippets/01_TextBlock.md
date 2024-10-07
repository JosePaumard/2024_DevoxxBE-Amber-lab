Using the Dev.Java Playground
=============================

## Text Blocks

You can also follow this lab using the Dev.java Playground that you can find here: https://dev.java/playground/

To follow the lab, just copy the code and paste it in the Playground. Then you can modify it and run it by clicking the Run button. You don't need any IDE, any Git, any fancy tool, just a plain browser. Note that the Playground does not remember anything from one run to the other. So you need to run all the code you need, every time you run something. This is especially true for your variable declarations. 

First, copy and paste the following in the playground. It just creates a string of characters named `sonnet`, that you are going to use.

Start by copying and pasting the following code in the Dev.java Playground. Note the syntax used to write this string of characters. This is a Text Block. It should start with a triple `"` character, immediately followed by a line feed. It is then closed by another triple `"`. 

```java
var text = """
	One Two
	Three Four
	Five Six.""";

System.out.println("Text length = " + text.length());
```

If you click on the Run button, you should get the following message: 

```shell
Text length = 28
```

Congratulations, everything is working as expected! You can see how easy it is to execute some Java code in this Playground.

### Getting the Number of Lines 

First, modify the variable `numberOfLines` to compute the number of lines of this text block. You may take a look at the `lines()` method of the `String` class. 

```java
var text = """
        One Two
        Three Four
        Five Six.""";
var numberOfLines = 0L;
System.out.println("Number of lines = " + numberOfLines);
```

You can run the previous code in the Playground. Once you have modified the text block declaration, the result should be 3.


### Starting Text Block Lines With a Blank Character

The first character of each line of a text block is fixed by the position of the closing `"""`. If this marker is at the end of the last line of the text block, then the first character of each line is given by the leftmost non-blank character of the text block lines. 

But you can also put this closing marker on the next line, after the last line of your text block. In that case, its position gives the position of the first character of the lines. 

You may want to experiment with that to see what are the characters of the lines of your text block.  

```java
var text = """
        One Two
        Three Four
        Five Six.""";
var allLinesStartWithBlank =
        text.lines()
            .allMatch(line -> Character.isWhitespace(line.charAt(0)));
System.out.println("All lines start with blank = " + allLinesStartWithBlank);
```

You can run the previous code in the Playground. Once you have modified the text block declaration, the result should be true.


### Ignoring the Final Line Feed

By default each line of your text block ends with a line feed. You can prevent that by ending a line with a backslash character `\`. This will prevent the line feed of this line to be added to your text block. Note that you cannot add any character after this backslash.

```java
var text = """
        One Two
        Three Four
        Five Six.""";
var countLines = text.lines().count();
System.out.println("Number of lines = " + countLines);
```

You can run the previous code in the Playground. Once you have modified the text block declaration, the result should be 1. 


### Fixing each Line Length

You know how to set the first character of a line by positioning the closing marker `"""`, and you know how to prevent any line feed to be added at the end of a line. You can also set the position of the last character by using the following marker at the end of a line: `\s`. By default, all the trailing blank characters are removed from each line. If you place this character at the end of a line, 

```java
var text = """
        One Two
        Three Four
        Five Six.""";
var countLengthOfLines =
        text.lines()
            .mapToInt(String::length)
            .distinct()
            .count();
System.out.println("Number of distinct end of lines = " + countLengthOfLines);
```

You can run the previous code in the Playground. Once you have modified the text block declaration, the result should be 1.

### Fixing each Line Length to 55, Starting and Ending With at Least One Blank Character

Now, just modify the following code so that: 
- the first and the last character of each line is a blak character
- each line is exactly 14 characters long. 


```java
var text = """
         One, Two
         Three, Four
         Five, Six.""";
Predicate<String> allMatch =
        line -> line.length() == 14 &&
                Character.isWhitespace(line.charAt(0)) &&
                Character.isWhitespace(line.charAt(13));
var allLinesMatch = text.lines().allMatch(allMatch);
System.out.println("All lines are 12 chars  = " + allLinesMatch);
```

You can run the previous code in the Playground. Once you have modified the text block declaration, the result should be true. 
