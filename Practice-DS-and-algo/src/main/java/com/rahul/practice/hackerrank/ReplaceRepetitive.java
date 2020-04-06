package com.rahul.practice.hackerrank;

/**
 * In this challenge, we use regular expressions (RegEx) to remove instances of words that are
 * repeated more than once, but retain the first occurrence of any case-insensitive repeated word.
 * For example, the words love and to are repeated in the sentence I love Love to To tO code. Can
 * you complete the code in the editor so it will turn I love Love to To tO code into I love to
 * code?
 *
 * <p>To solve this challenge, complete the following three lines:
 *
 * <p>Write a RegEx that will match any repeated word. Complete the second compile argument so that
 * the compiled RegEx is case-insensitive. Write the two necessary arguments for replaceAll such
 * that each repeated word is replaced with the very first instance the word found in the sentence.
 * It must be the exact first occurrence of the word, as the expected output is case-sensitive.
 *
 * <p>Input:
 *
 * <ul>
 *   <li>5
 *   <li>Goodbye bye bye world world world
 *   <li>Sam went went to to to his business
 *   <li>Reya is is the the best player in eye eye game
 *   <li>in inthe
 *   <li>Hello hello Ab aB
 * </ul>
 *
 * Output:
 *
 * <ul>
 *   <li>Goodbye bye world
 *   <li>Sam went to his business
 *   <li>Reya is the best player in eye game
 *   <li>in inthe
 *   <li>Hello Ab
 * </ul>
 */
public class ReplaceRepetitive {}
