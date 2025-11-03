import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Handles message selection and ordering logic. Encapsulates the complexity of selecting and
 * transforming messages.
 */
public class MessageSelector {
  // Word/Letter ordering modes
  public static final int FORWARD = 0;
  public static final int REVERSE = 1;
  public static final int RND_INNER = 2;
  public static final int RND = 3;
  public static final int JOIN = 4;

  // Message ordering modes
  public static final int RANDOM = 0;
  public static final int SEQUENTIAL = 1;

  private volatile List<String> messages;
  private volatile int messageOrder;
  private volatile int wordOrder;
  private volatile int letterOrder;
  private int position = -1;

  public MessageSelector(List<String> messages, int messageOrder, int wordOrder, int letterOrder) {
    this.messages = messages;
    this.messageOrder = messageOrder;
    this.wordOrder = wordOrder;
    this.letterOrder = letterOrder;
  }

  /**
   * Updates the list of messages.
   *
   * @param messages the new message list
   */
  public void setMessages(List<String> messages) {
    this.messages = messages;
  }

  /**
   * Updates the message order mode.
   *
   * @param messageOrder RANDOM or SEQUENTIAL
   */
  public void setMessageOrder(int messageOrder) {
    this.messageOrder = messageOrder;
  }

  /**
   * Updates the word order mode.
   *
   * @param wordOrder word ordering mode
   */
  public void setWordOrder(int wordOrder) {
    this.wordOrder = wordOrder;
  }

  /**
   * Updates the letter order mode.
   *
   * @param letterOrder letter ordering mode
   */
  public void setLetterOrder(int letterOrder) {
    this.letterOrder = letterOrder;
  }

  /**
   * Gets the next message according to selection and ordering rules.
   *
   * @return the next message, or null if no messages available
   */
  public String getNextMessage() {
    if (messages.isEmpty()) {
      return null;
    }

    int index = selectIndex();
    String message = messages.get(index);
    return orderMessage(message, wordOrder, letterOrder);
  }

  private int selectIndex() {
    if (messageOrder == RANDOM) {
      return (int) (Math.random() * messages.size());
    } else {
      position = (position + 1) % messages.size();
      return position;
    }
  }

  /**
   * Orders a message according to word and letter ordering rules. Static helper for use outside of
   * message selection (e.g., sample preview).
   *
   * @param msg the message to order
   * @param wordOrder word ordering mode
   * @param letterOrder letter ordering mode
   * @return ordered message
   */
  public static String orderMessage(String msg, int wordOrder, int letterOrder) {
    if (wordOrder == FORWARD && letterOrder == FORWARD) {
      return msg;
    }

    List<String> words;
    if (wordOrder == JOIN) {
      words = new ArrayList<>(1);
      words.add(msg);
    } else {
      words = Arrays.asList(msg.split(" "));
    }

    orderList(words, wordOrder);

    if (letterOrder != FORWARD) {
      for (int i = 0; i < words.size(); i++) {
        char[] chars = words.get(i).toCharArray();
        List<Character> letters = new ArrayList<>(chars.length);
        for (Character c : chars) {
          letters.add(c);
        }
        orderList(letters, letterOrder);
        words.set(i, join(letters, ""));
      }
    }

    return join(words, " ");
  }

  private static void orderList(List list, int mode) {
    if (list.size() > 1) {
      switch (mode) {
        case REVERSE:
          Collections.reverse(list);
          break;
        case RND:
          Collections.shuffle(list);
          break;
        case RND_INNER:
          if (list.size() > 3) {
            Collections.shuffle(list.subList(1, list.size() - 1));
          }
          break;
        default:
          break;
      }
    }
  }

  private static String join(Collection collection, String delimiter) {
    StringBuilder buffer = new StringBuilder();
    Iterator iter = collection.iterator();
    if (iter.hasNext()) {
      buffer.append(iter.next());
      while (iter.hasNext()) {
        buffer.append(delimiter);
        buffer.append(iter.next());
      }
    }
    return buffer.toString();
  }
}
