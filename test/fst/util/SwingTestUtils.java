import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 * Utility class for testing Swing components.
 * Provides helper methods for working with Swing components in tests.
 */
public class SwingTestUtils {

    /**
     * Runs the specified Runnable on the EDT and waits for it to complete.
     *
     * @param runnable The Runnable to execute on the EDT
     * @throws Exception If an error occurs during execution
     */
    public static void invokeAndWait(Runnable runnable) throws Exception {
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            final CountDownLatch latch = new CountDownLatch(1);
            final Exception[] exceptions = new Exception[1];

            SwingUtilities.invokeLater(() -> {
                try {
                    runnable.run();
                } catch (Exception e) {
                    exceptions[0] = e;
                } finally {
                    latch.countDown();
                }
            });

            if (!latch.await(5, TimeUnit.SECONDS)) {
                throw new RuntimeException("Timeout waiting for EDT task to complete");
            }

            if (exceptions[0] != null) {
                throw exceptions[0];
            }
        }
    }

    /**
     * Finds a component of the specified type with the given name in the container.
     *
     * @param <T> The component type
     * @param container The container to search in
     * @param type The class of the component to find
     * @param name The name of the component to find
     * @return The found component, or null if not found
     */
    public static <T extends Component> T findComponentByName(Container container, Class<T> type, String name) {
        for (Component component : container.getComponents()) {
            if (type.isInstance(component) && name.equals(component.getName())) {
                return type.cast(component);
            }

            if (component instanceof Container) {
                T found = findComponentByName((Container) component, type, name);
                if (found != null) {
                    return found;
                }
            }
        }

        return null;
    }

    /**
     * Finds all components of the specified type in the container.
     *
     * @param <T> The component type
     * @param container The container to search in
     * @param type The class of the components to find
     * @return A list of found components
     */
    public static <T extends Component> List<T> findComponents(Container container, Class<T> type) {
        List<T> result = new ArrayList<>();
        findComponents(container, type, result);
        return result;
    }

    private static <T extends Component> void findComponents(Container container, Class<T> type, List<T> result) {
        for (Component component : container.getComponents()) {
            if (type.isInstance(component)) {
                result.add(type.cast(component));
            }

            if (component instanceof Container) {
                findComponents((Container) component, type, result);
            }
        }
    }

    /**
     * Clicks a button with the specified name in the container.
     *
     * @param container The container to search in
     * @param buttonName The name of the button to click
     * @throws Exception If the button is not found or cannot be clicked
     */
    public static void clickButton(Container container, String buttonName) throws Exception {
        JButton button = findComponentByName(container, JButton.class, buttonName);
        if (button == null) {
            throw new RuntimeException("Button not found: " + buttonName);
        }

        invokeAndWait(() -> {
            if (button.isEnabled()) {
                button.doClick();
            } else {
                throw new RuntimeException("Button is disabled: " + buttonName);
            }
        });
    }

    /**
     * Waits for the specified component to become visible.
     *
     * @param component The component to wait for
     * @param timeoutSeconds The maximum time to wait in seconds
     * @return true if the component became visible within the timeout, false otherwise
     * @throws InterruptedException If the thread is interrupted while waiting
     */
    public static boolean waitForComponentVisible(Component component, int timeoutSeconds) throws InterruptedException {
        long endTime = System.currentTimeMillis() + (timeoutSeconds * 1000);

        while (System.currentTimeMillis() < endTime) {
            if (component.isVisible()) {
                return true;
            }
            Thread.sleep(100);
        }

        return component.isVisible();
    }

    /**
     * Disposes all windows created during the test.
     * Call this in your test tearDown method to clean up.
     */
    public static void disposeAllWindows() {
        invokeOnEDT(() -> {
            for (Window window : Window.getWindows()) {
                window.dispose();
            }
        });
    }

    private static void invokeOnEDT(Runnable runnable) {
        try {
            if (SwingUtilities.isEventDispatchThread()) {
                runnable.run();
            } else {
                SwingUtilities.invokeAndWait(runnable);
            }
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException("Error executing on EDT", e);
        }
    }
}
