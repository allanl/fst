import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("MessageSelector Tests")
class MessageSelectorTest {

  private static final int FORWARD = MessageSelector.FORWARD;
  private static final int REVERSE = MessageSelector.REVERSE;
  private static final int RND_INNER = MessageSelector.RND_INNER;
  private static final int RND = MessageSelector.RND;
  private static final int JOIN = MessageSelector.JOIN;
  private static final int RANDOM = MessageSelector.RANDOM;
  private static final int SEQUENTIAL = MessageSelector.SEQUENTIAL;

  // EXAMPLE-BASED TESTS (Parameterized)
  // Document specific expected behaviors and known edge cases
  // ============================================================================

  @Test
  @DisplayName("Returns null when message list is empty")
  void testEmptyMessageList() {
    List<String> messages = new ArrayList<>();
    MessageSelector selector = new MessageSelector(messages, SEQUENTIAL, FORWARD, FORWARD);

    assertThat(selector.getNextMessage()).isNull();
  }

  @Test
  @DisplayName("Sequential order returns messages in order and wraps around")
  void testSequentialMessageOrder() {
    List<String> messages = Arrays.asList("First", "Second", "Third");
    MessageSelector selector = new MessageSelector(messages, SEQUENTIAL, FORWARD, FORWARD);

    assertThat(selector.getNextMessage()).isEqualTo("First");
    assertThat(selector.getNextMessage()).isEqualTo("Second");
    assertThat(selector.getNextMessage()).isEqualTo("Third");
    assertThat(selector.getNextMessage()).isEqualTo("First"); // wraps around
  }

  @Test
  @DisplayName("Random order returns all messages eventually")
  void testRandomMessageOrder() {
    List<String> messages = Arrays.asList("First", "Second", "Third");
    MessageSelector selector = new MessageSelector(messages, RANDOM, FORWARD, FORWARD);

    Set<String> seen = new HashSet<>();
    // Get enough messages to likely see all of them (with high probability)
    for (int i = 0; i < 50; i++) {
      String msg = selector.getNextMessage();
      assertThat(msg).isIn(messages);
      seen.add(msg);
    }

    assertThat(seen).containsExactlyInAnyOrderElementsOf(messages);
  }

  @ParameterizedTest(name = "Word order {0}: \"{1}\" -> \"{2}\"")
  @MethodSource("wordOrderTestCases")
  @DisplayName("Word ordering modes")
  void testWordOrdering(String modeName, String input, String expected, int mode) {
    String result = MessageSelector.orderMessage(input, mode, FORWARD);
    assertThat(result).isEqualTo(expected);
  }

  static Stream<Arguments> wordOrderTestCases() {
    return Stream.of(
        Arguments.of("FORWARD", "hello world test", "hello world test", FORWARD),
        Arguments.of("REVERSE", "hello world test", "test world hello", REVERSE),
        Arguments.of("JOIN", "hello world test", "hello world test", JOIN),
        Arguments.of("FORWARD single word", "hello", "hello", FORWARD),
        Arguments.of("REVERSE single word", "hello", "hello", REVERSE),
        Arguments.of("JOIN single word", "hello", "hello", JOIN));
  }

  @Test
  @DisplayName("Word RND_INNER keeps first and last words, shuffles middle")
  void testWordRndInner() {
    String input = "first second third fourth last";
    String result = MessageSelector.orderMessage(input, RND_INNER, FORWARD);

    String[] words = result.split(" ");
    assertThat(words[0]).isEqualTo("first");
    assertThat(words[words.length - 1]).isEqualTo("last");
    assertThat(words).containsExactlyInAnyOrder("first", "second", "third", "fourth", "last");
  }

  @Test
  @DisplayName("Word RND shuffles all words")
  void testWordRnd() {
    String input = "first second third fourth last";
    String result = MessageSelector.orderMessage(input, RND, FORWARD);

    String[] words = result.split(" ");
    assertThat(words).containsExactlyInAnyOrder("first", "second", "third", "fourth", "last");
  }

  @Test
  @DisplayName("Word RND_INNER with 3 or fewer words doesn't shuffle")
  void testWordRndInnerShortMessage() {
    assertThat(MessageSelector.orderMessage("one two three", RND_INNER, FORWARD))
        .isEqualTo("one two three");
    assertThat(MessageSelector.orderMessage("one two", RND_INNER, FORWARD)).isEqualTo("one two");
  }

  @ParameterizedTest(name = "Letter order {0}: \"{1}\" -> \"{2}\"")
  @MethodSource("letterOrderTestCases")
  @DisplayName("Letter ordering modes")
  void testLetterOrdering(String modeName, String input, String expected, int mode) {
    String result = MessageSelector.orderMessage(input, FORWARD, mode);
    assertThat(result).isEqualTo(expected);
  }

  static Stream<Arguments> letterOrderTestCases() {
    return Stream.of(
        Arguments.of("FORWARD", "hello", "hello", FORWARD),
        Arguments.of("REVERSE", "hello", "olleh", REVERSE),
        Arguments.of("FORWARD multi-word", "hello world", "hello world", FORWARD),
        Arguments.of("REVERSE multi-word", "hello world", "olleh dlrow", REVERSE),
        Arguments.of("REVERSE single char", "a", "a", REVERSE));
  }

  @Test
  @DisplayName("Letter RND_INNER keeps first and last letters, shuffles middle")
  void testLetterRndInner() {
    String input = "testing";
    String result = MessageSelector.orderMessage(input, FORWARD, RND_INNER);

    assertThat(result).startsWith("t");
    assertThat(result).endsWith("g");
    assertThat(result).hasSize(7);
    assertThat(result.chars().sorted().toArray())
        .isEqualTo(input.chars().sorted().toArray()); // same letters
  }

  @Test
  @DisplayName("Letter RND shuffles all letters")
  void testLetterRnd() {
    String input = "testing";
    String result = MessageSelector.orderMessage(input, FORWARD, RND);

    assertThat(result).hasSize(7);
    assertThat(result.chars().sorted().toArray())
        .isEqualTo(input.chars().sorted().toArray()); // same letters
  }

  @Test
  @DisplayName("Letter RND_INNER with 3 or fewer letters doesn't shuffle")
  void testLetterRndInnerShortWord() {
    assertThat(MessageSelector.orderMessage("cat", FORWARD, RND_INNER)).isEqualTo("cat");
    assertThat(MessageSelector.orderMessage("at", FORWARD, RND_INNER)).isEqualTo("at");
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("combinedOrderingTestCases")
  @DisplayName("Combined word and letter ordering")
  void testCombinedOrdering(
      String description, String input, String expected, int wordOrder, int letterOrder) {
    String result = MessageSelector.orderMessage(input, wordOrder, letterOrder);
    assertThat(result).isEqualTo(expected);
  }

  static Stream<Arguments> combinedOrderingTestCases() {
    return Stream.of(
        Arguments.of(
            "Word REVERSE + Letter REVERSE", "hello world", "dlrow olleh", REVERSE, REVERSE),
        Arguments.of("Word JOIN + Letter REVERSE", "hello world", "dlrow olleh", JOIN, REVERSE),
        Arguments.of(
            "Word REVERSE + Letter FORWARD", "hello world", "world hello", REVERSE, FORWARD));
  }

  @Test
  @DisplayName("Changing word order affects subsequent messages")
  void testChangeWordOrder() {
    List<String> messages = Arrays.asList("hello world");
    MessageSelector selector = new MessageSelector(messages, SEQUENTIAL, FORWARD, FORWARD);

    assertThat(selector.getNextMessage()).isEqualTo("hello world");

    selector.setWordOrder(REVERSE);
    assertThat(selector.getNextMessage()).isEqualTo("world hello");
  }

  @Test
  @DisplayName("Changing letter order affects subsequent messages")
  void testChangeLetterOrder() {
    List<String> messages = Arrays.asList("hello");
    MessageSelector selector = new MessageSelector(messages, SEQUENTIAL, FORWARD, FORWARD);

    assertThat(selector.getNextMessage()).isEqualTo("hello");

    selector.setLetterOrder(REVERSE);
    assertThat(selector.getNextMessage()).isEqualTo("olleh");
  }

  @Test
  @DisplayName("Changing message order from SEQUENTIAL to RANDOM works")
  void testChangeMessageOrder() {
    List<String> messages = Arrays.asList("First", "Second", "Third");
    MessageSelector selector = new MessageSelector(messages, SEQUENTIAL, FORWARD, FORWARD);

    assertThat(selector.getNextMessage()).isEqualTo("First");
    assertThat(selector.getNextMessage()).isEqualTo("Second");

    selector.setMessageOrder(RANDOM);
    String randomMsg = selector.getNextMessage();
    assertThat(randomMsg).isIn(messages);
  }

  @Test
  @DisplayName("Modifying message list affects future calls")
  void testMessageListModification() {
    List<String> messages = new ArrayList<>(Arrays.asList("First"));
    MessageSelector selector = new MessageSelector(messages, SEQUENTIAL, FORWARD, FORWARD);

    assertThat(selector.getNextMessage()).isEqualTo("First");

    messages.add("Second");
    assertThat(selector.getNextMessage()).isEqualTo("Second");
    assertThat(selector.getNextMessage()).isEqualTo("First"); // wraps around
  }

  @Test
  @DisplayName("setMessages replaces message list")
  void testSetMessages() {
    List<String> messages = Arrays.asList("First", "Second");
    MessageSelector selector = new MessageSelector(messages, SEQUENTIAL, FORWARD, FORWARD);

    assertThat(selector.getNextMessage()).isEqualTo("First");

    selector.setMessages(Arrays.asList("New1", "New2", "New3"));
    assertThat(selector.getNextMessage()).isEqualTo("New2");
    assertThat(selector.getNextMessage()).isEqualTo("New3");
    assertThat(selector.getNextMessage()).isEqualTo("New1"); // wraps around
  }

  @Test
  @DisplayName("Empty string message returns empty string")
  void testEmptyStringMessage() {
    List<String> messages = Arrays.asList("");
    MessageSelector selector = new MessageSelector(messages, SEQUENTIAL, FORWARD, FORWARD);

    assertThat(selector.getNextMessage()).isEqualTo("");
  }

  @Test
  @DisplayName("Message with multiple spaces preserved in FORWARD mode")
  void testMultipleSpaces() {
    String input = "hello  world";
    assertThat(MessageSelector.orderMessage(input, FORWARD, FORWARD)).isEqualTo("hello  world");
  }

  // ============================================================================
  // PROPERTY-BASED TESTS
  // Verify invariants hold for thousands of random inputs
  // Catches edge cases we didn't think of
  // ============================================================================

  @Property
  @Label("FORWARD mode is identity function")
  void forwardModeIsIdentity(@ForAll String input) {
    String result = MessageSelector.orderMessage(input, FORWARD, FORWARD);
    assertThat(result).isEqualTo(input);
  }

  @Property
  @Label("Ordering preserves all characters")
  void orderingPreservesCharacters(
      @ForAll String input,
      @ForAll @IntRange(min = 0, max = 4) int wordOrder,
      @ForAll @IntRange(min = 0, max = 3) int letterOrder) {

    Assume.that(!input.trim().isEmpty()); // Skip whitespace-only strings

    String result = MessageSelector.orderMessage(input, wordOrder, letterOrder);

    // Sort characters to compare content regardless of order
    char[] inputChars = input.toCharArray();
    char[] resultChars = result.toCharArray();
    java.util.Arrays.sort(inputChars);
    java.util.Arrays.sort(resultChars);

    assertThat(resultChars).isEqualTo(inputChars);
  }

  @Property
  @Label("Word REVERSE applied twice returns original")
  void wordReverseIsSelfInverse(@ForAll String input) {
    Assume.that(!input.trim().isEmpty()); // Skip whitespace-only strings

    String reversed = MessageSelector.orderMessage(input, REVERSE, FORWARD);
    String doubleReversed = MessageSelector.orderMessage(reversed, REVERSE, FORWARD);
    assertThat(doubleReversed).isEqualTo(input);
  }

  @Property
  @Label("Letter REVERSE applied twice returns original")
  void letterReverseIsSelfInverse(@ForAll String input) {
    Assume.that(!input.trim().isEmpty()); // Skip whitespace-only strings

    String reversed = MessageSelector.orderMessage(input, FORWARD, REVERSE);
    String doubleReversed = MessageSelector.orderMessage(reversed, FORWARD, REVERSE);
    assertThat(doubleReversed).isEqualTo(input);
  }

  @Property
  @Label("RND_INNER preserves first and last word for multi-word strings")
  void rndInnerPreservesWordBoundaries(@ForAll("multiWordStrings") String input) {
    String result = MessageSelector.orderMessage(input, RND_INNER, FORWARD);

    String[] inputWords = input.split(" ");
    String[] resultWords = result.split(" ");

    assertThat(resultWords).hasSize(inputWords.length);

    if (inputWords.length > 3) {
      assertThat(resultWords[0]).isEqualTo(inputWords[0]);
      assertThat(resultWords[resultWords.length - 1])
          .isEqualTo(inputWords[inputWords.length - 1]);
    }
  }

  @Provide
  Arbitrary<String> multiWordStrings() {
    return Arbitraries.strings()
        .withCharRange('a', 'z')
        .ofMinLength(1)
        .ofMaxLength(10)
        .list()
        .ofMinSize(4)
        .ofMaxSize(10)
        .map(words -> String.join(" ", words));
  }

  @Property
  @Label("RND_INNER preserves first and last letter for multi-letter words")
  void rndInnerPreservesLetterBoundaries(@ForAll("multiLetterWords") String input) {
    String result = MessageSelector.orderMessage(input, FORWARD, RND_INNER);

    assertThat(result).hasSize(input.length());
    if (input.length() > 3) {
      assertThat(result.charAt(0)).isEqualTo(input.charAt(0));
      assertThat(result.charAt(result.length() - 1)).isEqualTo(input.charAt(input.length() - 1));
    }
  }

  @Provide
  Arbitrary<String> multiLetterWords() {
    return Arbitraries.strings().withCharRange('a', 'z').ofMinLength(4).ofMaxLength(20);
  }

  @Property
  @Label("Sequential message order cycles through all messages")
  void sequentialOrderCycles(@ForAll("nonEmptyStringLists") List<String> messages) {
    MessageSelector selector = new MessageSelector(messages, SEQUENTIAL, FORWARD, FORWARD);

    Set<String> seen = new HashSet<>();
    // Get exactly messages.size() messages
    for (int i = 0; i < messages.size(); i++) {
      seen.add(selector.getNextMessage());
    }

    // Should have seen all unique messages
    assertThat(seen).containsAll(new HashSet<>(messages));

    // Next message should be the first one again (wrap around)
    assertThat(selector.getNextMessage()).isEqualTo(messages.get(0));
  }

  @Provide
  Arbitrary<List<String>> nonEmptyStringLists() {
    return Arbitraries.strings().alpha().ofMinLength(1).list().ofMinSize(1).ofMaxSize(20);
  }

  @Property
  @Label("Random message order always returns valid messages")
  void randomOrderReturnsValidMessages(@ForAll("nonEmptyStringLists") List<String> messages) {
    MessageSelector selector = new MessageSelector(messages, RANDOM, FORWARD, FORWARD);

    // Get 100 messages and verify all are from the original list
    for (int i = 0; i < 100; i++) {
      String msg = selector.getNextMessage();
      assertThat(messages).contains(msg);
    }
  }

  @Property
  @Label("Ordering never produces null for non-empty input")
  void orderingNeverProducesNull(
      @ForAll @StringLength(min = 1) String input,
      @ForAll @IntRange(min = 0, max = 4) int wordOrder,
      @ForAll @IntRange(min = 0, max = 3) int letterOrder) {

    String result = MessageSelector.orderMessage(input, wordOrder, letterOrder);
    assertThat(result).isNotNull();
  }
}
